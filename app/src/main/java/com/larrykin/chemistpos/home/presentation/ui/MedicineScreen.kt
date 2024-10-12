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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    var isCreateMedicine by remember { mutableStateOf(true) }

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
                            isCreateMedicine = true
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
                        onClick = {
                            scope.launch { listState.scrollToItem(1) }
                            isCreateMedicine = false
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                            Text(text = "Update", modifier = Modifier.padding(top = 8.dp))
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
                CreateOrUpdateMedicine(
                    medicineViewModel = medicineViewModel,
                    isCreateMedicine = isCreateMedicine,
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
    var medicines by remember { mutableStateOf(listOf<Medicine>()) }
    LaunchedEffect(Unit) {
        medicineViewModel.getMedicines { fetchedMedicines ->
            medicines = fetchedMedicines
        }
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            HeaderText(
                text = "Medicines",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (medicines.isEmpty()) {
                Text(text = "No medicines found")
            } else {
                medicines.forEach { medicine ->
                    MedicineCard(medicineViewModel = medicineViewModel, medicine = medicine)
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

    if (showSuccessDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "Medicine deleted successfully",
            onDismiss = { showSuccessDialog = false },
            alertState = "success"
        )
    }
    if (showErrorDialog) {
        CustomAlertDialog(
            title = "Error",
            message = "Error deleting medicine",
            onDismiss = { showErrorDialog = false },
            alertState = "error"
        )
    }

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
                IconButton(onClick = { /* Handle edit action */ }) {
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
fun CreateOrUpdateMedicine(
    medicineViewModel: MedicineViewModel,
    isCreateMedicine: Boolean,
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
                text = if (isCreateMedicine) "Create Medicine" else "Update Medicine",
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
                    if (isCreateMedicine) {
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
                    } else {
                        //invoke the update method
                        medicineViewModel.updateMedicine { result ->
                            when (result) {
                                is MedicineResult.Success -> {
                                    errorMessage = ""
                                    setIsRed(false)
                                    messageState.value = "Medicine updated successfully"
                                    //clear the fields
                                    medicineViewModel.name = ""
                                    medicineViewModel.company = ""
                                }

                                is MedicineResult.Error -> {
                                    errorMessage = result.message
                                }

                                is MedicineResult.MedicineNotFound -> {
                                    errorMessage = "Medicine with name not found"
                                }

                                else -> {}
                            }
                        }
                    }
                    scope.launch { listState.scrollToItem(1) }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isCreateMedicine) "Create Medicine" else "Update Medicine")
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