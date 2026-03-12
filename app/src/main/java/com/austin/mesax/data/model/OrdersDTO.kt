package com.austin.mesax.data.model

data class OrdersDTO(
    val id: Int,
    val user_id: Int,
    val shift_id: Int,
    val table_id: Int,
    val status: String,
    val total: Double,
    val opened_at: String?,
    val updated_at: String?,
    val created_at: String?

)
