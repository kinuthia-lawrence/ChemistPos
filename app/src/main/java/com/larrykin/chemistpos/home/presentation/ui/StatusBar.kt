package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material.icons.sharp.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StatusBar() {
    Spacer(modifier = Modifier.height(24.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 8.dp, end = 8.dp, bottom = 0.dp), // Added top padding
        verticalAlignment = Alignment.CenterVertically
    ) {

        //? Settings button on left
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
        // App name centered
        Text(
            text = "Chemist POS",
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        // Tips icon
        IconButton(onClick = { /* Handle notifications click */ }) {
            Icon(Icons.Sharp.Info, contentDescription = "Notifications")
        }
        // Notification icon
        IconButton(onClick = { /* Handle notifications click */ }) {
            Icon(Icons.Sharp.Notifications, contentDescription = "Notifications")
        }
        // Profile icon
        IconButton(onClick = { /* Handle profile click */ }) {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StatusBar()
}