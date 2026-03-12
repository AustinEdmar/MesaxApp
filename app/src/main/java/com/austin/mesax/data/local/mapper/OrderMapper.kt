package com.austin.mesax.data.local.mapper

import com.austin.mesax.data.local.entity.OrderEntity
import com.austin.mesax.data.model.OrdersDTO

fun OrdersDTO.toEntity() = OrderEntity(
    id = id,
    user_id = user_id,
    shift_id = shift_id,
    table_id = table_id,
    status = status,
    total = total,
    opened_at = opened_at,
    updated_at = updated_at,
    created_at = created_at
)






