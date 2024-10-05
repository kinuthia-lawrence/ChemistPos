package com.larrykin.chemistpos.home.presentation.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.presentation.viewModels.LoggedInUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository // Repository to interact with user data
) : ViewModel() {

    // State for the user profile
    var user = mutableStateOf<LoggedInUser?>(null)
        private set

    init {
        // Load the current user's profile when the view model is initialized
        loadUserProfile()
    }

    // Function to load user profile data
    private fun loadUserProfile() {
        viewModelScope.launch {
            // Fetch the currently logged-in user (implementation may vary)
//            user.value = userRepository.getCurrentUser() // Assume this method retrieves the current user
        }
    }

    // Function to handle user edit action
    fun editUserDetails(newUsername: String, newEmail: String) {
        viewModelScope.launch {
            // Update user details in the repository
//            userRepository.updateUserDetails(newUsername, newEmail)
            // Reload the user profile to get updated data
            loadUserProfile()
        }
    }

    // Function to handle user logout
    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
//            userRepository.logoutUser()
            onLogout() // Call the logout callback
        }
    }
//    fun onLogout(navController: NavController, authViewModel: AuthViewModel) {
//        authViewModel.clearLoggedInUser() // Clear user session
//        navController.navigate("login") {
//            popUpTo(0) // Clear the back stack
//        }
//    }

}
