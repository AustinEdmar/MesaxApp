package com.austin.mesax.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "products",
    indices = [Index("categoryId")]
)
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    var stock: Int,
    val imageUrl: String?,
    val categoryId: Int
)