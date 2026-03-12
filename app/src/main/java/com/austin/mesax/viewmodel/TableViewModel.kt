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
import com.austin.mesax.data.repository.OrdersRepository
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
class TableViewModel @Inject constructor(
    private val tablesRepository: TablesRepository,
    private val ordersRepository: OrdersRepository

) : ViewModel() {
    var tablesUiState by mutableStateOf<TablesUiState>(TablesUiState.Loading)
        private set






    init {

        observeTables()

        syncTables()
        startSyncLoop()

    }

    private fun startSyncLoop() {
        viewModelScope.launch {
            while(true) {
                try {
                    tablesRepository.syncTables()
                } catch (e: Exception) {
                    Log.e("TABLE_SYNC", e.message ?: "")
                }
                delay(5000) // a cada 5 segundos
            }
        }
    }


    fun openOrder(tableId: Int) {
        viewModelScope.launch {
            try {
                val response = ordersRepository.getOrders(tableId)
                Log.d("ORDER", response.toString())
            } catch (e: Exception) {
                Log.e("ORDER_ERROR", e.message.toString())
            }
        }
    }

    fun syncTables() {
        viewModelScope.launch {
            try {
                tablesRepository.syncTables()
            } catch (e: IOException) {
                // Erro de rede/falta de internet
                if (tablesUiState is TablesUiState.Loading) {
                    tablesUiState = TablesUiState.Error("Sem conexão com a internet. Verifique sua rede.")
                }
            } catch (e: Exception) {
                // Outros erros
                if (tablesUiState is TablesUiState.Loading) {
                    tablesUiState = TablesUiState.Error("Erro ao sincronizar dados: ${e.message}")
                }
            }
        }
    }


    private fun observeTables() {
        viewModelScope.launch {
            tablesRepository.observeTables()
                .catch {
                    tablesUiState = TablesUiState.Error("Erro ao carregar mesas")
                }
                .collect { tables ->
                    tablesUiState = TablesUiState.Success(tables)
                }
      }
    }





}
