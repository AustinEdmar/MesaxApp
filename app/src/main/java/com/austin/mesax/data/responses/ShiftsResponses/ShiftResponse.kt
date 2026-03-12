package com.austin.mesax.data.responses.ShiftsResponses

import com.austin.mesax.data.responses.AuthResponses.UserDto
import com.google.gson.annotations.SerializedName


data class ShiftResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("initial_amount")
    val initialAmount: String,

    @SerializedName("opened_at")
    val openedAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,


    @SerializedName("created_at")
    val createdAt: String?,

    val user: UserDto

)

//{
//	"data": {
//		"id": 4,
//		"status": "open",
//		"initial_amount": "2000.00",
//		"user": {
//			"id": 2,
//			"name": "Austin Edmar",
//			"email": "austin@gmail.com"
//		},
//		"created_at": "2026-02-19T12:28:55.000000Z"
//	}
//}



//{
//	"id": 4,
//	"user_id": 2,
//	"initial_amount": "2000.00",
//	"expected_cash_amount": null,
//	"final_cash_amount": null,
//	"status": "open",
//	"opened_at": "2026-02-19 12:28:55",
//	"closed_at": null,
//	"created_at": "2026-02-19T12:28:55.000000Z",
//	"updated_at": "2026-02-19T12:28:55.000000Z",
//	"user": {
//		"id": 2,
//		"name": "Austin Edmar",
//		"email": "austin@gmail.com",
//		"profile_photo": null,
//		"phone": "923000000",
//		"active": 1,
//		"access_level": 0,
//		"email_verified_at": null,
//		"created_at": "2026-02-19T01:07:29.000000Z",
//		"updated_at": "2026-02-19T01:07:29.000000Z"
//	}
//}