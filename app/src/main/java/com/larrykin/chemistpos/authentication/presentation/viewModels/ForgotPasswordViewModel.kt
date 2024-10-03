package com.larrykin.chemistpos.authentication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class ForgotPasswordViewModel : ViewModel() {
    private var generatedCode: String? = null

    fun generateAndSendCode(adminEmail: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                generatedCode = Random.nextInt(100000, 999999).toString()
                //todo:send email with code
                Log.d("ForgotPasswordViewModel", "Generated code $generatedCode")
                onResult("Code sent to $adminEmail")
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }

    fun verifyCode(inputCode: String, onResult: (Boolean) -> Unit) {
        onResult(inputCode == generatedCode)
    }

    fun resetPassword(email: String, newPassword: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // todo: update password in database
                onResult("Success: Password reset successfully for $email")
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }
}