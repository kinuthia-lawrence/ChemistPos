package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.AuthResult
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class settingsResult {
    data object Success : settingsResult()
    data class Error(val message: String) : settingsResult()
    data object UserExists : settingsResult()
    data object UserNotFound : settingsResult()
}


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // hold states
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var chemistName by mutableStateOf("")

    // create user
    fun createUser(onResult: (settingsResult) -> Unit) { // callback function to handle the result
        viewModelScope.launch {
            try {
                //convert email and username to lowercase
                val emailLower = email.trim().lowercase()
                val usernameLower = username.trim().lowercase()
                val phoneNumberTrim = phoneNumber.trim().toLong()
                val chemistNameTrim = chemistName.trim()

                //Remove trailing spaces after the domain in the email
                val emailProcessed = emailLower.replace(Regex("(?<=\\.com)\\s*$"), "")

                // check if it's the first user to be registered
                val allUsers =
                    when (val allUsersResult = userRepository.getAllUsers().firstOrNull()) {
                        is GetAllUsersResult.Success -> allUsersResult.users
                        is GetAllUsersResult.Error -> emptyList()
                        else -> emptyList()
                    }
                val role = if (allUsers.isEmpty()) Role.ADMIN else Role.USER
                val user = User(
                    emailProcessed,
                    usernameLower,
                    password,
                    phoneNumberTrim,
                    chemistNameTrim,
                    role
                )
                val result = userRepository.insertUser(user)
                if (result != null) {
                    onResult(settingsResult.Success)
                } else {
                    onResult(settingsResult.UserExists)
                }
            } catch (e: Exception) {
                onResult(settingsResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    //update user
    fun updateUser(onResult: (settingsResult) -> Unit) {
        viewModelScope.launch {
            try {
                val emailLower = email.trim().lowercase()
                val usernameLower = username.trim().lowercase()
                val phoneNumberTrim = phoneNumber.trim().toLong()
                val chemistNameTrim = chemistName.trim()

                val emailProcessed = emailLower.replace(Regex("(?<=\\.com)\\s*$"), "")

                val userExists = userRepository.getUserByEmail(emailProcessed)
                if (userExists == null) {
                    onResult(settingsResult.UserNotFound)
                    return@launch
                }
                if (userExists != null) {
                    //check the role of the user
                    val role = if (userExists.role == Role.ADMIN) Role.ADMIN else Role.USER

                    val user = User(
                        emailProcessed,
                        usernameLower,
                        password,
                        phoneNumberTrim,
                        chemistNameTrim,
                        role
                    )
                    //check if the user exists
                    val result = userRepository.updateUser(user)
                    if (result != null) {
                        onResult(settingsResult.Success)
                    } else {
                        onResult(settingsResult.Error("Unknown error updating user"))
                    }
                }
            } catch (e: Exception) {
                onResult(settingsResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    //getUsers
    fun getUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { result ->
                when (result) {
                    is GetAllUsersResult.Success -> onResult(result.users)
                    is GetAllUsersResult.Error -> onResult(emptyList())
                }
            }
        }
    }

    //delete user
    fun deleteUser(user: User, onResult:(Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.deleteUser(user)
            when (result) {
                is AuthResult.Success -> onResult(true)
                is AuthResult.Error -> onResult(false)
            }
        }
    }
}