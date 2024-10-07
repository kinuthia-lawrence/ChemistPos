package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.ProfileViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun HomeScreen(
    parentNavController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val profileDrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Create an inner NavController for the inner content
    val innerNavController = rememberNavController()
    val currentBackStackEntry = innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route ?: "dashboard"

// Get user profile from profileViewModel
    val loggedInUser = loginViewModel.loggedInUser
    val profileViewModel: ProfileViewModel = hiltViewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.padding(end = 120.dp)
            ) {
                MenuContent(
                    parentNavController = parentNavController
                )
            }
        }
    ) {
        ModalNavigationDrawer(
            drawerState = profileDrawerState,
            drawerContent = {
                loggedInUser?.let { user ->
                    ProfileContent(
                        userProfile = user,
                        onLogout = { loginViewModel.onLogout(parentNavController) },
                        profileViewModel = profileViewModel,
                        parentNavController = parentNavController
                    )
                } ?: run {
                    // Show placeholder
                    ProfileContent(
                        userProfile = LoggedInUser(
                            username = "Username",
                            email = "example@gmail.com",
                            role = Role.ADMIN,
                            chemistName = "Chemist Name",
                            phoneNumber = 748590146,
                            createdAt = Date(2024, 10, 10),
                        ),
                        onLogout = { loginViewModel.onLogout(parentNavController) },
                        profileViewModel = profileViewModel,
                        parentNavController = parentNavController
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    StatusBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onProfileClick = { scope.launch { profileDrawerState.open() } },
                        onNotificationClick = {
                            innerNavController.navigate("notification") {
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                        onInfoClick = {
                            innerNavController.navigate("help") {
                                popUpTo(innerNavController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavigationBar(
                        navController = innerNavController,
                        currentRoute = currentRoute
                    )
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Inner NavHost for handling inner navigation inside HomeScreen
                    item {
                        NavHost(
                            navController = innerNavController,
                            startDestination = "dashboard"
                        ) {
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
}

@Preview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    HomeScreen(navController, loginViewModel)
}