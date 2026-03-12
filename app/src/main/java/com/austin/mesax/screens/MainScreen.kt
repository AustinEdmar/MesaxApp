package com.austin.mesax.screens

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.austin.mesax.data.model.UiStates.AuthUiState
import com.austin.mesax.navigation.Screens
import com.austin.mesax.viewmodel.AuthViewModel


@Composable
fun MainScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController
) {
    when (val state = authViewModel.uiState) {

        AuthUiState.Loading -> {
            CircularProgressIndicator()
        }

        AuthUiState.Unauthenticated -> {
            LaunchedEffect(Unit) {
                navController.navigate(Screens.Login.route) {
                    popUpTo(0)
                }
            }
        }

        is AuthUiState.Authenticated -> {
            LaunchedEffect(Unit) {
                navController.navigate(Screens.Home.route) {
                    popUpTo(0)
                }
            }
        }

        is AuthUiState.Error -> {
            Text(state.message)
        }

        else -> {}
    }
}