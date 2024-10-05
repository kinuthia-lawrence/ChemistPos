package com.larrykin.chemistpos.core.presentation.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class LoggedInUser(
    val username: String,
    val email: String,
)

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _loggedInUser = MutableStateFlow<LoggedInUser?>(null)
    val loggedInUser: StateFlow<LoggedInUser?> = _loggedInUser.asStateFlow()

    fun setLoggedInUser(user: LoggedInUser) {
        _loggedInUser.value = user
    }

    fun clearLoggedInUser() {
        _loggedInUser.value = null
    }
}
