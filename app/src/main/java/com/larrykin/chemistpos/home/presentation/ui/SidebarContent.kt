package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SidebarContent(
    modifier: Modifier = Modifier.background(color = Color.Blue).padding(top = 48.dp)
) {
    Text(text ="Sidebar Content")
}

@Preview
@Composable
fun SidebarContentPreview() {
    SidebarContent()
}