package com.austin.mesax.data.responses.ShiftsResponses

import com.google.gson.annotations.SerializedName

data class OpenShiftResponse(
    val message: String?,
    @SerializedName("data")
    val shift: ShiftResponse?
)
