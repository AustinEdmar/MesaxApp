package com.austin.mesax.data.model.OrderResquest

data class AddItemRequest(
    val product_id: Int,
    val quantity: Int
)