package com.austin.mesax.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.austin.mesax.data.local.dao.CartDao
import com.austin.mesax.data.local.dao.OrderDao
import com.austin.mesax.data.local.entity.CartItemEntity
import com.austin.mesax.data.local.entity.OrderEntity


@Database(
    entities = [OrderEntity::class, CartItemEntity::class],
    version = 3,
    exportSchema = false
)
abstract class OrderDatabase : RoomDatabase() {

    abstract fun OrderDao(): OrderDao
    abstract fun CartDao(): CartDao
}
