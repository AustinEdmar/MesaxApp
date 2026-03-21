package com.austin.mesax.navigation

import com.austin.mesax.data.local.entity.TableEntity


sealed class Screens(val route: String) {


    object Cart: Screens("cart/{tableId}") {
        fun createRoute(tableId: Int) = "cart/$tableId"
    }

    object MainScreen: Screens("MainScreen")
    object Profile: Screens("profile")
    object Home: Screens("home")

    object SignUp: Screens("signup")
    object Login: Screens("login")
    object Categories: Screens("categories")

    object Products: Screens("products/{tableId}") {
        fun createRoute(tableId: Int) = "products/$tableId"
    }


}
