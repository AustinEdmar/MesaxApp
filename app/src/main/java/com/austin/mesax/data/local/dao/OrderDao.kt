package com.austin.mesax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.austin.mesax.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)


    @Query("SELECT * FROM orders WHERE table_id = :tableId")
    fun observeOrders(tableId: Int): Flow<List<OrderEntity>>



    @Query("SELECT * FROM orders")
    suspend fun getOrdersOnce(): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("DELETE FROM orders WHERE id IN (:ids)")
    suspend fun deleteOrdersByIds(ids: List<Int>)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Int)

    @Query("DELETE FROM orders")
    suspend fun clearOrders()



}