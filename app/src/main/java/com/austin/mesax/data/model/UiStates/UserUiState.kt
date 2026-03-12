package com.austin.mesax.data.model.UiStates

import com.austin.mesax.data.responses.AuthResponses.UserDto

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: UserDto) : UserUiState()
    data class Error(val message: String) : UserUiState()
}