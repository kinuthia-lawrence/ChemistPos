package com.larrykin.chemistpos.core.domain

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

object CodeGenerator {
    private var generatedCode: String? = null

    // Generate and send code to admin email
    fun generateAndSendCode(scope: CoroutineScope, adminEmail: String, onResult: (String) -> Unit) {
        scope.launch {
            try {
                val adminEmailLower = adminEmail.trim().lowercase()
                generatedCode = Random.nextInt(100000, 999999).toString()
                // TODO: send email with code
                Log.d("CodeGenerator", "Generated code $generatedCode")
                onResult("Code sent to $adminEmail")
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }

    // Verify the input code
    fun verifyCode(inputCode: String, onResult: (Boolean) -> Unit) {
        onResult(inputCode == generatedCode)
    }
}