package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.data.SaleItem
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel
import kotlinx.coroutines.launch

@Composable
fun CartScreen(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    val cartItems = stockViewModel.cart.collectAsState().value
    val quantityMap by stockViewModel.quantityMap.collectAsState()
    val expectedAmount by stockViewModel.expectedAmount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var cash by remember { mutableStateOf("") }
    var mpesa by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var credit by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp))  {
        cartItems.forEach { item ->
            var quantity = quantityMap[item] ?: 1
            val availableQuantity = item.quantityAvailable / item.minMeasure

//            LaunchedEffect(quantity) {
//                stockViewModel.setExpectedAmount(cartItems.sumOf {
//                    it.retailSellingPrice *
//                            quantity
//                })
//            }

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
                        Button(onClick = {
                            stockViewModel.removeFromCart(item, quantity)
                            stockViewModel.setExpectedAmount(stockViewModel.cart.value.sumOf {
                                it
                                    .retailSellingPrice * (quantityMap[it] ?: 1)
                            })
                        }) {
                            Text("Remove")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (quantity > 1) {
                                    stockViewModel.updateQuantity(item, quantity - 1)
                                }
                            }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Decrement")
                            }
                            Text(text = "$quantity", modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = {
                                if (quantity < availableQuantity) {
                                    stockViewModel.updateQuantity(item, quantity + 1)
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
            // Snackbar Host
            SnackbarHost(hostState = snackbarHostState)
            Text(
                text = "Expected Amount: @Ksh$expectedAmount",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = cash,
                onValueChange = { cash = it },
                labelText = "Cash",
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.AddCircle,
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = mpesa,
                onValueChange = { mpesa = it },
                labelText = "Mpesa",
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.AddCircle,
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = discount,
                onValueChange = { discount = it },
                labelText = "Discount",
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.AddCircle,
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = credit,
                onValueChange = { credit = it },
                labelText = "Credit",
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.AddCircle,
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                enabled = !cartItems.isEmpty(),
                onClick = {
                val saleItems = cartItems.map { item ->
                    SaleItem(
                        productId = item.id,
                        quantity = quantityMap[item] ?: 1
                    )
                }
                val cashAmount = cash.toDoubleOrNull() ?: 0.0
                val mpesaAmount = mpesa.toDoubleOrNull() ?: 0.0
                val totalPrice = cashAmount + mpesaAmount

                stockViewModel.saveSales(
                    items = saleItems,
                    totalPrice = totalPrice,
                    expectedAmount = expectedAmount,
                    cash = cashAmount,
                    mpesa = mpesaAmount,
                    discount = discount.toDoubleOrNull() ?: 0.0,
                    credit = credit.toDoubleOrNull() ?: 0.0,
                    seller = loggedInUser.email
                ) { success ->
                    if (success) {
                        stockViewModel.clearCart()
                        cash = ""
                        mpesa = ""
                        discount = ""
                        credit = ""
                        scope.launch {
                            snackbarHostState.showSnackbar("Sale successful")
                        }
                        //Log the data
                        Log.d("CartScreen", "Sale successful: $saleItems")

                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Sale failed")
                        }
                    }

                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Sell")
            }
        }
    }
}