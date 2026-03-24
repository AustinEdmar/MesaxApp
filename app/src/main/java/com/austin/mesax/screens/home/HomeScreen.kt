package com.austin.mesax.screens.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.austin.mesax.data.local.entity.TableEntity
import com.austin.mesax.data.model.UiStates.AuthUiState
import com.austin.mesax.screens.home.components.LegendItem
import com.austin.mesax.screens.home.components.ScreenScaffold
import com.austin.mesax.data.model.UiStates.TablesUiState
import com.austin.mesax.navigation.Screens
import com.austin.mesax.screens.home.components.OpenTurnDialog
import com.austin.mesax.screens.home.components.TableItem
import com.austin.mesax.viewmodel.AuthViewModel
import com.austin.mesax.viewmodel.OrderViewModel
import com.austin.mesax.viewmodel.ShiftViewModel
import com.austin.mesax.viewmodel.TableViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HomeScreen(
    navController: NavHostController,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    tableViewModel: TableViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
    AuthviewModel: AuthViewModel = hiltViewModel(),

    ) {

    //auth first

    val uiState = AuthviewModel.uiState
    // navegação reativa ao estado
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated) {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    // Controla a visibilidade do dialog
    var showAbrirTurnoDialog by remember { mutableStateOf(true) }

    val shift by shiftViewModel.shift.collectAsState()



    var showConfirmTableDialog by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableEntity?>(null) }  // substitua Table pelo seu tipo


    if (shift?.status == "open") {
        showAbrirTurnoDialog = false
    } else {
        showAbrirTurnoDialog = true

    }


    ScreenScaffold(
        title = "Caixa: ${shift?.userName ?: "Nenhum"}",
        amountTitle = "Fundo: ${shift?.initialAmount ?: "Nenhum"}",
        showMenu = true,
        showCart = false,
        showProfile = true,
        showSearch = false,
        onCartClick = onCartClick,
        onProfileClick = onProfileClick,

    ) {

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem("Disponível", Color(0xFF4CAF50))
            LegendItem("Ocupada", Color(0xFFFFC107))
            LegendItem("Reservada", Color(0xFFF44336))
        }

        when (
            val state = tableViewModel.tablesUiState

        ) {

            TablesUiState.Loading -> {
                CircularProgressIndicator()
            }

            is TablesUiState.Error -> {
                Text(state.message, color = Color.Red)
            }

            is TablesUiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3)
                ) {
                    items(state.tables) { table ->
                        TableItem(
                            table = table,

                            onTableClick = { clickedTable ->
                                selectedTable = clickedTable
                                showConfirmTableDialog = true  // 👈 abre o modal
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal de confirmação da mesa
    if (showConfirmTableDialog && selectedTable != null) {

        Dialog(
            onDismissRequest = {
                showConfirmTableDialog = false
                selectedTable = null
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // 🔹 Título
                    Text(
                        text = "Abrir Mesa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // 🔹 Mensagem
                    Text(
                        text = "Deseja abrir a Mesa ${selectedTable!!.number}?",
                        fontSize = 14.sp,

                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 🔹 Botões
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Cancelar
                        OutlinedButton(
                            onClick = {
                                showConfirmTableDialog = false
                                selectedTable = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar")
                        }

                        // Confirmar
                        Button(
                            onClick = {
                                val table = selectedTable!!
                                showConfirmTableDialog = false
                                selectedTable = null

                                orderViewModel.openOrder(table.number.toInt())
                               // orderViewModel.observeOrders(table.number.toInt())


                                navController.navigate(
                                    Screens.Products.createRoute(table.number.toInt())
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
    // Dialog aparece ao entrar na tela
    if (showAbrirTurnoDialog) {
        OpenTurnDialog(
            onDismiss = {
                showAbrirTurnoDialog = false
            },
            onDone = { initial_amount ->
                shiftViewModel.shift(
                      initial_amount = initial_amount.toDouble()
                  )
            }

        )
    }
}
