package com.larrykin.chemistpos.core.naviagation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.larrykin.chemistpos.authentication.presentation.ui.ForgotPasswordScreen
import com.larrykin.chemistpos.authentication.presentation.ui.LoginAsAdminScreen
import com.larrykin.chemistpos.authentication.presentation.ui.LoginScreen
import com.larrykin.chemistpos.authentication.presentation.ui.RegisterScreen
import com.larrykin.chemistpos.authentication.presentation.ui.SplashScreen
import com.larrykin.chemistpos.authentication.presentation.viewModels.ForgotPasswordViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginAsAdminViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterViewModel
import com.larrykin.chemistpos.home.presentation.ui.HomeScreen
import com.larrykin.chemistpos.home.presentation.ui.MedicineScreen
import com.larrykin.chemistpos.home.presentation.ui.ServiceCrudScreen
import com.larrykin.chemistpos.home.presentation.ui.SettingsScreen
import com.larrykin.chemistpos.home.presentation.ui.SupplierScreen
import com.larrykin.chemistpos.home.presentation.viewModels.MedicineViewModel
import com.larrykin.chemistpos.home.presentation.viewModels.ServicesViewModel
import com.larrykin.chemistpos.home.presentation.viewModels.SettingsViewModel
import com.larrykin.chemistpos.home.presentation.viewModels.SupplierViewModel

//sealed class is a class that can only be inherited by classes declared in the same file
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object ForgotPassword : Screen("forgot_password")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object Settings: Screen("settings")
    data object LoginAsAdmin : Screen("login_as_admin")
    data object Medicines : Screen("medicines")
    data object Suppliers : Screen("suppliers")
    data object ServiceCrud : Screen("service_crud")
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
            val viewModel : SettingsViewModel = hiltViewModel()
            SettingsScreen( viewModel, navController)
        }
        composable(route = Screen.LoginAsAdmin.route) {
            val viewModel: LoginAsAdminViewModel = hiltViewModel()
            LoginAsAdminScreen(viewModel, navController)
        }
        composable(route= Screen.Medicines.route){
            val viewModel : MedicineViewModel = hiltViewModel()
            MedicineScreen(viewModel, navController)
        }
        composable(route= Screen.Suppliers.route){
            val viewModel : SupplierViewModel = hiltViewModel()
            SupplierScreen(viewModel, navController)
        }
        composable(route = Screen.ServiceCrud.route){
            val viewModel : ServicesViewModel = hiltViewModel()
            ServiceCrudScreen(viewModel,navController)
        }
    }
}
