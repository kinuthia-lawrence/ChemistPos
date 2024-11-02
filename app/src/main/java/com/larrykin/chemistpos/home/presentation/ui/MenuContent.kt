package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.larrykin.chemistpos.home.presentation.viewModels.MenuContentViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuContent(
    parentNavController: NavHostController,
    menuContentViewModel: MenuContentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    Spacer(modifier = Modifier.height(48.dp))
    Text("Menu", modifier = Modifier.padding(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))
    NavigationDrawerItem(
        label = { Text(text = "Suppliers") },
        icon = {
            Icon(
                imageVector = Icons.Default.ShoppingCart, contentDescription = "Supplier " +
                        "Icon"
            )
        },
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
        label = { Text(text = "Sync Data") },
        icon = {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Sync Icon",
                tint = Color.Green
            )
        },
        selected = false,
        onClick = {
            scope.launch {
                val (adminEmail, subscriptionStatus) = menuContentViewModel.getAdminEmailAndStatus()
                if (subscriptionStatus == "active") {
                    menuContentViewModel.syncData(adminEmail.toString())
                } else {
                    Log.e("MyLogs", "Subscription status is not active: $subscriptionStatus")
                }
            }
        }
    )
    Spacer(modifier = Modifier.height(16.dp))
    NavigationDrawerItem(
        label = { Text(text = "Settings") },
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings Icon",
                tint = Color.Red
            )
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