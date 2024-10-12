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
import com.larrykin.chemistpos.home.data.Medicine
import com.larrykin.chemistpos.home.presentation.viewModels.MedicineResult
import com.larrykin.chemistpos.home.presentation.viewModels.MedicineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MedicineScreen(
    medicineViewModel: MedicineViewModel,
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
                    Text(text = "Medicines")
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
                            Text(text = "Medicines", modifier = Modifier.padding(top = 8.dp))
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
                Medicines(
                    medicineViewModel = medicineViewModel
                )
            }
            item {
                CreateMedicine(
                    medicineViewModel = medicineViewModel,
                    scope,
                    listState
                )
            }
            item {
                More(
                    medicineViewModel = medicineViewModel
                )
            }
        }
    }
}

@Composable
fun Medicines(
    medicineViewModel: MedicineViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var medicines by remember { mutableStateOf(listOf<Medicine>()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        medicineViewModel.getMedicines { fetchedMedicines ->
            medicines = fetchedMedicines
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

        val filteredMedicines = medicines.filter { it.name.contains(searchQuery, ignoreCase = true) }

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
                if (filteredMedicines.isEmpty()) {
                    Text(text = "No medicines found")
                } else {
                    filteredMedicines.forEach { medicine ->
                        MedicineCard(medicineViewModel = medicineViewModel, medicine = medicine)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineCard(
    medicineViewModel: MedicineViewModel,
    medicine: Medicine
) {
    var isDeleteMedicine by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showEditMedicineDialog by remember { mutableStateOf(false) }

    //success dialog
    if (showSuccessDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "Medicine deleted successfully",
            onDismiss = { showSuccessDialog = false },
            alertState = "success"
        )
    }
    //error dialog
    if (showErrorDialog) {
        CustomAlertDialog(
            title = "Error",
            message = "Error deleting medicine",
            onDismiss = { showErrorDialog = false },
            alertState = "error"
        )
    }
    //delete dialog
    if (isDeleteMedicine) {
        CustomAlertDialogWithChoice(
            title = "Delete Medicine",
            message = "Are you sure you want to delete this medicine?, this action cannot be undone!!",
            onDismiss = { isDeleteMedicine = false },
            onConfirm = {
                medicineViewModel.deleteMedicine(medicine.id) { result ->
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
            title = "Edit Medicine",
            message = "Are you sure you want to edit this medicine?",
            onDismiss = { showEditDialog = false },
            onConfirm = {
                showEditDialog = false
                showEditMedicineDialog = true
            },
            alertState = "confirm"
        )
    }
    if (showEditMedicineDialog) {
        EditMedicineDialog(
            medicineViewModel = medicineViewModel,
            medicine = medicine,
            onDismiss = { showEditMedicineDialog = false }
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
            Text(text = "Name: ${medicine.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Company: ${medicine.company}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { isDeleteMedicine = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun EditMedicineDialog(
    medicineViewModel: MedicineViewModel,
    medicine: Medicine,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(medicine.name) }
    var company by remember { mutableStateOf(medicine.company) }
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
            message = "Medicine updated successfully",
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
            text = "Edit Medicine",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            labelText = "Medicine Name",
            leadingIcon = Icons.Default.Edit,
            keyboardType = KeyboardType.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
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
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                //check if fields are empty
                if (name.isBlank() || company.isBlank()) {
                    showErrorAlert = true
                    return@Button
                }

                val updatedMedicine = medicine.copy(
                    name = name.trim(),
                    company = company.trim()
                )
                medicineViewModel.viewModelScope.launch {
                    medicineViewModel.updateMedicine(updatedMedicine, medicine.id, onResult = {
                        when (it) {
                            is MedicineResult.Success -> {
                                showSuccessAlert = true
                                onDismiss()
                            }

                            is MedicineResult.Error -> {
                                errorAlert = true
                            }

                            else -> {}
                        }
                    })
                }
            }) {
                Text("Update Medicine")
            }
        }
    }
}

@Composable
fun CreateMedicine(
    medicineViewModel: MedicineViewModel,
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
                text = "Create Medicine",
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
                value = medicineViewModel.name,
                onValueChange = { medicineViewModel.name = it.trim() },
                labelText = "Medicine Name",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Create,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = medicineViewModel.company,
                onValueChange = { medicineViewModel.company = it.trim() },
                labelText = "Company",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    //check if fields are empty
                    if (medicineViewModel.name.isEmpty() || medicineViewModel.company.isEmpty()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    //invoke the create medicine method
                    medicineViewModel.createMedicine { result ->
                        when (result) {
                            is MedicineResult.Success -> {
                                errorMessage = ""
                                setIsRed(false)
                                messageState.value = "Medicine created successfully"
                                //clear the fields
                                medicineViewModel.name = ""
                                medicineViewModel.company = ""
                            }

                            is MedicineResult.Error -> {
                                errorMessage = result.message
                            }

                            is MedicineResult.MedicineExists -> {
                                errorMessage = "Medicine with this name already exists"
                            }

                            else -> {}
                        }
                    }
                    scope.launch { listState.scrollToItem(1) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Create Medicine")
            }
        }
    }
}

@Composable
fun More(
    medicineViewModel: MedicineViewModel,
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