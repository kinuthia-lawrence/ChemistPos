package com.larrykin.chemistpos.authentication.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import com.larrykin.chemistpos.core.domain.CodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val codeGenerator = CodeGenerator(repository)

    //function to check if there exists an admin user
    fun checkIfAdminExists(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val allUsers = when (val allUsersResult = repository.getAllUsers().firstOrNull()) {
                is GetAllUsersResult.Success -> allUsersResult.users
                is GetAllUsersResult.Error -> emptyList()
                else -> emptyList()
            }
            val adminUser = allUsers.find { it.role == Role.ADMIN }
            onResult(adminUser != null)
        }
    }

    // generate and send code to the admin email
    fun generateCodeAndSendCode(adminEmail: String, onResult: (String) -> Unit) {
        codeGenerator.generateAndSendCode(viewModelScope, adminEmail, onResult)
    }

    // verify the input code
    fun verifyCode(inputCode: String, onResult: (Boolean) -> Unit) {
        codeGenerator.verifyCode(inputCode, onResult)
    }

    // reset the password
    fun resetPassword(email: String, newPassword: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val emailLower = email.trim().lowercase()
                val user = repository.getUserByEmail(emailLower)
                if (user != null) {
                    val updatedUser = user.copy(password = newPassword)
                    val result = repository.updateUser(updatedUser)
                    if (result != null) {
                        onResult("Success: Password reset successfully for $email")
                    } else {
                        onResult("Error: Failed to update password")
                    }
                } else {
                    onResult("Error: User with email $email not found")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }
}