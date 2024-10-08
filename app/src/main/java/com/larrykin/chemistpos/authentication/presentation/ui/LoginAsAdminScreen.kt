package com.larrykin.chemistpos.authentication.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginAsAdminViewModel
import com.larrykin.chemistpos.authentication.presentation.viewModels.results
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.core.naviagation.Screen
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog

@Composable
fun LoginAsAdminScreen(
    viewModel: LoginAsAdminViewModel,
    navController: NavController
) {
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var loginState by remember { mutableStateOf("") }

    //show dialog if fields are blank
    if (showDialog) {
        CustomAlertDialog(title = "Error", message = "Please fill in all fields", onDismiss = {
            showDialog = false
        }, alertState = "error")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText(
            text = "Login as Admin",
            modifier = Modifier
                .padding(vertical = defaultPadding)
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (loginState.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red)
                    .padding(8.dp)
            ) {
                Text(
                    text = loginState,
                    color = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it.trim() },
            labelText = "Username or Email",
            leadingIcon = Icons.Default.Person,
            modifier = Modifier.fillMaxWidth(),
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
            visualTransformation = PasswordVisualTransformation(), // mask the original text with dot character
            enabled = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                //check if fields are blank
                if (viewModel.username.isBlank() || viewModel.password.isBlank()) {
                    showDialog = true
                    setIsRed(true)
                    loginState = "Please fill in all fields"
                    return@Button
                }
                // Authenticate user
                viewModel.loginAsAdmin { result ->
                    when (result) {
                        is results.Success -> {
                            val user = result.user
                            navController.popBackStack()
                            navController.navigate(Screen.Settings.route)
                            showDialog = false
                            setIsRed(false)
                            loginState = ""
                        }

                        is results.UserNotFound -> {
                            loginState = "Invalid Username or Password"
                        }
                        is results.UserNotAdmin ->{
                            loginState = "User is not an Admin"
                        }

                        is results.Error -> {
                            loginState = "Error: ${result.message}"
                            setIsRed(true)
                        }

                        else -> {
                            loginState = "Unknown error"
                            setIsRed(true)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

    }

}