package com.austin.mesax.data.repository

import com.austin.mesax.data.api.ShiftApi
import com.austin.mesax.data.datastore.TokenManager
import com.austin.mesax.data.local.dao.ShiftDao
import com.austin.mesax.data.local.entity.ShiftEntity
import com.austin.mesax.data.local.mapper.toEntity
import com.austin.mesax.data.model.ShiftRequest.ShiftRequest
import com.austin.mesax.data.responses.ShiftsResponses.OpenShiftResponse
import com.austin.mesax.data.responses.ShiftsResponses.ShiftResponse
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ShiftRepository @Inject constructor(
    private val api: ShiftApi,
    private val shiftDao: ShiftDao,
    private val tokenManager: TokenManager
) {

    suspend fun shift(initial_amount: Double): OpenShiftResponse {
        val response = api.open(ShiftRequest(initial_amount))
        response.shift?.let {
            shiftDao.insert(it.toEntity())
        }
        return response
    }

    // 👇 AQUI está o shiftFlow
    val shiftFlow: Flow<ShiftEntity?> =
        shiftDao.observeShift()

    suspend fun syncShift() {
        try {
            val response = api.shiftCurrent()
            response.shift?.let {
                shiftDao.insert(it.toEntity())
            }
        } catch (e: Exception) {
            // tratar erro
        }
    }


    suspend fun clearShift() {
        shiftDao.clearAll()
    }

}
