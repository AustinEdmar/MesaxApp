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



}
