package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.presentation.viewModels.NotificationViewModel

@Composable
fun NotificationScreen(
    loggedInUser: LoggedInUser,
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Expired Goods", "Out of Stock", "More")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        val expiredGoods by notificationViewModel.expiredGoods.collectAsState()
        val outOfStockGoods by notificationViewModel.outOfStockGoods.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> {
                    Text("Expired Goods", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                    expiredGoods.forEach { product ->
                        Log.d("Set Logs", "Expired Goods: $product")
                        ProductItem(product, loggedInUser, notificationViewModel)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                1 -> {
                    Text("Out of Stock Goods", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                    outOfStockGoods.forEach { product ->
                        Log.d("Set Logs", "Out of Stock Goods: $product")
                        ProductItem(product, loggedInUser, notificationViewModel)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                2 -> {
                    MoreScreen()
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    loggedInUser: LoggedInUser,
    notificationViewModel: NotificationViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${product.name}")
            Text(text = "Company: ${product.company}")
            Text(text = "Supplier: ${product.supplierName}")
            Text(text = "Formulation: ${product.formulation}")
            Text(text = "Minimum Stock: ${product.minStock}")
            Text(text = "Quantity Available: ${product.quantityAvailable}")
            Text(text = "Minimum Measure: ${product.minMeasure}")
            Text(text = "Date Added: ${product.dateAdded}")
            Text(text = "Expiry Date: ${product.expiryDate}")

            if (loggedInUser.role == Role.ADMIN) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { notificationViewModel.deleteProduct(product.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun MoreScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("No more features available for now :)", style = MaterialTheme.typography.titleLarge)
    }
}