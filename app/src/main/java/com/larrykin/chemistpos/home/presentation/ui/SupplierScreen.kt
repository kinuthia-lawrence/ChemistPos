package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialogWithChoice
import com.larrykin.chemistpos.home.data.Supplier
import com.larrykin.chemistpos.home.presentation.viewModels.SupplierResult
import com.larrykin.chemistpos.home.presentation.viewModels.SupplierViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SupplierScreen(
    supplierViewModel: SupplierViewModel,
    navController: NavController
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(top = 48.dp)
                ) {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Suppliers")
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { scope.launch { listState.scrollToItem(0) } },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                            Text(text = "Suppliers", modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    IconButton(
                        onClick = {
                            scope.launch { listState.scrollToItem(1) }
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                            Text(text = "Create", modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    IconButton(
                        onClick = { scope.launch { listState.scrollToItem(2) } },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                            Text(text = "More", modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Suppliers(
                    supplierViewModel = supplierViewModel
                )
            }
            item {
                CreateSupplier(
                    supplierViewModel = supplierViewModel,
                    scope,
                    listState
                )
            }
            item {
                More(
                    supplierViewModel = supplierViewModel
                )
            }
        }
    }
}

@Composable
fun Suppliers(
    supplierViewModel: SupplierViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var suppliers by remember { mutableStateOf(listOf<Supplier>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        supplierViewModel.getSuppliers { fetchedSuppliers ->
            suppliers = fetchedSuppliers
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            labelText = "Search Suppliers",
            leadingIcon = Icons.Default.Search,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        val filteredSuppliers = suppliers.filter { it.name.contains(searchQuery, ignoreCase = true) }

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
                if (filteredSuppliers.isEmpty()) {
                    Text(text = "No suppliers found")
                } else {
                    filteredSuppliers.forEach { supplier ->
                        SupplierCard(supplierViewModel = supplierViewModel, supplier = supplier)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SupplierCard(
    supplierViewModel: SupplierViewModel,
    supplier: Supplier
) {
    var isDeleteSupplier by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditSupplierDialog by remember { mutableStateOf(false) }

    //success dialog
    if (showSuccessDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "Supplier deleted successfully",
            onDismiss = { showSuccessDialog = false },
            alertState = "success"
        )
    }
    //error dialog
    if (showErrorDialog) {
        CustomAlertDialog(
            title = "Error",
            message = "Error deleting supplier",
            onDismiss = { showErrorDialog = false },
            alertState = "error"
        )
    }
    //delete dialog
    if (isDeleteSupplier) {
        CustomAlertDialogWithChoice(
            title = "Delete Supplier",
            message = "Are you sure you want to delete this supplier?, this action cannot be undone!!",
            onDismiss = { isDeleteSupplier = false },
            onConfirm = {
                supplierViewModel.deleteSupplier(supplier.id) { result ->
                    if (result) {
                        showSuccessDialog = true
                    } else {
                        showErrorDialog = true
                    }
                }
            },
            alertState = "confirm"
        )
    }
    //edit dialog
    if (showEditDialog) {
        CustomAlertDialogWithChoice(
            title = "Edit Supplier",
            message = "Are you sure you want to edit this supplier?",
            onDismiss = { showEditDialog = false },
            onConfirm = {
                showEditDialog = false
                showEditSupplierDialog = true
            },
            alertState = "confirm"
        )
    }
    if (showEditSupplierDialog) {
        EditSupplierDialog(
            supplierViewModel = supplierViewModel,
            supplier = supplier,
            onDismiss = { showEditSupplierDialog = false }
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Name: ${supplier.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Phone: ${supplier.phone}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Email: ${supplier.email}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { isDeleteSupplier = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun EditSupplierDialog(
    supplierViewModel: SupplierViewModel,
    supplier: Supplier,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(supplier.name) }
    var phone by remember { mutableStateOf(supplier.phone) }
    var email by remember { mutableStateOf(supplier.email) }
    var showErrorAlert by remember { mutableStateOf(false) }
    var showSuccessAlert by remember { mutableStateOf(false) }
    var errorAlert by remember { mutableStateOf(false) }

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
            message = "Supplier updated successfully",
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
            text = "Edit Supplier",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            labelText = "Supplier Name",
            leadingIcon = Icons.Default.Edit,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = phone,
            onValueChange = { phone = it },
            labelText = "Phone",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            labelText = "Email",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Email,
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
                //check if fields are empty
                if (name.isBlank() || phone.isBlank() || email.isBlank()) {
                    showErrorAlert = true
                    return@Button
                }

                val updatedSupplier = supplier.copy(
                    name = name.trim(),
                    phone = phone.trim(),
                    email = email.trim()
                )
                supplierViewModel.viewModelScope.launch {
                    supplierViewModel.updateSupplier(updatedSupplier, supplier.id, onResult = {
                        when (it) {
                            is SupplierResult.Success -> {
                                showSuccessAlert = true
                                onDismiss()
                            }

                            is SupplierResult.Error -> {
                                errorAlert = true
                            }

                            else -> {}
                        }
                    })
                }
            }) {
                Text("Update Supplier")
            }
        }
    }
}

@Composable
fun CreateSupplier(
    supplierViewModel: SupplierViewModel,
    scope: CoroutineScope,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val messageState = remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            HeaderText(
                text = "Create Supplier",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (messageState.value.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRed) Color.Red else Color(0xFF014605))
                        .padding(8.dp)
                ) {
                    Text(
                        text = messageState.value,
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red)
                        .padding(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = supplierViewModel.name,
                onValueChange = { supplierViewModel.name = it.trim() },
                labelText = "Supplier Name",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Create,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = supplierViewModel.phone,
                onValueChange = { supplierViewModel.phone = it.trim() },
                labelText = "Phone",
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = supplierViewModel.email,
                onValueChange = { supplierViewModel.email = it.trim() },
                labelText = "Email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    //check if fields are empty
                    if (supplierViewModel.name.isEmpty() || supplierViewModel.phone.isEmpty() || supplierViewModel.email.isEmpty()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    //invoke the create supplier method
                    supplierViewModel.createSupplier { result ->
                        when (result) {
                            is SupplierResult.Success -> {
                                errorMessage = ""
                                setIsRed(false)
                                messageState.value = "Supplier created successfully"
                                //clear the fields
                                supplierViewModel.name = ""
                                supplierViewModel.phone = ""
                                supplierViewModel.email = ""
                            }

                            is SupplierResult.Error -> {
                                errorMessage = result.message
                            }

                            is SupplierResult.SupplierExists -> {
                                errorMessage = "Supplier with this name already exists"
                            }

                            else -> {}
                        }
                    }
                    scope.launch { listState.scrollToItem(1) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Supplier")
            }
        }
    }
}

@Composable
fun More(
    supplierViewModel: SupplierViewModel,
    modifier: Modifier = Modifier
) {
    val messageState = remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            HeaderText(
                text = "More",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (messageState.value.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isRed) Color.Red else Color(0xFF014605))
                        .padding(8.dp)
                ) {
                    Text(
                        text = messageState.value,
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No more features available for now :)",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}