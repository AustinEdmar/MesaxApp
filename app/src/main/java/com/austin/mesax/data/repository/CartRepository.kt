package com.austin.mesax.data.repository

import android.util.Log
import com.austin.mesax.data.api.OrdersApi
import com.austin.mesax.data.local.dao.CartDao
import com.austin.mesax.data.local.entity.CartItemEntity
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.model.OrderResquest.AddItemRequest
import com.austin.mesax.worker.CartSyncScheduler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepository @Inject constructor (
    private val api: OrdersApi,
    private val cartDao: CartDao,
    private val scheduler: CartSyncScheduler
){
    private val syncMutex = Mutex()

    private val _navigationEvent = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent = _navigationEvent.asSharedFlow()

    suspend fun addToCart(orderId: Int, product: ProductEntity) {
        cartDao.addToCartTransaction(orderId, product)
        scheduler.schedule()
    }

    suspend fun increaseQuantity(item: CartItemEntity) {
        cartDao.increaseQuantityTransaction(item)
        scheduler.schedule()
    }

    suspend fun decreaseQuantity(item: CartItemEntity) {
        cartDao.decreaseQuantityTransaction(item)
        scheduler.schedule()
    }

    suspend fun syncCart(): Boolean = syncMutex.withLock {
        Log.d("REPO", "Iniciando syncCart")
        var hasError = false
        
        while (true) {
            val items = cartDao.getPendingItems()
            if (items.isEmpty()) break
            Log.d("REPO", "Items pendentes: ${items.size}")


            for (item in items) {
                val deltaToSync = item.delta
                if (deltaToSync == 0) {
                    cartDao.update(item.copy(pendingSync = false))
                    continue
                }

                try {
                    // 1. Zera o delta localmente ANTES de enviar para a API (Otimismo Preventivo)
                    // Isso evita duplicidade se o Worker sofrer um retry inesperado ou crash.
                    cartDao.update(item.copy(delta = 0, pendingSync = false))

                    val response = try {
                        if (deltaToSync > 0) {
                            api.addItem(
                                item.orderId,
                                AddItemRequest(item.productId, deltaToSync)
                            )
                        } else {
                            api.decrementItem(
                                item.orderId,
                                AddItemRequest(item.productId, kotlin.math.abs(deltaToSync))
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("REPO", "Erro na chamada de API", e)
                        // 2. Erro de rede/conexão: Devolve o delta para tentar novamente no futuro
                        rollbackDelta(item, deltaToSync)
                        throw e
                    }
                    Log.d("REPO", "Resposta da API: ${response.code()}")

                    if (!response.isSuccessful) {
                        Log.e("SYNC", "API Error: ${response.code()} - ${response.message()}")
                        // 3. Erro na API (Ex: estoque insuficiente no server): Devolve o delta
                        rollbackDelta(item, deltaToSync)
                        hasError = true
                        continue
                    }

                    // 4. Sucesso: Limpeza final
                    withContext(NonCancellable) {
                        val current = cartDao.getItem(item.orderId, item.productId) ?: return@withContext
                        if (current.quantity <= 0 && current.delta == 0) {
                            cartDao.deleteItem(current.id, current.orderId)
                            cartDao.deleteProductIfNotInCart(current.productId)
                            cartDao.deleteOrderIfNotInCart(current.orderId)
                        }
                    }

                } catch (e: Exception) {
                    Log.e("SYNC", "Falha crítica ao sincronizar item ${item.id}", e)
                    hasError = true
                }
            }
            if (hasError) break
        }

        return !hasError
    }

    private suspend fun rollbackDelta(item: CartItemEntity, delta: Int) {
        val current = cartDao.getItem(item.orderId, item.productId)
        if (current != null) {
            cartDao.update(current.copy(
                delta = current.delta + delta,
                pendingSync = true
            ))
        } else {
            // Se o item sumiu (raro), recria com o delta pendente
            cartDao.insert(item.copy(id = 0, delta = delta, pendingSync = true))
        }
    }

    fun observeCart(orderId: Int?) =
        cartDao.getCart(orderId)
}
