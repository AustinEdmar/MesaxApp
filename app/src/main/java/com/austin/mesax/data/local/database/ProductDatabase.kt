package com.austin.mesax.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.austin.mesax.data.local.dao.CategoryDao
import com.austin.mesax.data.local.dao.ProductDao
import com.austin.mesax.data.local.entity.CategoryEntity
import com.austin.mesax.data.local.entity.ProductEntity


@Database(
    entities = [ProductEntity::class, CategoryEntity::class],
    version = 1
)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}