package com.austin.mesax.data.repository

import com.austin.mesax.data.api.OrdersApi
import com.austin.mesax.data.local.dao.CartDao
import com.austin.mesax.data.local.dao.OrderDao
import com.austin.mesax.data.local.entity.CartItemEntity
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.local.mapper.toEntity
import com.austin.mesax.data.model.OrderResquest.AddItemRequest
import com.austin.mesax.data.model.OrderResquest.OpenOrderRequest
import com.austin.mesax.data.model.OrdersDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class OrdersRepository @Inject constructor (
    private val api: OrdersApi,
    private val orderDao: OrderDao,
   // private val cartDao: CartDao
){

    private val syncMutex = Mutex()

    // 1- ORDERS
    suspend fun getOrders(tableId: Int): OrdersDTO {
        val response = api.getOrder(OpenOrderRequest(tableId))

        // salva no Room
        orderDao.insert(response.toEntity())

        return response
    }

    fun observeOrders(tableId: Int) = orderDao.observeOrders(tableId)

    suspend fun syncOrders() {

        try {

            val remoteOrders = api.getOrders()
            val localOrders = orderDao.getOrdersOnce()

            val remoteIds = remoteOrders.map { it.id }
            val localIds = localOrders.map { it.id }

            val toDelete = localIds - remoteIds.toSet()

            orderDao.deleteOrdersByIds(toDelete)
            orderDao.clearOrders()
            orderDao.insertOrders(
                remoteOrders.map { it.toEntity() }
            )

        } catch (e: Exception) {
            // offline → usa Room
        }
    }

    // 2- CARTS





    //suspend fun deleteCartItem(item: CartItemEntity) {
    //        cartDao.deleteItem(item.id)
    //
    //        syncAddItem()
    //    }


    // Função simples para chamar API


    //suspend fun syncAddItem(): String? = syncMutex.withLock {
    //
    //        try {
    //
    //            val items = cartDao.getPendingItems()
    //
    //            items.forEach { item ->
    //
    //                val response = api.addItem(
    //                    item.orderId,
    //                    AddItemRequest(
    //                        product_id = item.productId,
    //                        quantity = item.quantity
    //                    )
    //                )
    //
    //                if (response.isSuccessful) {
    //
    //                    val updated = item.copy(pendingSync = false)
    //                    cartDao.update(updated)
    //
    //                } else {
    //
    //                    return response.errorBody()?.string()
    //                }
    //            }
    //
    //        } catch (e: Exception) {
    //            return e.message
    //        }
    //
    //        return null
    //    }
    //
    //    fun observeCart(orderId: Int?) =
    //           cartDao.getCart(orderId)
    //
    //
    //    suspend fun updateCartItem(item: CartItemEntity) {
    //        cartDao.update(item)
    //    }





}
