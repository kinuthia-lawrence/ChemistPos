package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MenuContent(
    parentNavController: NavHostController
) {
    Spacer(modifier = Modifier.height(48.dp))
    Text("Menu", modifier = Modifier.padding(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    NavigationDrawerItem(
        label = { Text(text = "Suppliers") },
        icon = { Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Supplier " +
                "Icon") },
        selected = false,
        onClick = { parentNavController.navigate("suppliers") }
    )
    Spacer(modifier = Modifier.height(16.dp))
    NavigationDrawerItem(
        label = { Text(text = "Medicines") },
        icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Medicines Icon") },
        selected = false,
        onClick = { parentNavController.navigate("medicines") }
    )
    Spacer(modifier = Modifier.height(16.dp))
    NavigationDrawerItem(
        label = { Text(text = "Services") },
        icon = { Icon(imageVector = Icons.Default.Build, contentDescription = "Services Icon") },
        selected = false,
        onClick = { parentNavController.navigate("service_crud") }
    )
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    NavigationDrawerItem(
        label = { Text(text = "Settings") },
        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings Icon", tint = Color.Red)
             },
        selected = false,
        onClick = { parentNavController.navigate("login_as_admin") }
    )
}

@Preview
@Composable
fun SidebarContentPreview() {
    val parentNavController = NavHostController(LocalContext.current)
    MenuContent(parentNavController)
}