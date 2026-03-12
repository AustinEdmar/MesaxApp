package com.austin.mesax.data.local.mapper

import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.model.ProductDTO

fun ProductDTO.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        imageUrl = image_url,
        categoryId = categoryDTO.id
    )
}