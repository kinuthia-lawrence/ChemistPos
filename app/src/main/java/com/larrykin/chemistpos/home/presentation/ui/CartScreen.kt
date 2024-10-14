package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel

@Composable
fun CartScreen(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    val cartItems = stockViewModel.cart.collectAsState().value

    LazyColumn {
        items(cartItems) { product ->
            Text(text = "Name: ${product.name}, Quantity: ${product.quantityAvailable}")
        }
    }
}