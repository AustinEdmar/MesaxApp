package com.austin.mesax.data.api

import com.austin.mesax.data.responses.AuthResponses.AuthResponse
import com.austin.mesax.data.model.AuthRequest.LoginRequest
import com.austin.mesax.data.responses.AuthResponses.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): AuthResponse

    @GET("user") // vai ser usado para actualizar o user no datastore
     fun me(): UserResponse


    @POST("logout")
    suspend fun logout()

}
