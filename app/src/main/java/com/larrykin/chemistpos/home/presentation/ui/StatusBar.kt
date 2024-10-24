package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material.icons.sharp.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.home.presentation.viewModels.NotificationViewModel

@Composable
fun StatusBar(
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    val expiredGoods by notificationViewModel.expiredGoods.collectAsState()
    val outOfStockGoods by notificationViewModel.outOfStockGoods.collectAsState()

    val hasNotification = expiredGoods.isNotEmpty() || outOfStockGoods.isNotEmpty()
    Log.d("Set Logs", " StatusBar: $hasNotification")

    Spacer(modifier = Modifier.height(24.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 8.dp, end = 8.dp, bottom = 0.dp), // Added top padding
        verticalAlignment = Alignment.CenterVertically
    ) {

        //? Settings button on left
        IconButton(onClick = onMenuClick) {
            Icon(Icons.AutoMirrored.Rounded.List, contentDescription = "Menu")
        }
        // App name centered
        Text(
            text = "Chemist POS",
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
        // Tips icon
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Sharp.Info, contentDescription = "Information")
        }
        // Notification icon
        IconButton(onClick = onNotificationClick) {
            Log.d("Set Logs", " StatusBar clicked: $hasNotification")
            Icon(
                Icons.Sharp.Notifications,
                contentDescription = "Notifications",
                tint = if (hasNotification) Color.Red else Color.Unspecified
            )
        }
        // Profile icon
        IconButton(onClick = onProfileClick) {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    StatusBar()
}