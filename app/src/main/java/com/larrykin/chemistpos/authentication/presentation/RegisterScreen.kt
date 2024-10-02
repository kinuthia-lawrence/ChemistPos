package com.larrykin.chemistpos.authentication.presentation

import CustomAlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.authentication.data.RegisterResult
import com.larrykin.chemistpos.authentication.data.RegisterViewModel
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.authentication.data.UserRepositoryImplementation
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.core.naviagation.Screen
import java.util.*


@Composable
fun RegisterScreen(viewModel: RegisterViewModel = hiltViewModel(), navController: NavController) {
    var errorMessage by remember {
        mutableStateOf("")
    }
    var showDialog by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        CustomAlertDialog(title = "Success", message = "User with email ${viewModel.email} created successfully", onDismiss = {
            showDialog = false
        }, alertState = "success")
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
        if(errorMessage.isNotEmpty()){
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
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            labelText = "Email",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Default.Email,
            enabled = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
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
            onValueChange = { viewModel.phoneNumber = it },
            labelText = "Phone Number",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Default.Edit,
            enabled = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = viewModel.chemistName,
            onValueChange = { viewModel.chemistName = it },
            labelText = "Chemist Name",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Default.Create,
            enabled = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            value = viewModel.role.toString(),
            onValueChange = { viewModel.role = Role.valueOf(it) },
            labelText = "Role",
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = Icons.Default.Face,
            enabled = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                //check if fields are empty
                if(viewModel.email.isEmpty() || viewModel.username.isEmpty() || viewModel.password.isEmpty() || viewModel.confirmPassword.isEmpty() || viewModel.phoneNumber.isEmpty() || viewModel.chemistName.isEmpty()){
                    errorMessage = "Please fill all fields"
                    return@Button
                }
                //check if password are similar
                if(viewModel.password != viewModel.confirmPassword){
                    errorMessage = "Passwords do not match"
                    return@Button
                }
                //invoke the register method
                viewModel.register { result ->
                    when (result) {
                        is RegisterResult.Success -> {
                            showDialog = true
                            navController.navigate("login")
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


