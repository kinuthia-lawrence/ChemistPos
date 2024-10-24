package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialogWithChoice
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.presentation.viewModels.NotificationViewModel

@Composable
fun NotificationScreen(
    loggedInUser: LoggedInUser,
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Expired Goods", "Out of Stock", "More")

    val expiredGoods by notificationViewModel.expiredGoods.collectAsState()
    val outOfStockGoods by notificationViewModel.outOfStockGoods.collectAsState()

    val hasExpiredGoods = expiredGoods.isNotEmpty()
    val hasOutOfStockGoods = outOfStockGoods.isNotEmpty()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
        }

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                val textColor = when (index) {
                    0 -> if (hasExpiredGoods) Color.Red else Color.Unspecified
                    1 -> if (hasOutOfStockGoods) Color.Red else Color.Unspecified
                    else -> Color.Unspecified
                }
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, color = textColor) }
                )
            }
        }

        val expiredGoods by notificationViewModel.expiredGoods.collectAsState()
        val outOfStockGoods by notificationViewModel.outOfStockGoods.collectAsState()

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> {
                    Text(
                        "Expired Goods",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    expiredGoods.forEach { product ->
                        Log.d("Set Logs", "Expired Goods: $product")
                        ProductItem(product, loggedInUser, isExpired = true, notificationViewModel)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                1 -> {
                    Text(
                        "Out of Stock Goods",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    outOfStockGoods.forEach { product ->
                        Log.d("Set Logs", "Out of Stock Goods: $product")
                        ProductItem(product, loggedInUser, isExpired = false, notificationViewModel)
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
    isExpired: Boolean,
    notificationViewModel: NotificationViewModel
) {
    var showDeleteChoiceDialog by remember { mutableStateOf(false) }

    // Delete choice dialog
    if (showDeleteChoiceDialog) {
        CustomAlertDialogWithChoice(
            title = "Delete Product",
            message = "Are you sure you want to delete this product?, this action cannot be undone",
            onDismiss = { showDeleteChoiceDialog = false },
            onConfirm = {
                notificationViewModel.deleteProduct(product.id){ result ->
                    if (result) {
                        showDeleteChoiceDialog = false
                    }
                }
            },
            alertState = "confirm"
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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

            if (loggedInUser.role == Role.ADMIN && isExpired) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { showDeleteChoiceDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            if (!isExpired) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Update the product Quantity Available",
                    style = MaterialTheme.typography.titleSmall
                )
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