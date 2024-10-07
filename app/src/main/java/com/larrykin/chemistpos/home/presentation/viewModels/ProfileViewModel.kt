package com.larrykin.chemistpos.home.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.domain.UserRepository
import com.larrykin.chemistpos.core.data.LoggedInUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun updateProfilePicture(email: String, newUrl: String) {
        viewModelScope.launch {
            val user = userRepository.updateProfilePicture(email,newUrl)
        }
    }

}