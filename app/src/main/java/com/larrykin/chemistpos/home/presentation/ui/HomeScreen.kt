package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.core.presentation.viewModels.AuthViewModel
import com.larrykin.chemistpos.core.presentation.viewModels.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.ProfileViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun HomeScreen(
    parentNavController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val profileDrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Create an inner NavController for the inner content
    val innerNavController = rememberNavController()
    val currentBackStackEntry = innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route ?: "dashboard"

    // Collect the logged-in user from the AuthViewModel
    val loggedInUser by authViewModel.loggedInUser.collectAsState()

    // Load the user profile in the ProfileViewModel
    loggedInUser?.let { user ->
        profileViewModel.user.value = LoggedInUser(
            username = user.username,
            email = user.email,
            role = user.role,
            chemistName = user.chemistName,
            phoneNumber = user.phoneNumber,
            createdAt = user.createdAt,
//            profilePictureUrl = user.profilePictureUrl //todo: fetch profile picture
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.padding(end = 120.dp)
            ) {
                MenuContent()
            }
        }
    ) {
        ModalNavigationDrawer(
            drawerState = profileDrawerState,
            drawerContent = {
                loggedInUser?.let { user ->
                    ProfileContent(
                        userProfile = LoggedInUser(
                            username = user.username,
                            email = user.email,
                            role = user.role,
                            chemistName = user.chemistName,
                            phoneNumber = user.phoneNumber,
                            createdAt = user.createdAt,
//                            profilePictureUrl = user.profilePictureUrl
                        ),
                        onEdit = { /*todo: Navigate to edit profile screen */ },
                        onLogout = { onLogout(parentNavController, authViewModel) }
                    )
                } ?: run {
                    //show placeholder
                    ProfileContent(
                        userProfile = LoggedInUser(
                            username = "Username",
                            email = "example@gmail.com",
                            role = Role.ADMIN,
                            chemistName = "Chemist Name",
                            phoneNumber = 748590146,
                            createdAt =  Date(2024, 10, 10),
                        ),
                        onEdit = { /*TODO*/ },
                        onLogout = { onLogout(parentNavController, authViewModel) }
                    )
                }

            }
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

// Logout method
fun onLogout(navController: NavController, authViewModel: AuthViewModel) {


    authViewModel.clearLoggedInUser() // Clear user session
    navController.navigate("login") {
        popUpTo(0) // Clear the back stack
        launchSingleTop = true // Prevents creating duplicate instances of the login screen
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    HomeScreen(navController)
}