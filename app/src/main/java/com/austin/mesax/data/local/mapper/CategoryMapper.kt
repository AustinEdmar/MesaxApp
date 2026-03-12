package com.austin.mesax.data.local.mapper

import com.austin.mesax.data.local.entity.CategoryEntity
import com.austin.mesax.data.model.CategoryDTO

fun CategoryDTO.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        imagePath = image_path,
        created_at = created_at,
        updated_at = updated_at
    )
}

