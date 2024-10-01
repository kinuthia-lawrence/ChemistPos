package com.larrykin.chemistpos.authentication.presentation

import CustomAlertDialog
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.R
import com.larrykin.chemistpos.authentication.data.LoginResult
import com.larrykin.chemistpos.authentication.data.LoginViewModel
import com.larrykin.chemistpos.components.HeaderText
import com.larrykin.chemistpos.authentication.components.CustomTextField
import com.larrykin.chemistpos.core.naviagation.Screen

val defaultPadding = 16.dp
val itemSpacing = 16.dp

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navController: NavController) {
    var loginState by remember {
        mutableStateOf("")
    }
    val (isRed, setIsRed) = rememberSaveable {
        mutableStateOf(false)
    }
    val (checked, onCheckedChange) = rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    var showDialog by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        CustomAlertDialog(title = "Error", message = "Please fill in all fields", onDismiss = {
            showDialog = false
        }, alertState = "error")
    }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Confirmation") },
            text = {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(Color(0xFF014605))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Are you sure you want to  Forget the Password ?\nYou will require Admin Approval",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showForgotPasswordDialog = false
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF014605))
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("No")
                }
            }
        )
    }

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
            onValueChange = { viewModel.username = it },
            labelText = "Username",
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
            TextButton(onClick = { showForgotPasswordDialog = true }) {
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
                            navController.navigate(Screen.Home.route)
                            showDialog = false
                            setIsRed(false)
                            loginState = ""
                        }

                        is LoginResult.UserNotFound -> {
                            loginState = "User not found"
                            showDialog = true
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
                    0 -> {
                        Toast.makeText(context, " Coming Soon", Toast.LENGTH_SHORT).show()
                    }

                    1 -> {
                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()

                    }

                    2 -> {
                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            navController = navController,

            )
    }

}

// Alternative login options
@Composable
fun AlternativeLoginOptions(
    onIconClick: (index: Int) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
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
                    navController.navigate(Screen.Register.route)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4003F1))
            ) {
                Text("Sign Up")

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    // dummy NavController
    val navController = rememberNavController()
    LoginScreen(viewModel = hiltViewModel(), navController = navController)
}
