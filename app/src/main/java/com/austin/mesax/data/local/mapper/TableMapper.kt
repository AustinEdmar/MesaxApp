package com.austin.mesax.data.local.mapper

import com.austin.mesax.data.model.TableDto
import com.austin.mesax.data.local.entity.TableEntity

fun TableDto.toEntity() = TableEntity(
    id = id,
    number = number,
    status = status
)