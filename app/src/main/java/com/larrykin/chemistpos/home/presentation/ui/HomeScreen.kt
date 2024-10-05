package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(parentNavController: NavHostController = rememberNavController()) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val profileDrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Create an inner NavController for the inner content
    val innerNavController = rememberNavController()
    val currentBackStackEntry = innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route ?: "dashboard"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { SidebarContent() }
    ) {
        ModalNavigationDrawer(
            drawerState = profileDrawerState,
            drawerContent = { ProfileContent() }
        ) {
            Scaffold(
                topBar = {
                    StatusBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onProfileClick = { scope.launch { profileDrawerState.open() } },
                        onNotificationClick = { innerNavController.navigate("notification") },
                        onInfoClick = { innerNavController.navigate("help") }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        navController = innerNavController,
                        currentRoute = currentRoute
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Inner NavHost for handling inner navigation inside HomeScreen
                    NavHost(navController = innerNavController, startDestination = "dashboard") {
                        composable("dashboard") { DashboardScreen() }
                        composable("notification") { NotificationScreen() }
                        composable("help") { HelpScreen() }
                        composable("sales") { SalesScreen() }
                        composable("stock") { StockScreen() }
                        composable("services") { ServicesScreen() }
                        composable("cart") { CartScreen() }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    HomeScreen(navController)
}