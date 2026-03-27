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
class ProductViewModel @Inject constructor(

    private val productRepository: ProductRepository

) : ViewModel() {

/// product

    val selectedCategory = MutableStateFlow<Int?>(null)
    private val searchQuery = MutableStateFlow<String?>(null)

    val products = combine(
        selectedCategory,
        searchQuery
    ) { category, search ->
        category to search
    }.flatMapLatest { (category, search) ->
        productRepository.observeProducts(category, search)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val categories = productRepository.observeCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    /// product
    init {

        viewModelScope.launch {
            productRepository.startAutoSync(viewModelScope)
            //productRepository.syncAll()
            delay(2000)

            val test = productRepository.observeProducts(null, null).first()
            Log.d("ROOM_TEST", "Produtos no banco: ${test.size}")
        }
    }


    /// product


    fun onCategorySelected(categoryId: Int?) {

        searchQuery.value = null
        selectedCategory.value = categoryId

    }


    fun onSearchChanged(query: String?) {
        searchQuery.value = query
    }



}
