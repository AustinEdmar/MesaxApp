package com.austin.mesax.data.model

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    var quantity: Int
)