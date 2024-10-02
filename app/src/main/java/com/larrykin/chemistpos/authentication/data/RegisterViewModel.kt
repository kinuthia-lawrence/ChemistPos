package com.larrykin.chemistpos.authentication.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class RegisterResult{
    object Success: RegisterResult()
    data class Error(val message: String): RegisterResult()
    object UserExists: RegisterResult()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
) :ViewModel() {
//    hold states
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var chemistName by mutableStateOf("")
    var role by  mutableStateOf(Role.ROLE_USER)


    fun register(onResult: (RegisterResult) -> Unit){ // callback function to handle the result
        viewModelScope.launch {
            try {
                val user = User(
                    email,
                    username,
                    password,
                    phoneNumber.toLong(),
                    chemistName,
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