package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.User
import com.larrykin.chemistpos.authentication.presentation.ui.defaultPadding
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterResult
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.core.data.AuthResult
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialogWithChoice
import com.larrykin.chemistpos.home.presentation.viewModels.SettingsViewModel
import com.larrykin.chemistpos.home.presentation.viewModels.settingsResult
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isCreateUser by remember { mutableStateOf(true) }

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
                    Text(text = "Settings")
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
                            Text(text = "Users", modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                    IconButton(
                        onClick = {
                            scope.launch { listState.scrollToItem(1) }
                            isCreateUser = true
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
                            isCreateUser = false
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
                Users(
                    settingsViewModel = settingsViewModel
                )
            }
            item {
                CreateUserOrUpdate(
                    settingsViewModel = settingsViewModel,
                    isCreateUser = isCreateUser
                )
            }
            item {
                More(
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}


@Composable
fun Users(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var users by remember { mutableStateOf(listOf<User>()) }
    LaunchedEffect(Unit) {
        settingsViewModel.getUsers { fetchedUsers ->
            users = fetchedUsers
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
                text = "Users",
                modifier = Modifier
                    .padding(vertical = defaultPadding)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (users.isEmpty()) {
                Text(text = "No users found")
            } else {
                users.forEach { user ->
                    UserCard(settingsViewModel = settingsViewModel, user = user)
                }
            }


        }
    }
}

//user card
@Composable
fun UserCard(
    settingsViewModel: SettingsViewModel,
    user: User
) {
    var isDeleteUser by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "User deleted successfully",
            onDismiss = { showSuccessDialog = false },
            alertState = "success"
        )
    }
    if (showErrorDialog) {
        CustomAlertDialog(
            title = "Error",
            message = "Error deleting user",
            onDismiss = { showErrorDialog = false },
            alertState = "error"
        )
    }

    if (isDeleteUser) {
        CustomAlertDialogWithChoice(
            title = "Delete User",
            message = "Are you sure you want to delete this user?, this action cannot be undone!!",
            onDismiss = { isDeleteUser = false },
            onConfirm = {
                settingsViewModel.deleteUser(user) { result ->
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
            Text(text = "Name: ${user.username}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Phone: ${user.phoneNumber}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Role: ${user.role}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Password: ${user.password}", style = MaterialTheme.typography.bodyLarge)

            if (user.role != Role.ADMIN) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { isDeleteUser = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                ) {
                    Text(text = "Delete User", color = Color.White)
                }
            }
        }
    }
}


@Composable
fun CreateUserOrUpdate(
    settingsViewModel: SettingsViewModel,
    isCreateUser: Boolean,
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
        )
        {

            HeaderText(
                text = if (isCreateUser) "Create User" else "Update User",
                modifier = Modifier
                    .padding(vertical = defaultPadding)
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
                value = settingsViewModel.email,
                onValueChange = { settingsViewModel.email = it.trim() },
                labelText = if (isCreateUser) "Email" else "Email of Account to update",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Email,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = settingsViewModel.username,
                onValueChange = { settingsViewModel.username = it.trim() },
                labelText = "Username",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Person,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = settingsViewModel.password,
                onValueChange = { settingsViewModel.password = it },
                labelText = "Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = settingsViewModel.confirmPassword,
                onValueChange = { settingsViewModel.confirmPassword = it },
                labelText = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = settingsViewModel.phoneNumber,
                onValueChange = { settingsViewModel.phoneNumber = it.trim() },
                labelText = "Phone Number",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = settingsViewModel.chemistName,
                onValueChange = { settingsViewModel.chemistName = it.trim() },
                labelText = "Chemist Name",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Create,
                enabled = true
            )


            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    //check if fields are empty
                    if (settingsViewModel.email.isEmpty() || settingsViewModel.username.isEmpty() || settingsViewModel.password.isEmpty() || settingsViewModel.confirmPassword.isEmpty() || settingsViewModel.phoneNumber.isEmpty() || settingsViewModel.chemistName.isEmpty()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    //check if password are similar
                    if (settingsViewModel.password != settingsViewModel.confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    if (isCreateUser) {
                        //invoke the create user method
                        settingsViewModel.createUser { result ->
                            when (result) {
                                is settingsResult.Success -> {
                                    errorMessage = ""
                                    setIsRed(false)
                                    messageState.value = "User created successfully"
                                    //clear the fields
                                    settingsViewModel.email = ""
                                    settingsViewModel.username = ""
                                    settingsViewModel.password = ""
                                    settingsViewModel.confirmPassword = ""
                                    settingsViewModel.phoneNumber = ""
                                    settingsViewModel.chemistName = ""
                                }

                                is settingsResult.Error -> {
                                    errorMessage = result.message
                                }

                                is settingsResult.UserExists -> {
                                    errorMessage = "User with this email already exists"
                                }

                                else -> {}
                            }
                        }
                    } else {
                        //invoke the update method
                        settingsViewModel.updateUser { result ->
                            when (result) {
                                is settingsResult.Success -> {
                                    errorMessage = ""
                                    setIsRed(false)
                                    messageState.value = "User updated successfully"
                                    //clear the fields
                                    settingsViewModel.email = ""
                                    settingsViewModel.username = ""
                                    settingsViewModel.password = ""
                                    settingsViewModel.confirmPassword = ""
                                    settingsViewModel.phoneNumber = ""
                                    settingsViewModel.chemistName = ""
                                }

                                is settingsResult.Error -> {
                                    errorMessage = result.message
                                }

                                is settingsResult.UserNotFound -> {
                                    errorMessage = "User with email not found"
                                }

                                else -> {}
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isCreateUser) "Create User" else "Update User")

            }
        }
    }
}

@Composable
fun More(
    settingsViewModel: SettingsViewModel,
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
        )
        {

            HeaderText(
                text = "More",
                modifier = Modifier
                    .padding(vertical = defaultPadding)
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
                text = "No more settings available for now :)",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}