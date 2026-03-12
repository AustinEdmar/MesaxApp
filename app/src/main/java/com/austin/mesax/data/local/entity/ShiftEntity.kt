package com.austin.mesax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "shifts")
data class ShiftEntity(

    @PrimaryKey

    val id: Int, // 👈 sempre 1

    val userId: Int,
    val userName: String,

    val initialAmount: Double,

    val status: String,

    val openedAt: String?,

    val updatedAt: String?,

    val createdAt: String?
)
