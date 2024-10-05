package com.larrykin.chemistpos.authentication.presentation.ui

import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.larrykin.chemistpos.R
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginResult
import com.larrykin.chemistpos.authentication.presentation.viewModels.LoginViewModel
import com.larrykin.chemistpos.core.naviagation.Screen
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialogWithChoice
import com.larrykin.chemistpos.core.presentation.viewModels.AuthViewModel
import com.larrykin.chemistpos.core.presentation.viewModels.LoggedInUser
import com.larrykin.chemistpos.home.presentation.ui.HelpScreen

val defaultPadding = 16.dp
val itemSpacing = 16.dp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var loginState by remember { mutableStateOf("") }
    val (isRed, setIsRed) = rememberSaveable { mutableStateOf(false) }
    val (checked, onCheckedChange) = rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var navigateToForgotPassword by remember { mutableStateOf(false) }
    var navigateToRegister by remember { mutableStateOf(false) }


    //show dialog if fields are blank
    if (showDialog) {
        CustomAlertDialog(title = "Error", message = "Please fill in all fields", onDismiss = {
            showDialog = false
        }, alertState = "error")
    }

    //show dialog if user wants to forget password
    if (navigateToForgotPassword) {
        CustomAlertDialogWithChoice(
            title = "Confirmation",
            message = "Are you sure you want to Forget the Password?\nYou will require Internet Connection and Admin Approval",
            onDismiss = { navigateToForgotPassword = false },
            onConfirm = {
                navigateToRegister = false
                navController.navigate(Screen.ForgotPassword.route)
            },
            alertState = "confirm"
        )
    }
    //show dialog if user wants to register
    if (navigateToRegister) {
        CustomAlertDialogWithChoice(
            title = "Confirmation",
            message = "Are you sure you want to Register new account?\nYou will require Internet Connection and Admin Approval",
            onDismiss = { navigateToRegister = false },
            onConfirm = {
                navigateToRegister = false
                navController.navigate(Screen.Register.route)
            },
            alertState = "confirm"
        )
    }

    //Login Screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = checked, onCheckedChange = onCheckedChange)
                Text("Remember me")
            }
            TextButton(onClick = { navigateToForgotPassword = true }) {
                Text("Forgot Password?")
            }
        }
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
                viewModel.login { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            val user = result.user
                            authViewModel.setLoggedInUser(
                                LoggedInUser(
                                    username = user.username,
                                    email = user.email,
                                    role = user.role,
                                    chemistName = user.chemistName,
                                    phoneNumber = user.phoneNumber,
                                    createdAt = user.createdAt
                                )
                            )
                            navController.popBackStack()
                            navController.navigate(Screen.Home.route)
                            showDialog = false
                            setIsRed(false)
                            loginState = ""
                        }

                        is LoginResult.UserNotFound -> {
                            loginState = "Invalid Username or Password"
                        }

                        is LoginResult.Error -> {
                            loginState = "Error: ${result.message}"
                            setIsRed(true)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        AlternativeLoginOptions(
            onIconClick = { index ->
                when (index) {
                    0 -> Toast.makeText(context, "Instagram clicked", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(context, "GitHub clicked", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(context, "Google clicked", Toast.LENGTH_SHORT).show()
                }
            },
            //passing the navigateToRegister state to the AlternativeLoginOptions
            setNavigateToRegister = { navigateToRegister = it }
        )
    }

}

// Alternative login options
@Composable
fun AlternativeLoginOptions(
    onIconClick: (index: Int) -> Unit,
    setNavigateToRegister: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showHelpDialog by remember { mutableStateOf(false) }
    val iconList = listOf(
        R.drawable.icon_instagram,
        R.drawable.icon_github,
        R.drawable.icon_google,
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Or Sign in With")
        Spacer(Modifier.height(itemSpacing))
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            iconList.forEachIndexed { index, iconResId ->
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = "alternative Login",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            onIconClick(index)
                        }
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(defaultPadding))
            }
        }
        Spacer(Modifier.height(itemSpacing))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an Account?")
            Spacer(Modifier.height(itemSpacing))
            TextButton(
                onClick = {
                    setNavigateToRegister(true)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4003F1))
            ) {
                Text("Sign Up")
            }
        }
        Spacer(Modifier.height(itemSpacing))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Need Help? Click")
            TextButton(
                onClick = { showHelpDialog = true },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4003F1))
            ) {
                Text("Here")
            }
        }
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Close")
                    }
                },
                text = {
                    LazyColumn(modifier = Modifier.padding(0.dp)) {
                        item {
                            HelpScreen()
                        }
                    }
                }
            )
        }
    }

}

