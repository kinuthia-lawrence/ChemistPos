package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel
import kotlinx.coroutines.launch

@Composable
fun CartScreen(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    val cartItems = stockViewModel.cart.collectAsState().value
    var expectedAmount by remember { mutableStateOf(0.0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Column(modifier = Modifier.padding(16.dp)) {
        cartItems.forEach { item ->
            var quantity by remember { mutableStateOf(1) }
            val availableQuantity = item.quantityAvailable / item.minMeasure


            LaunchedEffect(quantity) {
                expectedAmount =
                    cartItems.sumOf { it.retailSellingPrice * quantity * item.minMeasure }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Name: ${item.name}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Company: ${item.company}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Retail Price: @Ksh${item.retailSellingPrice}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Wholesale Price: @Ksh${item.wholesaleSellingPrice}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Min Measure: ${item.minMeasure}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Stock Available: ${item.quantityAvailable}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sellable Quantity: ${availableQuantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { stockViewModel.removeFromCart(item) }) {
                            Text("Remove")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Decrement")
                            }
                            Text(text = "$quantity", modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = {
                                if (quantity < availableQuantity) {
                                    quantity++
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Quantity exceeds available stock")
                                    }
                                }
                            }) {
                                Icon(Icons.Default.AddCircle, contentDescription = "Increment")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Snackbar Host
            SnackbarHost(hostState = snackbarHostState)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Section
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Expected Amount: @Ksh$expectedAmount",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = "",
                onValueChange = {},
                labelText = "Cash",
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = "",
                onValueChange = {},
                labelText = "Mpesa",
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = "",
                onValueChange = {},
                labelText = "Discount",
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = "",
                onValueChange = {},
                labelText = "Credit",
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Handle sell action */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Sell")
            }
        }


    }
}