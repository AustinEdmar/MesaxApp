package com.austin.mesax.data.model

import com.google.gson.annotations.SerializedName

data class ProductDTO(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val image_url: String?,
    @SerializedName("category")
    val categoryDTO: CategoryDTO
)
