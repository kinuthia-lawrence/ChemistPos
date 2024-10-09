package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import com.larrykin.chemistpos.home.data.Product
import java.util.*
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.ui.platform.LocalContext

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
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var formulation by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var minMeasure by remember { mutableStateOf("") }
    var quantityAvailable by remember { mutableStateOf("") }
    var buyingPrice by remember { mutableStateOf("") }
    var retailSellingPrice by remember { mutableStateOf("") }
    var wholesaleSellingPrice by remember { mutableStateOf("") }
    var supplierName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf<Date?>(null) }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = company,
            onValueChange = { company = it },
            label = { Text("Company") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = formulation,
            onValueChange = { formulation = it },
            label = { Text("Formulation") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = minStock,
            onValueChange = { minStock = it },
            label = { Text("Min Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = minMeasure,
            onValueChange = { minMeasure = it },
            label = { Text("Min Measure") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = quantityAvailable,
            onValueChange = { quantityAvailable = it },
            label = { Text("Quantity Available") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = buyingPrice,
            onValueChange = { buyingPrice = it },
            label = { Text("Buying Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = retailSellingPrice,
            onValueChange = { retailSellingPrice = it },
            label = { Text("Retail Selling Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = wholesaleSellingPrice,
            onValueChange = { wholesaleSellingPrice = it },
            label = { Text("Wholesale Selling Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = supplierName,
            onValueChange = { supplierName = it },
            label = { Text("Supplier Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // DatePicker for expiry date
        Button(onClick = {
            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                expiryDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time
            }, year, month, day).show()
        }) {
            Text(text = expiryDate?.toString() ?: "Select Expiry Date")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val product = Product(
                name = name,
                company = company,
                formulation = formulation,
                minStock = minStock.toIntOrNull() ?: 0,
                minMeasure = minMeasure.toIntOrNull() ?: 0,
                quantityAvailable = quantityAvailable.toIntOrNull() ?: 0,
                buyingPrice = buyingPrice.toDoubleOrNull() ?: 0.0,
                retailSellingPrice = retailSellingPrice.toDoubleOrNull() ?: 0.0,
                wholesaleSellingPrice = wholesaleSellingPrice.toDoubleOrNull() ?: 0.0,
                supplierName = supplierName,
                dateAdded = Date(),
                updatedAt = null,
                addedBy = loggedInUser.username,
                expiryDate = expiryDate ?: Date(),
                description = description.ifEmpty { null }
            )
//            stockViewModel.addProduct(product)
            Log.d("StockViewModel", "Product added: $product")
        }) {
            Text("Add Product")
        }
    }
}