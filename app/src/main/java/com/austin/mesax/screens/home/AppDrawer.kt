package com.austin.mesax.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    currentRoute: String = "home",
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header do Drawer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(24.dp)
        ) {
            Text(
                text = "Oi Garçom",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bem-vindo ao seu aplicativo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Itens do menu
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            selected = currentRoute == "home",
            onClick = { onItemClick("home") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Pedidos"
                )
            },
            label = {
                Text(
                    text = "Pedidos",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            selected = currentRoute == "orders",
            onClick = { onItemClick("orders") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            selected = currentRoute == "profile",
            onClick = { onItemClick("profile") },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 3.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            text = "Versão 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 64.dp, start = 16.dp, top = 16.dp)
        )
    }
}