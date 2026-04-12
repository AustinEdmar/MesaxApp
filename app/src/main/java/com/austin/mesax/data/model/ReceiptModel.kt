package com.austin.mesax.data.model

import com.austin.mesax.screens.home.components.PaymentMethod

data class ReceiptModel(
    val items    : List<CartItem>,
    val subtotal : Int,
    val tax      : Int,
    val total    : Int,
    val tableId  : Int,
    val orderId  : Int?,
    val paymentMethod: PaymentMethod,
    val paidAmount: Int? = null,
    val change: Int? = null
)
