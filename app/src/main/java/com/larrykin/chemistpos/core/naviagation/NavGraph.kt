package com.larrykin.chemistpos.core.naviagation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.ForgotPasswordViewModel
import com.larrykin.chemistpos.authentication.presentation.ui.ForgotPasswordScreen
import com.larrykin.chemistpos.authentication.presentation.ui.LoginScreen
import com.larrykin.chemistpos.authentication.presentation.ui.RegisterScreen
import com.larrykin.chemistpos.authentication.presentation.ui.SplashScreen
import com.larrykin.chemistpos.home.presentation.ui.HomeScreen

//sealed class is a class that can only be inherited by classes declared in the same file
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Register : Screen("register")
    object Home : Screen("home")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(route = Screen.Login.route) {
            //Obtain the loginViewModel within the composable function
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(viewModel, navController)
        }
        composable(route = Screen.ForgotPassword.route){
            //Obtain the forgotPasswordViewModel within the composable function
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            ForgotPasswordScreen(viewModel,navController)
        }
        composable(route = Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(viewModel, navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
    }
}
