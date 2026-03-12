package com.austin.mesax.data.model.AuthRequest

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)