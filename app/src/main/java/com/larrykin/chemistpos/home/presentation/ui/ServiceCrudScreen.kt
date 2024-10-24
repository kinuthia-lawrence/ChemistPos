package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.larrykin.chemistpos.home.data.Service
import com.larrykin.chemistpos.home.data.Supplier
import com.larrykin.chemistpos.home.presentation.viewModels.ServiceResult
import com.larrykin.chemistpos.home.presentation.viewModels.ServicesViewModel
import com.larrykin.chemistpos.home.presentation.viewModels.SupplierResult
import com.larrykin.chemistpos.home.presentation.viewModels.SupplierViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ServiceCrudScreen(
    servicesViewModel: ServicesViewModel,
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
                    Text(text = "Services")
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
                            Icon(imageVector = Icons.Default.Build, contentDescription = null)
                            Text(text = "Services", modifier = Modifier.padding(top = 8.dp))
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
                Services(
                    servicesViewModel = servicesViewModel
                )
            }
            item {
                CreateService(
                    servicesViewModel = servicesViewModel,
                    scope,
                    listState
                )
            }
            item {
                More(
                    servicesViewModel = servicesViewModel
                )
            }
        }
    }
}


@Composable
fun Services(
    servicesViewModel: ServicesViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var services by remember { mutableStateOf(listOf<Service>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        servicesViewModel.getServices { fetchedServices ->
            services = fetchedServices
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            labelText = "Search Services",
            leadingIcon = Icons.Default.Search,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        val filteredServices = services.filter { it.name.contains(searchQuery, ignoreCase = true) }

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
                if (filteredServices.isEmpty()) {
                    Text(text = "No Services found")
                } else {
                    filteredServices.forEach { service ->
                        ServiceCard(servicesViewModel = servicesViewModel, service = service)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    servicesViewModel: ServicesViewModel,
    service: Service
) {
    var isDeleteSupplier by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditServiceDialog by remember { mutableStateOf(false) }

    //success dialog
    if (showSuccessDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "Service deleted successfully",
            onDismiss = { showSuccessDialog = false },
            alertState = "success"
        )
    }
    //error dialog
    if (showErrorDialog) {
        CustomAlertDialog(
            title = "Error",
            message = "Error deleting Service",
            onDismiss = { showErrorDialog = false },
            alertState = "error"
        )
    }
    //delete dialog
    if (isDeleteSupplier) {
        CustomAlertDialogWithChoice(
            title = "Delete Service",
            message = "Are you sure you want to delete this Service?, this action cannot be " +
                    "undone!!",
            onDismiss = { isDeleteSupplier = false },
            onConfirm = {
                servicesViewModel.deleteService(service.id) { result ->
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
            title = "Edit Service",
            message = "Are you sure you want to edit this Service?",
            onDismiss = { showEditDialog = false },
            onConfirm = {
                showEditDialog = false
                showEditServiceDialog = true
            },
            alertState = "confirm"
        )
    }
    if (showEditServiceDialog) {
        EditService(
            servicesViewModel = servicesViewModel,
            service = service,
            onDismiss = { showEditServiceDialog = false }
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
            Text(text = "Name: ${service.name}", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Description: ${service.description}", style = MaterialTheme.typography
                    .bodyLarge
            )
            Text(text = "Price: ${service.price}", style = MaterialTheme.typography.bodyLarge)

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
fun EditService(
    servicesViewModel: ServicesViewModel,
    service: Service,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(service.name) }
    var description by remember { mutableStateOf(service.description) }
    var price by remember { mutableStateOf(service.price.toString()) }
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
            message = "Service updated successfully",
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
            text = "Edit Service",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            labelText = "Service Name",
            leadingIcon = Icons.Default.Edit,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            labelText = "Description",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = price,
            onValueChange = { price = it.trim() },
            labelText = "Price",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Number,
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
                if (name.isBlank() || description.isBlank() || price.isBlank()) {
                    showErrorAlert = true
                    return@Button
                }

                val updatedService = service.copy(
                    name = name.trim(),
                    description = description.trim(),
                    price = price.trim().toDouble()
                )
                servicesViewModel.viewModelScope.launch {
                    servicesViewModel.updateService(updatedService, service.id, onResult = {
                        when (it) {
                            is ServiceResult.Success -> {
                                showSuccessAlert = true
                                onDismiss()
                            }
                            is ServiceResult.Error -> {
                                errorAlert = true
                            }

                            else -> {}
                        }
                    })
                }
            }) {
                Text("Update Service")
            }
        }
    }
}

@Composable
fun CreateService(
    servicesViewModel: ServicesViewModel,
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
                text = "Create Service",
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
                value = servicesViewModel.name,
                onValueChange = { servicesViewModel.name = it },
                labelText = "Service Name",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Create,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = servicesViewModel.description,
                onValueChange = { servicesViewModel.description = it },
                labelText = "Description",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = servicesViewModel.price,
                onValueChange = { servicesViewModel.price = it.trim() },
                labelText = "Price",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    //check if fields are empty
                    if (servicesViewModel.name.isEmpty() || servicesViewModel.description.isEmpty() ||
                        servicesViewModel.price.isEmpty()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    //invoke the create supplier method
                    servicesViewModel.createService { result ->
                        when (result) {
                            is ServiceResult.Success -> {
                                errorMessage = ""
                                setIsRed(false)
                                messageState.value = "Service created successfully"
                                //clear the fields
                                servicesViewModel.name = ""
                                servicesViewModel.description = ""
                                servicesViewModel.price = ""
                            }

                            is ServiceResult.Error -> {
                                errorMessage = result.message
                            }

                            is ServiceResult.ServiceExists -> {
                                errorMessage = "Service with this name already exists"
                            }

                            else -> {}
                        }
                    }
                    scope.launch { listState.scrollToItem(1) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Service")
            }
        }
    }
}

@Composable
fun More(
    servicesViewModel: ServicesViewModel,
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