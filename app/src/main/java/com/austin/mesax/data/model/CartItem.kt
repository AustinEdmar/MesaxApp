package com.austin.mesax.data.model

data class CartItem(
    val id: Int,
    val name: String,
    val stockLabel: String = "Stock:",
    val stockQty: Int,
    val unitPrice: Int,
    val quantity: Int = 1,
    val imageUrl: String?
)