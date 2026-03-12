package com.austin.mesax.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage

import com.austin.mesax.screens.home.components.ScreenScaffold
import com.austin.mesax.viewmodel.OrderViewModel
import com.austin.mesax.viewmodel.ShiftViewModel


data class OrderItem(
    val id: Int,
    val name: String,
    val stockLabel: String = "Stock:",
    val stockQty: Int,
    val unitPrice: Int,
    val quantity: Int = 1,
    val imageUrl: String = "https://images.unsplash.com/photo-1608270586620-248524c67de9?w=200&q=80"
)

@Composable
fun CartScreen(
    onCartClick: () -> Unit,
    navController: NavHostController? = null,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
) {
    val shift     by shiftViewModel.shift.collectAsState()
    val cartCount by orderViewModel.cartCount.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }

    val items = remember {
        mutableStateListOf(
            OrderItem(1, "Funge com carne Bacalhau 350 ml", stockQty = 2, unitPrice = 300),
            OrderItem(2, "Cuca Garrafa 350 ml", stockQty = 2, unitPrice = 300000),
            OrderItem(3, "Cuca Garrafa 350 ml", stockQty = 2, unitPrice = 300),
            OrderItem(4, "Cuca Garrafa 350 ml", stockQty = 2, unitPrice = 300),
            OrderItem(5, "Cuca Garrafa 350 ml", stockQty = 2, unitPrice = 300),
            OrderItem(6, "Cuca Garrafa 350 ml", stockQty = 2, unitPrice = 300),
        )
    }

    val subtotal = items.sumOf { it.unitPrice * it.quantity }
    val tax      = 2000
    val total    = subtotal + tax

    ScreenScaffold(
        amountTitle = "Caixa: ${shift?.userName ?: "Nenhum"}",
        title = "Mesa: $",
        showMenu = true,
        showCart = false,
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
                items(items, key = { it.id }) { item ->
                    OrderItemCard(
                        item = item,
                        onIncrease = {
                            val idx = items.indexOf(item)
                            if (idx >= 0) items[idx] = item.copy(quantity = item.quantity + 1)
                        },
                        onDecrease = {
                            val idx = items.indexOf(item)
                            if (idx >= 0 && item.quantity > 1)
                                items[idx] = item.copy(quantity = item.quantity - 1)
                        }
                    )
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
                    SummaryRowCart("Subtotal", "${formatKzCart(subtotal)} KZ", Color.Gray, Color.Black)
                    SummaryRowCart("Tax. iva", "${formatKzCart(tax)} KZ",      Color.Gray, Color.Gray)
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
                        "${formatKzCart(total)} KZ",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { /* TODO */ },
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
@Composable
private fun OrderItemCard(
    item: OrderItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth().
        padding( vertical = 2.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            0.7.dp,
            Color(0xFFE5E5E5)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding( vertical = 12.dp, horizontal = 15.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEEEEEE))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(1.dp))
                Row {
                    Text("${item.stockLabel} ", fontSize = 12.sp, color = Color.Gray)
                    Text("${item.stockQty},  ", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.SemiBold)
                    Text("Preço: ${item.unitPrice} kz", fontSize = 12.sp, color = Color.Gray)

                }


                Spacer(modifier = Modifier.height(1.dp))
                Row {
                    Text("Preço total:  ", fontSize = 12.sp, color = Color.Gray)
                    Text("${item.unitPrice * item.quantity} KZ", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            QuantityStepperCart(item.quantity, onIncrease, onDecrease)
        }
    }
}

// ─── Quantity Stepper ─────────────────────────────────────────────────────────
@Composable
private fun QuantityStepperCart(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Column(

    ) {
        OutlinedIconButton(
            onClick = onDecrease,
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Diminuir", modifier = Modifier.size(14.dp))
        }

        Spacer(modifier = Modifier.padding(top = 2.dp))

        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(quantity.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center)
        }


        Spacer(modifier = Modifier.padding(top = 2.dp))
        OutlinedIconButton(
            onClick = onIncrease,
            modifier = Modifier.size(30.dp),
            shape = CircleShape,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(14.dp))
        }
    }
}

// ─── Summary Row ──────────────────────────────────────────────────────────────
@Composable
private fun SummaryRowCart(label: String, value: String, labelColor: Color, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = labelColor)
        Text(value, fontSize = 13.sp, color = valueColor, fontWeight = FontWeight.Medium)
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────
private fun formatKzCart(amount: Int): String = "%,d".format(amount).replace(',', '.')