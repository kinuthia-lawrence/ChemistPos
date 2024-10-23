package com.larrykin.chemistpos.home.presentation.ui

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialogWithChoice
import com.larrykin.chemistpos.core.presentation.ui.CustomFilterField
import com.larrykin.chemistpos.home.data.Product
import com.larrykin.chemistpos.home.presentation.viewModels.StockResult
import com.larrykin.chemistpos.home.presentation.viewModels.StockViewModel
import kotlinx.coroutines.launch
import java.util.*

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
fun AvailableStockContent(
    loggedInUser: LoggedInUser,
    stockViewModel: StockViewModel = hiltViewModel()
) {
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
            labelText = "Search Products",
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
                    ProductCard(
                        product = product,
                        loggedInUser = loggedInUser,
                        stockViewModel = stockViewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, loggedInUser: LoggedInUser, stockViewModel: StockViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        var showDeleteChoiceDialog by remember { mutableStateOf(false) }
        var showDeleteSuccessDialog by remember { mutableStateOf(false) }
        var showDeleteErrorDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        var showEditProductDialog by remember { mutableStateOf(false) }

        // Delete choice dialog
        if (showDeleteChoiceDialog) {
            CustomAlertDialogWithChoice(
                title = "Delete Product",
                message = "Are you sure you want to delete this product?, this action cannot be undone",
                onDismiss = { showDeleteChoiceDialog = false },
                onConfirm = {
                    stockViewModel.viewModelScope.launch {
                        stockViewModel.deleteProduct(product.id) { isDeleted ->
                            if (isDeleted) {
                                Log.d("ProductCard", "Product delete dialog")
                                showDeleteSuccessDialog = true
                            } else {
                                Log.d("ProductCard", "Product delete dialog2")
                                showDeleteErrorDialog = true
                            }
                            Log.d("ProductCard", "Product delete dialog3")
                            showDeleteChoiceDialog = false
                        }
                    }
                },
                alertState = "confirm"
            )
        }

        // Delete success dialog
        if (showDeleteSuccessDialog) {
            Log.d("ProductCard", "Product deleted successfully")
            CustomAlertDialog(
                title = "Success",
                message = "Product deleted successfully",
                onDismiss = { showDeleteSuccessDialog = false },
                alertState = "success"
            )
        }

        // Delete error dialog
        if (showDeleteErrorDialog) {
            CustomAlertDialog(
                title = "Error",
                message = "An error occurred deleting the product",
                onDismiss = { showDeleteErrorDialog = false },
                alertState = "error"
            )
        }

        // Edit dialog
        if (showEditDialog) {
            CustomAlertDialogWithChoice(
                title = "Edit Product",
                message = "Are you sure you want to edit this product?",
                onDismiss = { showEditDialog = false },
                onConfirm = {
                    showEditDialog = false
                    showEditProductDialog = true
                },
                alertState = "confirm"
            )
        }

        if (showEditProductDialog) {
            EditProductDialog(
                product = product,
                loggedInUser = loggedInUser,
                stockViewModel = stockViewModel,
                onDismiss = { showEditProductDialog = false }
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${product.name}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Company: ${product.company}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Formulation: ${product.formulation}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Minimum Stock: ${product.minStock}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Min Sellable Measure: ${product.minMeasure}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "NO. of Sellable Units Available: ${product.quantityAvailable}",
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
                text = "Supplier Name: ${product.supplierName}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Expiry Date: ${product.expiryDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Description: ${product.description ?: "N/A"}",
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (loggedInUser.role == Role.ADMIN) {
                Text(
                    text = "Buying Price: @Ksh${product.buyingPrice}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if (product.updatedAt != null) {
                        "Updated at: ${product.updatedAt}"
                    } else {
                        "Added at: ${product.dateAdded}"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteChoiceDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

//Edit Stock Content
@Composable
fun EditProductDialog(
    product: Product,
    loggedInUser: LoggedInUser,
    stockViewModel: StockViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var company by remember { mutableStateOf(product.company) }
    var minStock by remember { mutableStateOf(product.minStock.toString()) }
    var minMeasure by remember { mutableStateOf(product.minMeasure.toString()) }
    var quantityAvailable by remember { mutableStateOf(product.quantityAvailable.toString()) }
    var buyingPrice by remember { mutableStateOf(product.buyingPrice.toString()) }
    var retailSellingPrice by remember { mutableStateOf(product.retailSellingPrice.toString()) }
    var wholesaleSellingPrice by remember { mutableStateOf(product.wholesaleSellingPrice.toString()) }
    var supplierName by remember { mutableStateOf(product.supplierName) }
    var expiryDate by remember { mutableStateOf(product.expiryDate) }
    var description by remember { mutableStateOf(product.description ?: "") }

    var showErrorAlert by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var errorAlert by remember { mutableStateOf(false) }

    val formulations = listOf(
        "Tablets", "Capsules", "Powders", "Granules", "Solutions", "Suspensions",
        "Emulsions", "Gels", "Lotions", "Patches", "Inhalers", "Suppositories",
        "Injectables", "Syrup", "Creams", "Ointments"
    )
    val isDropDownExpanded = remember { mutableStateOf(false) }
    var formulation by remember { mutableStateOf(product.formulation) }
    val itemPosition = remember { mutableStateOf(formulations.indexOf(product.formulation)) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    if (showErrorAlert) {
        CustomAlertDialog(
            title = "Error",
            message = "Please fill all fields",
            onDismiss = { showErrorAlert = false },
            alertState = "Error"
        )
    }
    if (showSuccessAlert) {
        CustomAlertDialog(
            title = "Success",
            message = "Product updated successfully",
            onDismiss = { showSuccessAlert = false },
            alertState = "success"
        )
    }
    if (errorAlert) {
        CustomAlertDialog(
            title = "Error",
            message = "An error occurred",
            onDismiss = { errorAlert = false },
            alertState = "Error"
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText(
            text = "Edit Product",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Medicine Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = company,
            onValueChange = { company = it },
            labelText = "Company Name",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color(0xFFD3D3D3))
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            isDropDownExpanded.value = true
                        }
                ) {
                    Text(text = "Formulation : " + formulations[itemPosition.value])
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = {
                        isDropDownExpanded.value = false
                    }) {
                    formulations.forEachIndexed { index, selectedFormulation ->
                        DropdownMenuItem(
                            text = { Text(text = selectedFormulation) },
                            onClick = {
                                isDropDownExpanded.value = false
                                itemPosition.value = index
                                formulation = formulations[index]
                            })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = minStock,
            onValueChange = { minStock = it },
            labelText = "Minimum Stock(Min. NO. of sellable Measure)",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = minMeasure,
            onValueChange = { minMeasure = it },
            labelText = "Minimum sellable Measure",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = quantityAvailable,
            onValueChange = { quantityAvailable = it },
            labelText = "Total number of Minimum Sellable Units",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = buyingPrice,
            onValueChange = { buyingPrice = it },
            labelText = "Buying Price per Minimum Sellable Unit",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = retailSellingPrice,
            onValueChange = { retailSellingPrice = it },
            labelText = "Retail Selling Price per Minimum Sellable Unit",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = wholesaleSellingPrice,
            onValueChange = { wholesaleSellingPrice = it },
            labelText = "Wholesale Selling Price per Minimum Sellable Unit",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
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
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            labelText = "Description(optional)",
            leadingIcon = Icons.Default.Edit,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                //check if all fields are filled
                if (name.isBlank() || company.isBlank() || formulation.isBlank() || minStock.isBlank() ||
                    minMeasure.isBlank() || quantityAvailable.isBlank() || buyingPrice.isBlank() ||
                    retailSellingPrice.isBlank() || wholesaleSellingPrice.isBlank() || supplierName.isBlank() ||
                    expiryDate == null
                ) {
                    showErrorAlert = true
                    return@Button
                }
                val updatedProduct = product.copy(
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
                    expiryDate = expiryDate ?: Date(),
                    description = if (description.isBlank()) null else description,
                    updatedAt = Date()
                )
                stockViewModel.viewModelScope.launch {
                    stockViewModel.updateProduct(updatedProduct, onResult = {
                        when (it) {
                            is StockResult.Success -> {
                                showSuccessAlert = true
                                onDismiss()
                            }

                            is StockResult.Error -> {
                                errorAlert = true
                            }
                        }
                    })
                }
            }) {
                Text("Update Product")
            }
        }
    }
}

// Add Stock Content
@Composable
fun AddStockContent(loggedInUser: LoggedInUser, stockViewModel: StockViewModel = hiltViewModel()) {
    //
    val medicineNames by stockViewModel.medicineNames.collectAsState()
    val companyNames by stockViewModel.companyNames.collectAsState()
    val supplierNames by stockViewModel.supplierNames.collectAsState()

    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var minMeasure by remember { mutableStateOf("") }
    var quantityAvailable by remember { mutableStateOf("") }
    var buyingPrice by remember { mutableStateOf("") }
    var retailSellingPrice by remember { mutableStateOf("") }
    var wholesaleSellingPrice by remember { mutableStateOf("") }
    var supplierName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf<Date?>(null) }
    var description by remember { mutableStateOf("") }

    var showErrorAlert by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var errorAlert by remember { mutableStateOf(false) }

    // Formulations
    val formulations = listOf(
        "Tablets", "Capsules", "Powders", "Granules", "Solutions", "Suspensions",
        "Emulsions", "Gels", "Lotions", "Patches", "Inhalers", "Suppositories",
        "Injectables", "Syrup", "Creams", "Ointments"
    )


    val isDropDownExpanded = remember { mutableStateOf(false) }
    var formulation by remember { mutableStateOf(formulations.firstOrNull() ?: "") }
    val itemPosition = remember { mutableStateOf(0) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)


    if (showErrorAlert) {
        CustomAlertDialog(
            title = "Error",
            message = "Please fill all fields",
            onDismiss = { showErrorAlert = false },
            alertState = "Error"
        )
    }
    if (showSuccessAlert) {
        CustomAlertDialog(
            title = "Success",
            message = "Product added successfully",
            onDismiss = { showSuccessAlert = false },
            alertState = "success"
        )
    }
    if (errorAlert) {
        CustomAlertDialog(
            title = "Error",
            message = "An error occurred",
            onDismiss = { errorAlert = false },
            alertState = "Error"
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText(
            text = "Add Stock",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        CustomFilterField(
            namesList = medicineNames,
            name = name,
            onNameChange = { name = it },
            labelText = "Medicine Name",
            leadingIcon = Icons.Default.Edit,
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomFilterField(
            namesList = companyNames,
            name = company,
            onNameChange = { company = it },
            labelText = "Company Name",
            leadingIcon = Icons.Default.Edit,
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color.DarkGray)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            isDropDownExpanded.value = true
                        }
                ) {
                    Text(
                        text = "Formulation : " + formulations[itemPosition.value],
                        color = Color.White
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = {
                        isDropDownExpanded.value = false
                    }) {
                    formulations.forEachIndexed { index, selectedFormulation ->
                        DropdownMenuItem(
                            text = { Text(text = selectedFormulation) },
                            onClick = {
                                isDropDownExpanded.value = false
                                itemPosition.value = index
                                formulation = formulations[index]
                            })
                    }
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = minStock,
            onValueChange = { minStock = it },
            labelText = "Minimum Stock(Min. NO. of sellable units)",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = minMeasure,
            onValueChange = { minMeasure = it },
            labelText = "Minimum sellable Measure",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = quantityAvailable,
            onValueChange = { quantityAvailable = it },
            labelText = "Total number of Minimum Units",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = buyingPrice,
            onValueChange = { buyingPrice = it },
            labelText = "Buying Price per Minimum Sellable Unit",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = retailSellingPrice,
            onValueChange = { retailSellingPrice = it },
            labelText = "Retail Selling Price per Minimum sellable Unit",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = wholesaleSellingPrice,
            onValueChange = { wholesaleSellingPrice = it },
            labelText = "Wholesale Selling Price per Minimum Sellable Unit",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true

        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomFilterField(
            namesList = supplierNames,
            name = supplierName,
            onNameChange = { supplierName = it },
            labelText = "Supplier Name",
            leadingIcon = Icons.Default.Edit,
            keyboardType = KeyboardType.Text
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
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            labelText = "Description(optional)",
            leadingIcon = Icons.Default.Edit,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            //check if all fields are filled
            if (name.isBlank() || company.isBlank() || formulation.isBlank() || minStock.isBlank() ||
                minMeasure.isBlank() || quantityAvailable.isBlank() || buyingPrice.isBlank() ||
                retailSellingPrice.isBlank() || wholesaleSellingPrice.isBlank() || supplierName.isBlank() ||
                expiryDate == null
            ) {
                showErrorAlert = true
                return@Button
            }
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
                description = if (description.isBlank()) null else description
            )
            stockViewModel.viewModelScope.launch {
                stockViewModel.addProduct(product, onResult = {
                    when (it) {
                        is StockResult.Success -> {
                            showSuccessAlert = true
                            // clear all fields
                            name = ""
                            company = ""
                            formulation = ""
                            minStock = ""
                            minMeasure = ""
                            quantityAvailable = ""
                            buyingPrice = ""
                            retailSellingPrice = ""
                            wholesaleSellingPrice = ""
                            supplierName = ""
                            expiryDate = null
                            description = ""
                        }

                        is StockResult.Error -> {
                            errorAlert = true
                        }
                    }
                })
            }
        }) {
            Text("Add Product")
        }
    }
}