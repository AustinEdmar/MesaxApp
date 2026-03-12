package com.austin.mesax.data.model.UiStates

import com.austin.mesax.data.responses.AuthResponses.AuthResponse

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val auth: AuthResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()

}