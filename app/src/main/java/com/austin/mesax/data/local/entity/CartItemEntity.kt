package com.austin.mesax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val orderId: Int,

    val productId: Int,

    val name: String,

    val price: Double,

    val quantity: Int = 1,

    val pendingSync: Boolean = true
)

