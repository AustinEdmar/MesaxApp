package com.austin.mesax.data.api

import com.austin.mesax.data.model.OrderResquest.AddItemRequest
import com.austin.mesax.data.model.OrderResquest.OpenOrderRequest
import com.austin.mesax.data.model.OrdersDTO
import com.austin.mesax.data.responses.OrderResponses.OrderActionResponse
import com.austin.mesax.data.responses.OrderResponses.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApi {

    @POST("orders/open")
    suspend fun getOrder(
        @Body request: OpenOrderRequest
    ): OrdersDTO



    @GET("orders")
    suspend fun getOrders(): List<OrdersDTO>


   // @POST("orders/{orderId}/add-item")
    //    suspend fun addItem(
    //        @Path("orderId") orderId: Int,
    //        @Body request: AddItemRequest
    //    ): OrderActionResponse

    @POST("orders/{orderId}/add-item")
    suspend fun addItem(
        @Path("orderId") orderId: Int,
        @Body request: AddItemRequest
    ): Response<OrderActionResponse>

}