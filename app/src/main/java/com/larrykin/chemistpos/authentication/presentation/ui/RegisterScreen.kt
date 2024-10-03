package com.larrykin.chemistpos.authentication.presentation.ui

import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterResult
import com.larrykin.chemistpos.authentication.presentation.viewModels.RegisterViewModel
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.core.naviagation.Screen


@Composable
fun RegisterScreen(viewModel: RegisterViewModel = hiltViewModel(), navController: NavController) {
    var errorMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var isCodeSent by remember { mutableStateOf(false) }
    var isCodeVerified by remember { mutableStateOf(false) }
    val adminEmailState = remember { mutableStateOf("") }
    val codeState = remember { mutableStateOf("") }
    val messageState = remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }

    //show dialog if user is created successfully
    if (showDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "User with email ${viewModel.email} created successfully",
            onDismiss = {
                showDialog = false
            },
            alertState = "success"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText(
            text = "Login",
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
            value = adminEmailState.value,
            onValueChange = { adminEmailState.value = it.trim() },
            labelText = "Admin Email",
            leadingIcon = Icons.Default.Email,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCodeVerified && !isCodeSent
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.generateAndSendCode(adminEmailState.value) { result ->
                    messageState.value = result
                    setIsRed(result.startsWith("Error"))
                    if (result.startsWith("Code sent")) {
                        isCodeSent = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCodeVerified && !isCodeSent
        ) {
            Text("Send Code")
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = codeState.value,
            onValueChange = { codeState.value = it.trim() },
            labelText = "Enter Code",
            leadingIcon = Icons.Default.Edit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCodeVerified && isCodeSent
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.verifyCode(codeState.value) { isValid ->
                    isCodeVerified = isValid
                    messageState.value = if (isValid) "" else "Invalid code"
                    setIsRed(!isValid)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCodeVerified && isCodeSent
        ) {
            Text("Verify Code")
        }

        if (isCodeVerified) {
            CustomTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it.trim() },
                labelText = "Email",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Email,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it.trim() },
                labelText = "Username",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Person,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                labelText = "Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                labelText = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = viewModel.phoneNumber,
                onValueChange = { viewModel.phoneNumber = it.trim() },
                labelText = "Phone Number",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomTextField(
                value = viewModel.chemistName,
                onValueChange = { viewModel.chemistName = it.trim() },
                labelText = "Chemist Name",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Create,
                enabled = true
            )


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    //check if fields are empty
                    if (viewModel.email.isEmpty() || viewModel.username.isEmpty() || viewModel.password.isEmpty() || viewModel.confirmPassword.isEmpty() || viewModel.phoneNumber.isEmpty() || viewModel.chemistName.isEmpty()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    //check if password are similar
                    if (viewModel.password != viewModel.confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    //invoke the register method
                    viewModel.register { result ->
                        when (result) {
                            is RegisterResult.Success -> {
                                showDialog = true
                                navController.popBackStack()
                            }

                            is RegisterResult.Error -> {
                                errorMessage = result.message
                            }

                            is RegisterResult.UserExists -> {
                                errorMessage = "User with this email already exists"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
        Spacer(Modifier.height(itemSpacing))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an Account?")
            Spacer(Modifier.height(itemSpacing))
            TextButton(
                onClick = {
                    navController.navigate(Screen.Login.route)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4003F1))
            ) {
                Text("Sign In")

            }
        }
    }
}


