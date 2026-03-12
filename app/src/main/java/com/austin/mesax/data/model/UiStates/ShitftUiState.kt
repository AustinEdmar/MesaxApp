package com.austin.mesax.data.model.UiStates

import com.austin.mesax.data.responses.ShiftsResponses.OpenShiftResponse

sealed class ShitftUiState {
    object Idle : ShitftUiState()
    object Loading : ShitftUiState()
    data class Success(val shift: OpenShiftResponse) : ShitftUiState()
    data class Error(val message: String) : ShitftUiState()
}