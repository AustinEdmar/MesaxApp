package com.austin.mesax.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.austin.mesax.data.local.entity.CartItemWithProduct
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.repository.CartRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn


import javax.inject.Inject

import kotlinx.coroutines.launch

//
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val orderIdFlow = MutableStateFlow<Int?>(null)

    // 👇 CART 100% REATIVO (SEM JOBS, SEM OBSERVE MANUAL)
    val cartItems = orderIdFlow
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { orderId ->
            cartRepository.observeCart(orderId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // contador automático
    val cartCount = cartItems
        .map { list -> list.sumOf { it.cartItem.quantity } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0
        )

    fun setOrderId(orderId: Int?) {
        orderIdFlow.value = orderId
    }

    fun addProduct(product: ProductEntity, orderId: Int) {
        viewModelScope.launch {
            cartRepository.addToCart(orderId, product)
        }
    }

    fun increaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(item.cartItem)
        }
    }

    fun decreaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(item.cartItem)
        }
    }
}