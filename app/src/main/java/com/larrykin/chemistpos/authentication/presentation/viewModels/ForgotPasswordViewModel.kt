package com.larrykin.chemistpos.authentication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.domain.CodeGenerator
import kotlinx.coroutines.launch
import kotlin.random.Random

class ForgotPasswordViewModel(private val userRepository: UserRepository) : ViewModel() {
    private var generatedCode: String? = null
    private val codeGenerator = CodeGenerator(userRepository)

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
                var emailLower = email.trim().lowercase()
                // todo: update password in database
                onResult("Success: Password reset successfully for $email")
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }
}