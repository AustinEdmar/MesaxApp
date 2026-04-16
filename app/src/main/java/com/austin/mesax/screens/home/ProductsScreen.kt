package com.austin.mesax.screens.home

import android.content.Context
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import com.austin.mesax.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.austin.mesax.data.model.UiStates.AuthUiState
import com.austin.mesax.navigation.Screens
import com.austin.mesax.screens.home.components.EmptyProductsState

import com.austin.mesax.screens.home.components.ProductItemCard
import com.austin.mesax.screens.home.components.ScreenScaffold
import com.austin.mesax.screens.home.components.SearchDialog
import com.austin.mesax.viewmodel.AuthViewModel
import com.austin.mesax.viewmodel.CartViewModel
import com.austin.mesax.viewmodel.OrderViewModel
import com.austin.mesax.viewmodel.ProductViewModel
import com.austin.mesax.viewmodel.ShiftViewModel

@Composable
fun ProductsScreen(
    tableId: Int,
    navController: NavHostController? = null,

    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,

    shiftViewModel: ShiftViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    AuthviewModel: AuthViewModel = hiltViewModel(),

) {

    //auth first

    val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()

    val uiState = AuthviewModel.uiState
    // navegação reativa ao estado
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated) {
            navController?.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val shift by shiftViewModel.shift.collectAsState()
    val products by productViewModel.products.collectAsState()
    val categories by productViewModel.categories.collectAsState()
    val selectedCategory by productViewModel.selectedCategory.collectAsState()
    val orderId by orderViewModel.orderId.collectAsState()

    val cartCount by cartViewModel.cartCount.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val error by orderViewModel.errorMessage.collectAsState()


    LaunchedEffect(tableId) {
           orderViewModel.observeOrders(tableId)
        }

    // 🔑 começa a observar o carrinho dessa order
//    LaunchedEffect(orderId) {
//
//        cartViewModel.observeCart(orderId)
//
//    }

    LaunchedEffect(orderId) {
        cartViewModel.setOrderId(orderId)
    }

    LaunchedEffect(error) {

        error?.let {

            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

        }

    }
    val soundPool = remember {
        SoundPool.Builder().setMaxStreams(1).build()
    }

    val soundId = remember {
        soundPool.load(context, R.raw.beep, 1)
    }

    ScreenScaffold(
        amountTitle = "Caixa: ${shift?.userName ?: "Nenhum"}",
        title = "Mesa: $tableId",
        showMenu = true,
        showCart = cartitems.isNotEmpty(), // em vez bollean  chamei o tamanho da lista, que esta  val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()
        showSearch = true,
        cartCount = cartCount,
        showProfile = true,
        onSearchClick = { showSearchDialog = true },
        onCartClick = onCartClick,
        onProfileClick = onProfileClick
    ) {

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                CategoryChip(
                    iconM = Icons.Default.Fastfood,
                    text = "Todos",
                    isSelected = selectedCategory == null,
                    onClick = { productViewModel.onCategorySelected(null) }
                )
            }

            items(categories.size) { index ->
                val category = categories[index]

                CategoryChip(
                    icon = category.imagePath,
                    text = category.name,
                    isSelected = selectedCategory == category.id,
                    onClick = {
                        productViewModel.onCategorySelected(category.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        // Menu Items Grid

        if (products.isEmpty() ) {

            EmptyProductsState()

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                items(products,
                    key = { product -> product.id }
                ) {
                    product ->
                    ProductItemCard(
                        product = product,
                        onClick = {
                            soundPool.play(soundId, 1f, 1f, 0, 0, 1f) // 🔊 som


                            Log.d("CLICK", "clicou no produto ${product.id}")
                            if (product.stock <= 0) {
                                Toast.makeText(
                                    context,
                                    "Produto sem estoque",
                                    Toast.LENGTH_SHORT
                                ).show()

                                return@ProductItemCard
                            }

                            orderId?.let {
                                cartViewModel.addProduct(product, it)
                            }
                        }
                    )
                }
            }
        }


    }

    if (showSearchDialog) {
        SearchDialog(
            onDismiss = {
                productViewModel.onSearchChanged(null)
                showSearchDialog = false
                        },
            onSearch = { query ->
                productViewModel.onSearchChanged(
                    query.takeIf { it.isNotBlank() }
                )
                showSearchDialog = false
            }
        )
    }


}
