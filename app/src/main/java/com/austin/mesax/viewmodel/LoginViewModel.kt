package com.austin.mesax.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.mesax.data.model.UiStates.LoginUiState
import com.austin.mesax.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.json.JSONObject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun login(email: String, password: String) {

        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val response = repository.login(email, password)

                // vem do field:SerializedName(value = "access_token")
                //public final val accessToken: String
                //Log.d("LoginViewModel", "Login OK: ${response.accessToken}")

                //repository.saveToken(response.accessToken)

                uiState = LoginUiState.Success(response)

                Log.d("LoginViewModel", "Login OK: ${response.user.email}")
                Log.d("LoginViewModelResponse", "Login OK: ${uiState}")

            } catch (e: retrofit2.HttpException) {
                // Erro do servidor / API
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    val json = org.json.JSONObject(errorBody ?: "")
                    json.getString("message")
                } catch (ex: Exception) {
                    errorBody ?: "Erro no servidor"
                }
                uiState = LoginUiState.Error("Erro ${e.code()}: $errorMessage")

            } catch (e: java.io.IOException) {
                // Sem internet ou problemas de rede
                uiState = LoginUiState.Error("Sem conexão. Verifique sua internet.")

            } catch (e: Exception) {
                // Outros erros inesperados
                uiState = LoginUiState.Error("Erro inesperado: ${e.message}")
            }
        }
    }

}
