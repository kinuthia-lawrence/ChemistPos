package com.larrykin.chemistpos.authentication.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel //this marks the viewModel for Hilt to inject dependencies
class LoginViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    //hold states
    var username: String = ""
    var password: String = ""
    var loginSuccess: Boolean = false

    fun login() {
        viewModelScope.launch {
            val user =
                repository.loginUser(username, password) //call the login method with a coroutine.
            loginSuccess = user != null
        }
    }
}