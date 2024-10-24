package com.larrykin.chemistpos.home.presentation.ui

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.larrykin.chemistpos.home.data.ServicesOffered
import com.larrykin.chemistpos.home.presentation.viewModels.ServiceResult
import com.larrykin.chemistpos.home.presentation.viewModels.ServicesViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun ServicesScreen(
    loggedInUser: LoggedInUser,
    servicesViewModel: ServicesViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Add Service", "Service History")

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
                0 -> AddService(loggedInUser, servicesViewModel)
                1 -> ServicesHistory(loggedInUser, servicesViewModel)
            }
        }
    }
}

@Composable
fun ServicesHistory(loggedInUser: LoggedInUser, servicesViewModel: ServicesViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var services by remember { mutableStateOf<List<ServicesOffered>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        servicesViewModel.getServicesOffered { result, serviceList ->
            when (result) {
                is ServiceResult.Success -> {
                    services = serviceList
                }

                is ServiceResult.Error -> {
                    errorMessage = result.message
                }

                ServiceResult.ServiceExists ->  {
                    errorMessage = "Service already exists"
                }
                ServiceResult.ServiceNotFound ->  {
                    errorMessage = "Service not found"
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        CustomTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            labelText = "Search Services",
            leadingIcon = Icons.Default.Search,
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
                filteredServices.forEach { service ->
                    ServiceCard(
                        servicesOffered = service,
                        loggedInUser = loggedInUser,
                        servicesViewModel = servicesViewModel
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AddService(loggedInUser: LoggedInUser, servicesViewModel: ServicesViewModel) {
    Text(text = "add service")
}

@Composable
fun ServiceCard(
    servicesOffered: ServicesOffered,
    loggedInUser: LoggedInUser,
    servicesViewModel: ServicesViewModel
) {
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
        var showEditServiceDialog by remember { mutableStateOf(false) }

        // Delete choice dialog
        if (showDeleteChoiceDialog) {
            CustomAlertDialogWithChoice(
                title = "Delete Service",
                message = "Are you sure you want to delete this Service?, this action cannot be " +
                        "undone",
                onDismiss = { showDeleteChoiceDialog = false },
                onConfirm = {
                    servicesViewModel.viewModelScope.launch {
                        servicesViewModel.deleteServiceOffered(servicesOffered.id) { isDeleted ->
                            if (isDeleted) {
                                Log.d("ServiceCard", "Service delete dialog")
                                showDeleteSuccessDialog = true
                            } else {
                                Log.d("ServiceCard", "Service delete dialog2")
                                showDeleteErrorDialog = true
                            }
                            Log.d("ServiceCard", "Service delete dialog3")
                            showDeleteChoiceDialog = false
                        }
                    }
                },
                alertState = "confirm"
            )
        }

        // Delete success dialog
        if (showDeleteSuccessDialog) {
            Log.d("ServiceCard", "Service deleted successfully")
            CustomAlertDialog(
                title = "Success",
                message = "Service deleted successfully",
                onDismiss = { showDeleteSuccessDialog = false },
                alertState = "success"
            )
        }

        // Delete error dialog
        if (showDeleteErrorDialog) {
            CustomAlertDialog(
                title = "Error",
                message = "An error occurred deleting the Service",
                onDismiss = { showDeleteErrorDialog = false },
                alertState = "error"
            )
        }

        // Edit dialog
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
            EditServiceDialog(
                servicesOffered = servicesOffered,
                loggedInUser = loggedInUser,
                servicesViewModel = servicesViewModel,
                onDismiss = { showEditServiceDialog = false }
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Service Name: ${servicesOffered.name}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Servitor: ${servicesOffered.servitor}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Description: ${servicesOffered.description}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Cash: ${servicesOffered.cash}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Mpesa: ${servicesOffered.mpesa}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (loggedInUser.role == Role.ADMIN) {
                Text(
                    text = if (servicesOffered.updatedAt != null) {
                        "Updated at: ${servicesOffered.updatedAt}"
                    } else {
                        "Created at: ${servicesOffered.createdAt}"
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
fun EditServiceDialog(
    servicesOffered: ServicesOffered,
    loggedInUser: LoggedInUser,
    servicesViewModel: ServicesViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(servicesOffered.name) }
    var servitor by remember { mutableStateOf(servicesOffered.servitor) }
    var description by remember { mutableStateOf(servicesOffered.description) }
    var cash by remember { mutableStateOf(servicesOffered.cash.toString()) }
    var mpesa by remember { mutableStateOf(servicesOffered.mpesa.toString()) }


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
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            labelText = "Service Name",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = servitor,
            onValueChange = { servitor = it },
            labelText = "Service Name",
            leadingIcon = Icons.Default.CheckCircle,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            labelText = "Description",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = cash,
            onValueChange = { cash = it },
            labelText = "Cash",
            leadingIcon = Icons.Default.Create,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = mpesa,
            onValueChange = { mpesa = it },
            labelText = "Mpesa",
            leadingIcon = Icons.Default.Create,
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
                //check if all fields are filled
                if (name.isBlank() || description.isBlank() || servitor.isBlank()
                ) {
                    showErrorAlert = true
                    return@Button
                }
                val updatedServiceOffered = servicesOffered.copy(
                    name = name,
                    servitor = servitor,
                    description = description,
                    cash = cash.toDoubleOrNull() ?: 0.0,
                    mpesa = mpesa.toDoubleOrNull() ?: 0.0,
                    totalPrice = (cash.toDoubleOrNull() ?: 0.0) + (mpesa.toDoubleOrNull() ?: 0.0),
                    updatedAt = Date()
                )
                servicesViewModel.viewModelScope.launch {
                    servicesViewModel.updateServiceOffered(updatedServiceOffered,
                        servicesOffered.id,
                        onResult = {
                            when (it) {
                                is ServiceResult.Success -> {
                                    showSuccessAlert = true
                                    onDismiss()
                                }

                                else -> {
                                    errorAlert = true
                                }
                            }
                        })
                }
            }) {
                Text("Update Service")
            }
        }
    }
}