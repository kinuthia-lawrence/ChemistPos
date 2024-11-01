package com.larrykin.chemistpos.authentication.presentation.viewModels

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.GetAllUsersResult
import com.larrykin.chemistpos.core.domain.CodeGenerator
import com.larrykin.chemistpos.core.networkUtils.ConnectivityReceiver
import com.larrykin.chemistpos.core.networkUtils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Date

sealed class RegisterResult {
    data object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
    data object UserExists : RegisterResult()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository,
    private val context: Context
) : ViewModel() {
    // hold states
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var chemistName by mutableStateOf("")

    private val codeGenerator = CodeGenerator(repository)

    // function to generate and send code to admin email
    fun generateAndSendCode(adminEmail: String, onResult: (String) -> Unit) {
        codeGenerator.generateAndSendCode(viewModelScope, adminEmail, onResult)
    }

    // verify the input code
    fun verifyCode(inputCode: String, onResult: (Boolean) -> Unit) {
        codeGenerator.verifyCode(inputCode, onResult)
    }

    // function to register a new user
    fun register(onResult: (RegisterResult) -> Unit) { // callback function to handle the result
        viewModelScope.launch {
            try {
                // convert email and username to lowercase
                val emailLower = email.trim().lowercase()
                val usernameLower = username.trim().lowercase()
                val phoneNumberTrim = phoneNumber.trim().toLong()
                val chemistNameTrim = chemistName.trim()

                // Remove trailing spaces after the domain in the email
                val emailProcessed = emailLower.replace(Regex("(?<=\\.com)\\s*$"), "")

                // check if it's the first user to be registered
                val allUsers = when (val allUsersResult = repository.getAllUsers().firstOrNull()) {
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
                val result = repository.insertUser(user)
                if (result != null) {
                    onResult(RegisterResult.Success)
                    if (role == Role.ADMIN) {
                        if (NetworkUtils.isInternetAvailable(context)) {
                            val firestoreResults = repository.createUserInFirestore(user)

                            if (firestoreResults == false) {
                                handleOfflineScenario(user)
                                Log.d("MyLogs", "RegisterViewModel : handleOfflineScenario invoked")
                            } else {
                                Log.d("MyLogs", "RegisterViewModel : User created in firestore")
                            }
                        } else {
                            handleOfflineScenario(user)
                        }
                    }
                } else {
                    onResult(RegisterResult.UserExists)
                }
            } catch (e: Exception) {
                onResult(RegisterResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun handleOfflineScenario(user: User) {
        // sync when internet is available
        val connectivityReceiver = ConnectivityReceiver {
            viewModelScope.launch {
                when (val allUsersResult = repository.getAllUsers().firstOrNull()) {
                    is GetAllUsersResult.Success -> {
                        val adminUser = allUsersResult.users.find { it.role == Role.ADMIN }
                        if (adminUser != null) {
                            val firestoreResults = repository.createUserInFirestore(adminUser)
                            if (firestoreResults == true) {
                                Log.d("MyLogs", "RegisterViewModel : User created in firestore")
                            }
                        }
                    }

                    is GetAllUsersResult.Error -> {
                        Log.e("MyLogs", "Error fetching users: ${allUsersResult.message}")
                    }

                    else -> {
                        Log.e("MyLogs", "Unknown error fetching users")
                    }
                }
            }
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(connectivityReceiver, intentFilter)
    }
}