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
import com.larrykin.chemistpos.authentication.presentation.ui.defaultPadding
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterResult
import com.larrykin.chemistpos.components.HeaderText
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

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Gray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp).padding(top = 48.dp)
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
                    IconButton(onClick = { scope.launch { listState.scrollToItem(0) } }) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Users")
                    }
                    IconButton(onClick = { scope.launch { listState.scrollToItem(1) } }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Create User")
                    }
                    IconButton(onClick = { scope.launch { listState.scrollToItem(1) } }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Update User")
                    }
                    IconButton(onClick = { scope.launch { listState.scrollToItem(2) } }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxSize()
        ) {
            item {
                Users(
                    settingsViewModel = settingsViewModel
                )
            }
            item {
                CreateUserOrUpdate(
                    settingsViewModel = settingsViewModel
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
        .background(color = Color.White)
        .height(200.dp)
        .padding(top = 100.dp)
) {
    val messageState = remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    HeaderText(
        text = "Users",
        modifier = Modifier
            .padding(vertical = defaultPadding)
//            .align(alignment = Alignment.CenterHorizontally)
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
    Text(text = "Users")
}

@Composable
fun CreateUserOrUpdate(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
        .background(color = Color.Blue)
        .padding(top = 16.dp)
) {
    val messageState = remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    HeaderText(
        text = "Create or Update User",
        modifier = Modifier
            .padding(vertical = defaultPadding)
//            .align(alignment = Alignment.CenterHorizontally)
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
        labelText = "Email",
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
            //invoke the register method
            settingsViewModel.createUser { result ->
                when (result) {
                    is settingsResult.Success -> {
                        errorMessage = ""
                        messageState.value = "User created successfully"
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
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Register")
    }
}

@Composable
fun More(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
        .background(color = Color.Green)
        .padding(top = 16.dp)
) {
    val messageState = remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    HeaderText(
        text = "More",
        modifier = Modifier
            .padding(vertical = defaultPadding)
//            .align(alignment = Alignment.CenterHorizontally)
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
    Text(text = "More")
}