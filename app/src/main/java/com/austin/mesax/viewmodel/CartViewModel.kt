package com.austin.mesax.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.mesax.data.local.entity.CartItemWithProduct
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.repository.CartRepository

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
class CartViewModel @Inject constructor(

    private val cartRepository: CartRepository,


    ) : ViewModel() {


    private val _cartItems = MutableStateFlow<List<CartItemWithProduct>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val cartCount = cartItems
        .map { list -> list.sumOf { it.cartItem.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    init {


    }


    fun syncAddItem() {
        viewModelScope.launch {

            val error = cartRepository.syncAddItem()

            if (error != null) {

                _errorMessage.value = error

            }
            Log.d("SYNC", "Chamando syncOrders")
        }

    }



    //cart
    fun addProduct(product: ProductEntity, orderId: Int) {

        viewModelScope.launch {

            cartRepository.addToCart(orderId, product)
            Log.d("CART", orderId.toString())
            syncAddItem()
        }
    }




    fun observeCart(orderId: Int?) {
        Log.d("CART_VM", "orderId recebido: $orderId")

        viewModelScope.launch {
            cartRepository.observeCart(orderId).collect {
                Log.d("CART_VM", "Recebeu: $it")
                _cartItems.value = it
            }
        }
    }


    fun increaseQuantity(item: CartItemWithProduct) {

        viewModelScope.launch {

            val updatedCartItem = item.cartItem.copy(
                quantity = item.cartItem.quantity + 1,
                pendingSync = true
            )

            cartRepository.updateCartItem(updatedCartItem)

            cartRepository.syncAddItem()
        }
    }

    fun decreaseQuantity(item: CartItemWithProduct) {

        viewModelScope.launch {

            if (item.cartItem.quantity <= 1) {
                cartRepository.deleteCartItem(item.cartItem)
            } else {

                val updated = item.cartItem.copy(
                    quantity = item.cartItem.quantity - 1,
                    pendingSync = true
                )

                cartRepository.updateCartItem(updated)
            }

            cartRepository.syncAddItem()
        }
    }




}
