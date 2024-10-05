package com.larrykin.chemistpos.authentication.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
    object UserNotFound : LoginResult()
}


@HiltViewModel //this marks the viewModel for Hilt to inject dependencies
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    //hold states
    var username by mutableStateOf("")
    var password by mutableStateOf("")


    fun login(onResult: (LoginResult) -> Unit) {
        viewModelScope.launch {
            try {
                val usernameLower = username.trim().lowercase()
                val user = repository.loginUser(usernameLower, password)
                if (user != null) {
                    onResult(LoginResult.Success(user)) // Return user details
                } else {
                    val emailLower = username.trim().lowercase()
                    val userByEmail = repository.loginUserByEmail(emailLower, password)
                    if (userByEmail != null) {
                        onResult(LoginResult.Success(userByEmail)) // Return user details
                    } else {
                        onResult(LoginResult.UserNotFound)
                    }
                }
            } catch (e: Exception) {
                onResult(LoginResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

}