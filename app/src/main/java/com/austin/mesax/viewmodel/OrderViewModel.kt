package com.austin.mesax.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.mesax.data.local.entity.CartItemEntity
import com.austin.mesax.data.local.entity.ProductEntity

import com.austin.mesax.data.repository.OrdersRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import javax.inject.Inject

import kotlinx.coroutines.launch

//
@HiltViewModel
class OrderViewModel @Inject constructor(

    private val ordersRepository: OrdersRepository,


) : ViewModel() {

    private val _orderId = MutableStateFlow<Int?>(null)
    val orderId: StateFlow<Int?> = _orderId

    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val cartCount = cartItems
        .map { list -> list.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    init {
        syncOrders()

    }

    fun syncAddItem() {
        viewModelScope.launch {

            val error = ordersRepository.syncAddItem()

            if (error != null) {

                _errorMessage.value = error

            }
            Log.d("SYNC", "Chamando syncOrders")
        }

    }
    fun syncOrders() {
        viewModelScope.launch {
            ordersRepository.syncOrders()
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


    fun observeOrders(tableId: Int) {
        viewModelScope.launch {
            ordersRepository.observeOrders(tableId).collect { orders ->

                Log.d("ORDERPAU", orders.toString())

                // pega o primeiro pedido aberto da mesa
                _orderId.value = orders.firstOrNull()?.id
            }
        }
    }


    fun addProduct(product: ProductEntity, orderId: Int) {

        viewModelScope.launch {

            ordersRepository.addToCart(orderId, product)
            Log.d("CART", orderId.toString())
            syncAddItem()
        }
    }







    fun observeCart(orderId: Int?) {
        viewModelScope.launch {
            ordersRepository.observeCart(orderId).collect {
                _cartItems.value = it
                Log.d("CART", it.toString())
            }
        }
    }




}
