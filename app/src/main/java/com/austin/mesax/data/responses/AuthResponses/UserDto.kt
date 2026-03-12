package com.austin.mesax.data.responses.AuthResponses

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,

    val phone: String?, // opcional, mas recomendo

    @SerializedName("access_level")
    val accessLevel: Int?,

    @SerializedName("profile_photo")
    val profilePhoto: String?,

    @SerializedName("email_verified_at")
    val emailVerifiedAt: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)