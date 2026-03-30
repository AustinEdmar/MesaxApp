package com.austin.mesax.data.repository

import com.austin.mesax.data.api.OrdersApi
import com.austin.mesax.data.local.dao.CartDao
import com.austin.mesax.data.local.dao.OrderDao
import com.austin.mesax.data.local.entity.CartItemEntity
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.local.mapper.toEntity
import com.austin.mesax.data.model.OrderResquest.AddItemRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class CartRepository @Inject constructor (
    private val api: OrdersApi,

    private val cartDao: CartDao
){

    private val syncMutex = Mutex()
    private val cartMutex = Mutex()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()



    suspend fun addToCart(orderId: Int, product: ProductEntity) {
        cartMutex.withLock {
            cartDao.addToCartTransaction(orderId, product)
        }
    }


    suspend fun increaseQuantity(item: CartItemEntity){
        cartMutex.withLock {
            cartDao.increaseQuantityTransaction(item)
        }
    }

    suspend fun decreaseQuantity(item: CartItemEntity) {
        cartMutex.withLock {
            cartDao.decreaseQuantityTransaction(item)
        }
    }






    // Função simples para chamar API



    suspend fun syncAddItem(): String? = syncMutex.withLock {

        try {

            val items = cartDao.getPendingItems()

            items.forEach { item ->

                when {

                    // 🔼 INCREMENTAR
                    item.delta > 0 -> {

                        val response = api.addItem(
                            item.orderId,
                            AddItemRequest(
                                product_id = item.productId,
                                quantity = item.delta
                            )
                        )

                        if (!response.isSuccessful) {
                            return response.errorBody()?.string()
                        }
                    }

                    // 🔽 DECREMENTAR
                   item.delta < 0 -> {

                        repeat(kotlin.math.abs(item.delta)) {

                            val response = api.decrementItem(
                                item.orderId,
                                AddItemRequest(
                                    product_id = item.productId,
                                    quantity = 1
                                )
                            )

                            if (!response.isSuccessful) {
                                return response.errorBody()?.string()
                            }
                        }
                    }
                }

                // 🔥 pega item atualizado do banco
                val current = cartDao.getItem(
                    orderId = item.orderId,
                    productId = item.productId
                ) ?: return@forEach

                // 🔥 limpa depois de sincronizar
                val updated = current.copy(
                    pendingSync = false,
                    delta = 0
                )

                cartDao.update(updated)

                // 🔥 deleta apenas depois do sync
                if (updated.quantity == 0) {
                    cartDao.deleteItem(updated.id,
                        updated.orderId)

                   // cartDao.deleteProduct(updated.productId)
                    cartDao.deleteProductIfNotInCart(updated.productId)


                    // 🔥 verifica se carrinho ficou vazio
                    val remainingItems = cartDao.getAllItems()
                    //cartDao.deleteProduct(updated.productId)

                    if (remainingItems.isEmpty()) {
                        _navigationEvent.emit(Unit)
                    }


                }
            }

        } catch (e: Exception) {
            return e.message
        }

        return null
    }

    fun observeCart(orderId: Int?) =
           cartDao.getCart(orderId)

}
