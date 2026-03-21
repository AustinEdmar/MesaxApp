package com.austin.mesax.data.repository

import com.austin.mesax.data.api.OrdersApi
import com.austin.mesax.data.local.dao.CartDao
import com.austin.mesax.data.local.dao.OrderDao
import com.austin.mesax.data.local.entity.CartItemEntity
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.local.mapper.toEntity
import com.austin.mesax.data.model.OrderResquest.AddItemRequest

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class CartRepository @Inject constructor (
    private val api: OrdersApi,

    private val cartDao: CartDao
){

    private val syncMutex = Mutex()



 //   suspend fun addToCart(
    //        orderId: Int,
    //        product: ProductEntity
    //    ) {
    //
    //        if (product.stock <= 0) {
    //            throw Exception("Produto sem estoque")
    //        }
    //
    //        // ✅ diminui o stock corretamente
    //        product.stock -= 1
    //
    //        // Garante que o product exist no banco de dados de pedidos (foreign key)
    //        cartDao.insertProduct(product)
    //
    //        // ✅ Persiste a diminuição do estoque no banco de dados
    //        cartDao.updateProduct(product)
    //
    //        val existingItem =
    //            cartDao.getItem(orderId, product.id)
    //
    //        if (existingItem != null) {
    //
    //            val updated =
    //                existingItem.copy(
    //                    quantity = existingItem.quantity + 1,
    //                    pendingSync = true
    //                )
    //
    //            cartDao.update(updated)
    //
    //        } else {
    //
    //            val item =
    //                CartItemEntity(
    //                    orderId = orderId,
    //                    productId = product.id,
    //                    quantity = 1,
    //                    pendingSync = true
    //                )
    //
    //            cartDao.insert(item)
    //        }
    //    }


    suspend fun addToCart(orderId: Int, product: ProductEntity) {
        cartDao.addToCartTransaction(orderId, product)
    }





    suspend fun deleteCartItem(item: CartItemEntity) {
        cartDao.deleteItem(item.id)

        syncAddItem()
    }


    // Função simples para chamar API


    suspend fun syncAddItem(): String? = syncMutex.withLock {

        try {

            val items = cartDao.getPendingItems()

            items.forEach { item ->

                val response = api.addItem(
                    item.orderId,
                    AddItemRequest(
                        product_id = item.productId,
                        quantity = item.quantity
                    )
                )

                if (response.isSuccessful) {

                    val updated = item.copy(pendingSync = false)
                    cartDao.update(updated)

                } else {

                    return response.errorBody()?.string()
                }
            }

        } catch (e: Exception) {
            return e.message
        }

        return null
    }

    fun observeCart(orderId: Int?) =
           cartDao.getCart(orderId)


    suspend fun updateCartItem(item: CartItemEntity) {
        cartDao.update(item)
    }








}
