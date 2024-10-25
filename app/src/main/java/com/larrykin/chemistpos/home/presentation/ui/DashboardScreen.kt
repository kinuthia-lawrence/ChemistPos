package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.DashboardViewModel

@Composable
fun DashboardScreen(
    loggedInUser: LoggedInUser,
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "HELLO ${loggedInUser.username.uppercase()},",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Welcome to ChemistPOS",
            style = MaterialTheme.typography.titleSmall
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}