package com.larrykin.chemistpos.authentication.presentation.ui

import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import com.larrykin.chemistpos.authentication.presentation.viewModels.ForgotPasswordViewModel
import com.larrykin.chemistpos.components.HeaderText
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    navController: NavController
) {
    val adminEmailState = remember { mutableStateOf("") }
    val codeState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val newPasswordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val messageState = remember { mutableStateOf("") }
    var isCodeVerified by remember { mutableStateOf(false) }
    var resetState by remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isCodeSent by remember { mutableStateOf(false) }

    if (showDialog) {
        CustomAlertDialog(
            title = "Success",
            message = "Password reset successfully",
            onDismiss = { showDialog = false },
            alertState = "success"
        )
        LaunchedEffect(Unit) {
            delay(5000)
            showDialog = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText(text = "Reset Password")
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
        if (resetState.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isRed) Color.Red else Color(0xFF2E7D32))
                    .padding(8.dp)
            ) {
                Text(
                    text = resetState,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = adminEmailState.value,
            onValueChange = { adminEmailState.value = it.trim().lowercase() },
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
            enabled = !isCodeVerified
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.verifyCode(codeState.value) { isValid ->
                    isCodeVerified = isValid
                    messageState.value = if (isValid) "Code verified" else "Invalid code"
                    setIsRed(!isValid)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCodeVerified
        ) {
            Text("Verify Code")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isCodeVerified) {
            CustomTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it.trim().lowercase() },
                labelText = "Email to Reset",
                leadingIcon = Icons.Default.Email,
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = newPasswordState.value,
                onValueChange = { newPasswordState.value = it },
                labelText = "New Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(), // mask the original text with dot character
                enabled = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = confirmPasswordState.value,
                onValueChange = { confirmPasswordState.value = it },
                labelText = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(), // mask the original text with dot character
                enabled = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (newPasswordState.value == confirmPasswordState.value) {
                        viewModel.resetPassword(
                            emailState.value,
                            newPasswordState.value
                        ) { result ->
                            messageState.value = result
                            setIsRed(result.startsWith("Error"))
                            if (result.startsWith("Success")) {
                                setIsRed(false)
                                navController.popBackStack()
                                showDialog = true
                                //fields and states
                                resetState = result
                                emailState.value = ""
                                newPasswordState.value = ""
                                confirmPasswordState.value = ""
                                codeState.value = ""
                                adminEmailState.value = ""
                            }
                        }
                    } else {
                        messageState.value = "Passwords do not match"
                        setIsRed(true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Password")
            }
        }

    }
}