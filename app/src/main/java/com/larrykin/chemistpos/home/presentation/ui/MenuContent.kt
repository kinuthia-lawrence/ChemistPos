package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MenuContent() {
    Spacer(modifier = Modifier.height(48.dp))
    Text("Menu", modifier = Modifier.padding(16.dp))
    HorizontalDivider()
    NavigationDrawerItem(
        label = { Text(text = "Settings") },
        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings Icon") },
        selected = false,
        onClick = { /*TODO*/ }
    )
}

@Preview
@Composable
fun SidebarContentPreview() {
    MenuContent()
}