package com.austin.mesax.data.model.UiStates

import com.austin.mesax.data.local.entity.TableEntity

sealed class TablesUiState {
    object Loading : TablesUiState()
    data class Success(val tables: List<TableEntity>) : TablesUiState()
    data class Error(val message: String) : TablesUiState()
}
