package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel

@Composable
fun CartScreen(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    val cartItems = stockViewModel.cart.collectAsState().value

    Column {
        cartItems.forEach { item ->
           Text(text = "Name: ${item.name}, Quantity: ${item.quantityAvailable}, Price: ${item.retailSellingPrice}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}