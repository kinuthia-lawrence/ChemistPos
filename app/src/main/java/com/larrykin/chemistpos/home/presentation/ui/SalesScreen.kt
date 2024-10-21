package com.larrykin.chemistpos.home.presentation.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.data.Sales
import com.larrykin.chemistpos.home.presentation.viewModels.StockResult
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.text.set

@Composable
fun SalesScreen(stockViewModel: StockViewModel = hiltViewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Stock", "Sales")

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

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> StockTabContent(stockViewModel)
                1 -> SalesTabContent(stockViewModel)
            }
        }
    }
}

@Composable
fun StockTabContent(stockViewModel: StockViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        stockViewModel.getAllProducts { result, productList ->
            when (result) {
                is StockResult.Success -> {
                    products = productList
                }

                is StockResult.Error -> {
                    errorMessage = result.message
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            labelText = "Search Medicines",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        val filteredProducts = products.filter { it.name.contains(searchQuery, ignoreCase = true) }

        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red)
                    .padding(8.dp)
            ) {
                Text(
                    text = errorMessage ?: "An error occurred",
                    color = Color.White,
                )
            }
        } else {
            Column {
                filteredProducts.forEach { product ->
                    MedicineCard(product, stockViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun MedicineCard(product: Product, stockViewModel: StockViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val cartItems = stockViewModel.cart.collectAsState().value

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Alert") },
            text = { Text("Quantity available is less than the minimum measure") }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        val availableQuantity = product.quantityAvailable / product.minMeasure

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${product.name}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Expiry Date: ${product.expiryDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Quantity Available: ${product.quantityAvailable}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Retail Price: @Ksh${product.retailSellingPrice}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Wholesale Price: @Ksh${product.wholesaleSellingPrice}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Min Measure: ${product.minMeasure}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sellable Quantity: $availableQuantity",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        if (cartItems.none { it.id == product.id }) {
                            if (product.quantityAvailable > product.minMeasure) {
                                stockViewModel.addToCart(product)
                            } else {
                                showDialog = true
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Add to Cart",
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun SalesTabContent(stockViewModel: StockViewModel = hiltViewModel()) {
    var fromDate by remember { mutableStateOf(Date()) }
    var toDate by remember { mutableStateOf(Date()) }
    val sales: List<Sales> by stockViewModel.sales.collectAsState(initial = emptyList())

    Column(modifier = Modifier.padding(16.dp)) {
        // Date Pickers
        Row(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DatePicker(label = "From Date", selectedDate = fromDate) { date ->
                fromDate = date
            }
            DatePicker(label = "To Date", selectedDate = toDate) { date ->
                toDate = date
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Normalize dates
        val normalizedFromDate = Calendar.getInstance().apply {
            time = fromDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val normalizedToDate = Calendar.getInstance().apply {
            time = toDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        // Filter and sort sales
        val filteredSales =
            sales.filter { sale -> sale.date in normalizedFromDate..normalizedToDate }

        // Display sales
        filteredSales.forEach { sale ->
            SaleItemCard(sale)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun DatePicker(label: String, selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateText = remember { mutableStateOf(dateFormat.format(selectedDate)) }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = {
            // Show date picker dialog
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val newDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                    dateText.value = dateFormat.format(newDate)
                    onDateSelected(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text(text = dateText.value)
        }
    }
}

@Composable
fun SaleItemCard(sale: Sales, stockViewModel: StockViewModel = hiltViewModel()) {
        val products = remember { mutableStateMapOf<Int, String>() }

        LaunchedEffect(sale.items) {
            sale.items.forEach { saleItem ->
                val product = stockViewModel.getProductById(saleItem.productId)
                product?.let {
                    products[saleItem.productId] = it.name
                }
            }
        }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID: ${sale.id}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(sale.date)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Seller: ${sale.seller}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            sale.items.forEach { saleItem ->
                val productName = products[saleItem.productId] ?: "Unknown"
                Text(text = "Item: $productName, Quantity: ${saleItem.quantity}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Cash Amount: ${sale.cash}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Mpesa Amount: ${sale.mpesa}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Discount Amount: ${sale.discount}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Credit Amount: ${sale.credit}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Paid Amount: ${sale.totalPrice}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Expected Amount: ${sale.expectedAmount}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}