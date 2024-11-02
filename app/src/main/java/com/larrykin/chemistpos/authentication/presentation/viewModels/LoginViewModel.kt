package com.larrykin.chemistpos.authentication.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.LoggedInUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object UserNotFound : LoginResult()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var loggedInUser by mutableStateOf<LoggedInUser?>(null) // Add this property

    fun login(onResult: (LoginResult) -> Unit) {
        viewModelScope.launch {
            try {
                val usernameLower = username.trim().lowercase()
                val user = repository.loginUser(usernameLower, password)
                if (user != null) {
                    loggedInUser = LoggedInUser(
                        username = user.username,
                        email = user.email,
                        role = user.role,
                        chemistName = user.chemistName,
                        phoneNumber = user.phoneNumber,
                        createdAt = user.createdAt
                    )
                    onResult(LoginResult.Success(user))
                } else {
                    val emailLower = username.trim().lowercase()
                    val userByEmail = repository.loginUserByEmail(emailLower, password)
                    if (userByEmail != null) {
                        loggedInUser = LoggedInUser(
                            username = userByEmail.username,
                            email = userByEmail.email,
                            role = userByEmail.role,
                            chemistName = userByEmail.chemistName,
                            phoneNumber = userByEmail.phoneNumber,
                            createdAt = userByEmail.createdAt
                        )
                        onResult(LoginResult.Success(userByEmail))
                    } else {
                        onResult(LoginResult.UserNotFound)
                    }
                }
            } catch (e: Exception) {
                onResult(LoginResult.Error(e.message ?: "Unknown error"))
            }
        }
    }
    fun onLogout(navController: NavController) {
        loggedInUser = null
        navController.navigate("login") {
            popUpTo(0) // Clear the back stack
            launchSingleTop = true // Prevents creating duplicate instances of the login screen
        }
    }
}