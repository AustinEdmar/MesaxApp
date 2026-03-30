package com.austin.mesax.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.mesax.data.local.entity.CartItemWithProduct
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.repository.CartRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import javax.inject.Inject

import kotlinx.coroutines.launch

//
@HiltViewModel
class CartViewModel @Inject constructor(

    private val cartRepository: CartRepository,


    ) : ViewModel() {

    val navigationEvent = cartRepository.navigationEvent
    private val _cartItems = MutableStateFlow<List<CartItemWithProduct>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val cartCount = cartItems
        .map { list -> list.sumOf { it.cartItem.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    init {


    }

    private var syncJob: Job? = null

    fun triggerSync() {
        syncJob?.cancel()

        syncJob = viewModelScope.launch {
            delay(5000) // espera o usuário parar de clicar
            cartRepository.syncAddItem()

        }
    }




    //cart
    fun addProduct(product: ProductEntity, orderId: Int) {

        viewModelScope.launch {

            cartRepository.addToCart(orderId, product)
            Log.d("CART", orderId.toString())
            triggerSync() // 🔥 não chama direto
            // syncAddItem()
        }
    }




    fun observeCart(orderId: Int?) {
        viewModelScope.launch {
            cartRepository.observeCart(orderId)
                .distinctUntilChanged()
                .collect {
                    _cartItems.value = it
                }
        }
    }


    fun increaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(item.cartItem)
            triggerSync()
        }
    }

    fun decreaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(item.cartItem)
            triggerSync()
        }
    }
    }


//    fun increaseQuantity(item: CartItemWithProduct) {
//
//        viewModelScope.launch {
//
//            val updatedCartItem = item.cartItem.copy(
//                quantity = item.cartItem.quantity + 1,
//                delta = item.cartItem.delta + 1, // 🔥 incrementa delta
//                pendingSync = true
//            )
//
//            cartRepository.updateCartItem(updatedCartItem)
//            triggerSync() // 🔥 não chama direto
//            //cartRepository.syncAddItem()
//        }
//    }
//
//    fun decreaseQuantity(item: CartItemWithProduct) {
//
//        viewModelScope.launch {
//
//            if (item.cartItem.quantity <= 1) {
//                cartRepository.deleteCartItem(item.cartItem)
//            } else {
//
//                val updated = item.cartItem.copy(
//                    quantity = item.cartItem.quantity - 1,
//                    delta = item.cartItem.delta - 1, // 🔥 incrementa delta
//                    pendingSync = true
//                )
//
//                cartRepository.updateCartItem(updated)
//            }
//            triggerSync() // 🔥 não chama direto
//           // cartRepository.syncAddItem()
//        }
//    }
