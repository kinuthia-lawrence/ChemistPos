package com.larrykin.chemistpos.authentication.presentation.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class results {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
    data object UserNotFound : LoginResult()
    data object UserNotAdmin : LoginResult()
}

@HiltViewModel
class LoginAsAdminViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")


    fun loginAsAdmin(onResult: (LoginResult) -> Unit) {
        viewModelScope.launch {
            try {
                val usernameLower = username.trim().lowercase()
                val user = repository.loginUser(usernameLower, password)
                if (user != null) {
                    //check if user role is admin
                    if (user.role == Role.ADMIN) {
                        onResult(results.Success(user))
                    } else {
                        onResult(results.UserNotAdmin)
                    }
                } else {
                    val emailLower = username.trim().lowercase()
                    val userByEmail = repository.loginUserByEmail(emailLower, password)
                    if (userByEmail != null) {
                        if (userByEmail.role == Role.ADMIN) {
                            onResult(results.Success(userByEmail))
                        } else {
                            onResult(results.UserNotAdmin)
                        }
                    } else {
                        onResult(results.UserNotFound)
                    }
                }
            } catch (e: Exception) {
                onResult(results.Error(e.message ?: "Unknown error"))
            }
        }

    }
}