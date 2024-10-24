package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.ServicesViewModel

@Composable
fun ServicesScreen(
    loggedInUser: LoggedInUser,
    servicesViewModel: ServicesViewModel = hiltViewModel()
){
    Text(text = "Hello $loggedInUser")
}