package com.austin.mesax.data.local.mapper

import com.austin.mesax.data.local.entity.ShiftEntity
import com.austin.mesax.data.responses.ShiftsResponses.ShiftResponse

fun ShiftResponse.toEntity(): ShiftEntity {
    return ShiftEntity(
        id = id, // continua fixo se queres só 1 turno local
        userId = user.id, // 👈 AGORA VEM DO OBJETO USER
        userName = user.name, // 👈 AGORA VEM DO OBJETO USER
        initialAmount = initialAmount.toDouble(),
        status = status,
        openedAt = openedAt,
        updatedAt = updatedAt,
        createdAt = createdAt
    )
}

