package com.austin.mesax


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.austin.mesax.core.lib.SunmiPrinter
import com.austin.mesax.navigation.Screens
import com.austin.mesax.screens.MainScreen
import com.austin.mesax.screens.auth.LoginScreen
import com.austin.mesax.screens.home.CartScreen


import com.austin.mesax.screens.home.HomeScreen
import com.austin.mesax.screens.home.ProductsScreen

import com.austin.mesax.screens.profile.ProfileScreen


import com.austin.mesax.ui.theme.ShopEasyTheme
import com.austin.mesax.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

   //0
    private lateinit var printer: SunmiPrinter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // (1) cria instância
        printer = SunmiPrinter(this)

        // (2) conecta impressora
        printer.connectPrinter()
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            ShopEasyTheme(){

                val navController = rememberNavController()
                val cartViewModel: CartViewModel = hiltViewModel()

                // 🔥 Sync ao abrir app
//                LaunchedEffect(Unit) {
//
//                    cartViewModel.syncOnAppStart()
//
//                }

                NavHost(
                    navController = navController,
                    startDestination = Screens.MainScreen.route
                ) {

                    composable(Screens.MainScreen.route){
                        MainScreen(
                            navController = navController
                        )
                    }

                    composable(Screens.Login.route){
                        LoginScreen(
                            navController = navController
                        )
                    }

                    composable(Screens.Home.route) {
                        HomeScreen(
                            navController = navController,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            },
                            onCartClick = {
                                // No table selected in Home, cart navigation might need a different logic if used.
                                // But showCart is false in HomeScreen's ScreenScaffold.
                            }
                        )
                    }


                    composable(
                        route = Screens.Products.route,
                        arguments = listOf(
                            navArgument("tableId") {
                                type = NavType.IntType
                            }
                        )
                    ) { backStackEntry ->

                        val tableId = backStackEntry.arguments?.getInt("tableId") ?: 0

                        ProductsScreen(
                            tableId = tableId,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            },
                            onCartClick = {
                                navController.navigate(Screens.Cart.createRoute(tableId))
                            },
                            navController = navController,


                        )
                    }



                    composable(Screens.Profile.route) {
                        ProfileScreen(
                            navController = navController,
                            onProfileClick = {
                                navController.navigate(Screens.Profile.route)
                            },
                            onCartClick = {
                             //   navController.navigate(Screens.Cart.route)

                            },
                            // (3) PASSA A FUNÇÃO PARA O COMPOSABLE
                            onPrintClick = {
                                printer.printTest()

                            },

                            printBarcode = {
                                printer.printBarcode("123456789012")
                            },

//                            printReceipt = {
//                                printer.printReceipt()
//                            },



                        isConnected = printer.isConnected.value


                        )
                    }


                    composable(
                        route = Screens.Cart.route,
                        arguments = listOf(
                            navArgument("tableId") {
                                type = NavType.IntType
                            }
                        )
                    ) { backStackEntry ->
                        val tableId = backStackEntry.arguments?.getInt("tableId") ?: 0

                        CartScreen(
                            tableId = tableId,
                            onCartClick = {
                                // Stay on Cart or navigate back
                            },
                            navController = navController,

                            //printer = printer
                        )
                    }




                }


            }
    }
}

    override fun onDestroy() {
        super.onDestroy()

        // (4) MUITO IMPORTANTE
        printer.disconnect()
    }
}
