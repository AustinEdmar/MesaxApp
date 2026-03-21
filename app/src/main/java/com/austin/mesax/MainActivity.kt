package com.austin.mesax


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.austin.mesax.navigation.Screens
import com.austin.mesax.screens.MainScreen
import com.austin.mesax.screens.auth.LoginScreen
import com.austin.mesax.screens.home.CartScreen


import com.austin.mesax.screens.home.HomeScreen
import com.austin.mesax.screens.home.ProductsScreen

import com.austin.mesax.screens.profile.ProfileScreen

import com.austin.mesax.ui.theme.ShopEasyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            ShopEasyTheme(){
                val navController = rememberNavController()

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
                            }

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
                            navController = navController
                        )
                    }




                }


            }
    }
}
}
