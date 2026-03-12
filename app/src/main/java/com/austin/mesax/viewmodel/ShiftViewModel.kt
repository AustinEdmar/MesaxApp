package com.austin.mesax.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.mesax.data.model.UiStates.ShitftUiState
import com.austin.mesax.data.model.UiStates.TablesUiState
import com.austin.mesax.data.model.UiStates.UserUiState
import com.austin.mesax.data.repository.AuthRepository
import com.austin.mesax.data.repository.ProductRepository
import com.austin.mesax.data.repository.ShiftRepository
import com.austin.mesax.data.repository.TablesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException


//
@HiltViewModel
class ShiftViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,

) : ViewModel() {



    var shitftUiState by mutableStateOf<ShitftUiState>(ShitftUiState.Idle)
        private set

    init {

        syncShiftOnStart()

    }

    val shift = shiftRepository.shiftFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private fun syncShiftOnStart() {
        viewModelScope.launch {
            shiftRepository.syncShift()
        }
    }











    fun shift(initial_amount: Double){

           viewModelScope.launch {
               shitftUiState = ShitftUiState.Loading
               try {
                    val response = shiftRepository.shift(initial_amount)

                   shitftUiState = ShitftUiState.Success(response)

                    Log.d("HomeViewModel", "Shift OK: $response")
                   Log.d("HomeViewModel", "Shift OK: $shitftUiState")
                } catch (e: Exception) {
                    shitftUiState = ShitftUiState.Error("Erro ao abrir turno: ${e.message}")
                    Log.d("HomeViewModel", "Shift Error: ${e.message}")
                }

            }
       }


    /// product






}
