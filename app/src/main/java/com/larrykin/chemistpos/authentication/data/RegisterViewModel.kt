package com.larrykin.chemistpos.authentication.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
    object UserExists : RegisterResult()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    // hold states
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var chemistName by mutableStateOf("")

    fun register(onResult: (RegisterResult) -> Unit) { // callback function to handle the result
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
                val allUsers = when (val allUsersResult = repository.getAllUsers().firstOrNull()) {
                    is GetAllUsersResult.Success -> allUsersResult.users
                    is GetAllUsersResult.Error -> emptyList()
                    else -> emptyList()
                }
                val role = if (allUsers.isNullOrEmpty()) Role.ADMIN else Role.USER
                val user = User(
                    emailProcessed,
                    usernameLower,
                    password,
                    phoneNumberTrim,
                    chemistNameTrim,
                    role
                )
                val result = repository.insertUser(user)
                if (result != null) {
                    onResult(RegisterResult.Success)
                } else {
                    onResult(RegisterResult.UserExists)
                }
            } catch (e: Exception) {
                onResult(RegisterResult.Error(e.message ?: "Unknown error"))
            }
        }
    }
}