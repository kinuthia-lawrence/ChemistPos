package com.larrykin.chemistpos.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.LoggedInUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Variable to hold the user profile
    private var _user: LoggedInUser? = null

    // Function to set the user profile
    fun setUser(loggedInUser: LoggedInUser) {
        _user = loggedInUser
        Log.d("LoggedInUser", " in setUser LoggedIn User set: $_user")
        checkUser()
    }

    private fun checkUser() {
        Log.d("LoggedInUser", "Checking user: $_user")
    }

    // Function to get the user profile
    fun getUser(): LoggedInUser? {
        return _user
        checkUser()
    }

    // Function to clear the user profile
    fun onLogout(navController: NavController) {
        Log.d("LoggedInUser", "Logging out user: $_user")
        _user = null
        navController.navigate("login") {
            popUpTo(0) // Clear the back stack
            launchSingleTop = true // Prevents creating duplicate instances of the login screen
        }
        Log.d("LoggedInUser", "Logout successful in profileViewModel")
        checkUser()
    }
}