package com.austin.mesax.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.austin.mesax.data.model.UiStates.AuthUiState
import com.austin.mesax.data.model.UiStates.TablesUiState

import com.austin.mesax.data.model.UiStates.UserUiState

import com.austin.mesax.navigation.Screens
import com.austin.mesax.screens.home.components.LegendItem
import com.austin.mesax.screens.home.components.ScreenScaffold
import com.austin.mesax.screens.home.components.TableItem
import com.austin.mesax.viewmodel.AuthViewModel
import com.austin.mesax.viewmodel.HomeViewModel
import com.austin.mesax.viewmodel.ShiftViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    AuthviewModel: AuthViewModel = hiltViewModel(),
    HomeViewModel: HomeViewModel = hiltViewModel(),
    shiftViewModel: ShiftViewModel = hiltViewModel(),
    onPrintClick: () -> Unit,
    printBarcode: () -> Unit,
   // printReceipt: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    isConnected: Boolean
) {
    val uiState = AuthviewModel.uiState
    val shift by shiftViewModel.shift.collectAsState()
    val connectionStatus = if (isConnected) "Conectado" else "Desconectado"

    // navegação reativa ao estado
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Unauthenticated) {
            navController.navigate(Screens.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val isLoading = uiState is AuthUiState.Loading

    //gff
    ScreenScaffold(
        title = "Caixa: ${shift?.userName ?: "Nenhum"}",
        amountTitle = if (
            shift?.status == "open"
        ) {
            "Maneio: ${shift?.initialAmount ?: 0.0} kz"
        } else {
            " 0.0 kz"
        },

        showMenu = true,
        showCart = true,
        showProfile = false,
        onCartClick = onCartClick,
        onProfileClick = onProfileClick
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            when (val state = HomeViewModel.uiState) {

                is UserUiState.Loading -> {
                    Text("Carregando usuário...")
                }

                is UserUiState.Success -> {
                    Column {
                        Text("Olá, ${state.user.name} 👋",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Email: ${state.user.email}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Light
                        )

                    }
                }

                is UserUiState.Error -> {
                    Text("Erro: ${state.message}")
                }
            }




            Spacer(modifier = Modifier.height(32.dp))

            // BOTÃO DE LOGOUT COM LOADING
            Button(
                onClick = {
                    AuthviewModel.logout()

                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Sair")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // (5) chama impressão
                    onPrintClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Imprimir Teste")
            }

            Button(
                onClick = {
                    // (5) chama impressão
                    printBarcode()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Imprimir barcode")
            }










            Text(
                text = "Status: $connectionStatus",
                fontSize = 14.sp,
                color = if (isConnected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )




        }

    }
    //


}
