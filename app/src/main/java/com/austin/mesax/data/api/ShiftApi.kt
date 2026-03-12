package com.austin.mesax.data.api

import com.austin.mesax.data.model.AuthRequest.LoginRequest
import com.austin.mesax.data.model.ShiftRequest.ShiftRequest
import com.austin.mesax.data.responses.AuthResponses.AuthResponse
import com.austin.mesax.data.responses.AuthResponses.UserResponse
import com.austin.mesax.data.responses.ShiftsResponses.OpenShiftResponse
import com.austin.mesax.data.responses.ShiftsResponses.ShiftResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShiftApi {

    @POST("shifts/open")
    suspend fun open(
        @Body body: ShiftRequest
    ): OpenShiftResponse


    @GET("shifts/current") // vai ser usado para actualizar o user no datastore
    suspend fun shiftCurrent(): OpenShiftResponse
}
