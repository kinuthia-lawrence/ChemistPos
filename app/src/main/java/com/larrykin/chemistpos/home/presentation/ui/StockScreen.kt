package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel

@Composable
fun StockScreen(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Available Stock", "Add Stock")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        // Use Box to avoid infinite height constraints
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> AvailableStockContent(loggedInUser, stockViewModel)
                1 -> AddStockContent(loggedInUser, stockViewModel)
            }
        }
    }
}

@Composable
fun AvailableStockContent(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    val stockItems = listOf(
        "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1",
        "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1",
        "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1", "Item 2", "Item 3", "Item 1",
        "Item 2", "Item 3", "Item 1", "Item 2", "Item 3"
    ) //

    Column {
        stockItems.forEach { item ->
            Text(text = item, modifier = Modifier.padding(vertical = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AddStockContent(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    // Replace with your content for adding stock
    Text(text = "Add Stock Content", modifier = Modifier.padding(16.dp))
}