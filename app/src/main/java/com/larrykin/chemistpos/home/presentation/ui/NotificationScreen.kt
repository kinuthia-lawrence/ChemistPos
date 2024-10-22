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
    var selectedSection by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            IconButton(onClick = { selectedSection = 0 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    Text(text = "Expired", modifier = Modifier.padding(top = 8.dp))
                }
            }
            IconButton(onClick = { selectedSection = 1 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text(text = "Out of Stock", modifier = Modifier.padding(top = 8.dp))
                }
            }
            IconButton(onClick = { selectedSection = 2 }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    Text(text = "More", modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        val expiredGoods by notificationViewModel.expiredGoods.collectAsState()
        val outOfStockGoods by notificationViewModel.outOfStockGoods.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedSection) {
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
            Text(text = "Formulation: ${product.formulation}")
            Text(text = "Minimum Stock: ${product.minStock}")

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