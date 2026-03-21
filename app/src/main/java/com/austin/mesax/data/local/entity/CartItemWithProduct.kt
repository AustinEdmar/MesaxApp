package com.austin.mesax.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CartItemWithProduct(
    @Embedded val cartItem: CartItemEntity,

    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: ProductEntity?
)