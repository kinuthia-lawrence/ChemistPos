package com.larrykin.chemistpos.core.naviagation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.larrykin.chemistpos.authentication.presentation.ui.ForgotPasswordScreen
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.ForgotPasswordViewModel
import com.larrykin.chemistpos.authentication.presentation.ui.LoginScreen
import com.larrykin.chemistpos.authentication.presentation.ui.RegisterScreen
import com.larrykin.chemistpos.authentication.presentation.ui.SplashScreen
import com.larrykin.chemistpos.home.presentation.ui.DashboardScreen
import com.larrykin.chemistpos.home.presentation.ui.HelpScreen
import com.larrykin.chemistpos.home.presentation.ui.HomeScreen
import com.larrykin.chemistpos.home.presentation.ui.NotificationScreen
import com.larrykin.chemistpos.home.presentation.ui.SettingsScreen

//sealed class is a class that can only be inherited by classes declared in the same file
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object ForgotPassword : Screen("forgot_password")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Settings: Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(route = Screen.Login.route) {
           val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(loginViewModel, navController)
        }
        composable(route = Screen.ForgotPassword.route) {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(viewModel, navController)
        }
        composable(route = Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(viewModel, navController)
        }
        composable(route = Screen.Home.route) {
            // Obtain the LoginViewModel from the parent NavBackStackEntry
            val loginViewModel: LoginViewModel = hiltViewModel(navController.getBackStackEntry(Screen.Login.route))
            HomeScreen(navController, loginViewModel)
        }
        composable(route= Screen.Settings.route){
            SettingsScreen()
        }
    }
}
