package com.austin.mesax.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.austin.mesax.data.model.CartItem
import com.austin.mesax.navigation.Screens
import androidx.compose.ui.platform.LocalContext
import com.austin.mesax.core.lib.SunmiPrinter
import com.austin.mesax.data.model.ReceiptModel
import com.austin.mesax.screens.home.components.CashPaymentDialog
import com.austin.mesax.screens.home.components.PaymentDialog
import com.austin.mesax.screens.home.components.PaymentMethod
import com.austin.mesax.screens.home.components.ScreenScaffold
import com.austin.mesax.screens.home.components.cart.OrderItemCard

import com.austin.mesax.screens.home.components.cart.SummaryRowCart

import com.austin.mesax.viewmodel.CartViewModel
import com.austin.mesax.viewmodel.OrderViewModel
import com.austin.mesax.viewmodel.ShiftViewModel
import java.util.Locale



@Composable
fun CartScreen(
    tableId: Int,
    //orderId: Int,
    onCartClick: () -> Unit,

    navController: NavHostController? = null,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),

    cartViewModel: CartViewModel = hiltViewModel(),
    //printer: SunmiPrinter // 👈 adicionar

) {
    val shift     by shiftViewModel.shift.collectAsState()

    val cartCount by cartViewModel.cartCount.collectAsState()

    val orderId by orderViewModel.orderId.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showCashDialog by remember { mutableStateOf(false) }


    val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()
    var cartLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val printer = remember { SunmiPrinter(context) }

    // ✅ ADICIONE ISTO — chama connectPrinter quando o composable entra no ecrã
// e disconnect quando sai, para libertar recursos
    LaunchedEffect(Unit) {
        printer.connectPrinter()
    }

    DisposableEffect(Unit) {
        onDispose {
            printer.disconnect()
        }
    }


    LaunchedEffect(tableId) {
        orderViewModel.observeOrders(tableId)
    }

    // 🔑 começa a observar o carrinho dessa order
    LaunchedEffect(orderId) {

        cartViewModel.observeCart(orderId)

    }

    // Navega quando a lista fica completamente vazia


    LaunchedEffect(cartitems) {
        when {
            cartitems.isNotEmpty() -> cartLoaded = true
            cartLoaded && cartitems.isEmpty() -> {
                navController?.navigate(Screens.Home.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }



    var showSearchDialog by remember { mutableStateOf(false) }



    cartitems.mapNotNull { cartItemWithProduct ->
        cartItemWithProduct.product?.let { product ->
            CartItem(
                id = cartItemWithProduct.cartItem.id,
                name = product.name,
                stockQty = product.stock,
                iva = product.iva,
                unitPrice = product.price.toInt(),
                quantity = cartItemWithProduct.cartItem.quantity,
                imageUrl = product.imageUrl
            )
        }
    }.toMutableStateList()


    val subtotal = cartitems.sumOf { it.product?.price?.toInt()?.times(it.cartItem.quantity) ?: 0 }
    val tax      = cartitems.sumOf { it.product?.iva ?: 0 }
    val total    = subtotal

    ScreenScaffold(
        amountTitle = "Caixa: ${shift?.userName ?: "Nenhum"}",
        title = "Mesa: $tableId",
        showMenu = true,
        //showCart = false,
        showCart = cartitems.isNotEmpty(), // em vez bollean  chamei o tamanho da lista, que esta  val cartitems by cartViewModel.cartItems.collectAsStateWithLifecycle()
        showSearch = false,
        cartCount = cartCount,
        showProfile = true,
        onSearchClick = { showSearchDialog = true },
        onCartClick = onCartClick,
        onProfileClick = {}
    ) {
        // ── Lista de itens ────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {


            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues()
            ) {
                items(cartitems, key = { it.cartItem.id }) { cartItemWithProduct ->
                    cartItemWithProduct.product?.let { product ->
                        OrderItemCard(
                            item = CartItem(
                                id        = cartItemWithProduct.cartItem.id,
                                name      = product.name,
                                stockQty  = product.stock,
                                iva  = product.iva,
                                unitPrice = product.price.toInt(),
                                quantity  = cartItemWithProduct.cartItem.quantity,
                                imageUrl  = product.imageUrl
                            ),
                            onIncrease = {

                                cartViewModel.increaseQuantity(cartItemWithProduct)
                            },
                            onDecrease = {
                                cartViewModel.decreaseQuantity(cartItemWithProduct)
                            },
                            //decreaseEnabled = product.stock > 0,
                            decreaseEnabled = cartItemWithProduct.cartItem.quantity > 0,
                            increaseEnabled = product.stock > 1




                        )
                    }
                }
            }


            // ── Rodapé: subtotal, tax, total, botão pagar ─────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SummaryRowCart("Subtotal", "${formatKzCart(subtotal.toDouble())} KZ", Color.Gray, Color.Black)
                    SummaryRowCart("Tax. iva", "${formatKzCart(tax.toDouble())} %",      Color.Gray, Color.Gray)
                }

                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total a pagar", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Text(
                        "${formatKzCart(total.toDouble())} KZ",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (showPaymentDialog) {
                    PaymentDialog(
                        onDismiss = { showPaymentDialog = false },

                        onSelect = { paymentMethod ->

                            showPaymentDialog = false

                            when(paymentMethod) {

                                PaymentMethod.CASH -> {
                                    showCashDialog = true
                                }

                                else -> {

                                    val items = cartitems.mapNotNull { cartItemWithProduct ->
                                        cartItemWithProduct.product?.let { product ->
                                            CartItem(
                                                id = cartItemWithProduct.cartItem.id,
                                                name = product.name,
                                                stockQty = product.stock,
                                                iva = product.iva,
                                                unitPrice = product.price.toInt(),
                                                quantity = cartItemWithProduct.cartItem.quantity,
                                                imageUrl = product.imageUrl
                                            )
                                        }
                                    }

                                    printer.printReceipt(
                                        ReceiptModel(
                                            items = items,
                                            subtotal = subtotal,
                                            tax = tax,
                                            total = total,
                                            tableId = tableId,
                                            orderId = orderId,
                                            paymentMethod = paymentMethod
                                        )
                                    )
                                }
                            }
                        }
                    )
                }

                if (showCashDialog) {
                    CashPaymentDialog(
                        total = total,
                        onDismiss = { showCashDialog = false },

                        onConfirm = { received, change ->

                            showCashDialog = false

                            val items = cartitems.mapNotNull { cartItemWithProduct ->
                                cartItemWithProduct.product?.let { product ->
                                    CartItem(
                                        id = cartItemWithProduct.cartItem.id,
                                        name = product.name,
                                        stockQty = product.stock,
                                        iva = product.iva,
                                        unitPrice = product.price.toInt(),
                                        quantity = cartItemWithProduct.cartItem.quantity,
                                        imageUrl = product.imageUrl
                                    )
                                }
                            }

                            printer.printReceipt(
                                ReceiptModel(
                                    items = items,
                                    subtotal = subtotal,
                                    tax = tax,
                                    total = total,
                                    tableId = tableId,
                                    orderId = orderId,
                                    paymentMethod = PaymentMethod.CASH,
                                    paidAmount = received,
                                    change = change
                                )
                            )
                        }
                    )
                }
                Button(
//                    onClick = {
//
//                        val items = cartitems.mapNotNull { cartItemWithProduct ->
//                            cartItemWithProduct.product?.let { product ->
//                                CartItem(
//                                    id = cartItemWithProduct.cartItem.id,
//                                    name = product.name,
//                                    stockQty = product.stock,
//                                    iva = product.iva,
//                                    unitPrice = product.price.toInt(),
//                                    quantity = cartItemWithProduct.cartItem.quantity,
//                                    imageUrl = product.imageUrl
//                                )
//                            }
//                        }
//
//                        printer.printReceipt(
//
//                            ReceiptModel(
//                                items = items,
//                                subtotal = subtotal,
//                                tax = tax,
//                                total = total,
//                                tableId = tableId,
//                                orderId = orderId
//                            )
//                        )
//                    },

                    onClick = {
                        showPaymentDialog = true
                    },


                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 5.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    )

                ) {
                    Text("Pagar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        }
    }
}

// ─── Order Item Card ──────────────────────────────────────────────────────────


// ─── Quantity Stepper ─────────────────────────────────────────────────────────

// ─── Summary Row ──────────────────────────────────────────────────────────────


fun formatKzCart(value: Double): String {
    return String.format(Locale.getDefault(), "%,.0f", value).replace(",", ".")
}