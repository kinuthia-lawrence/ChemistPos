package com.larrykin.chemistpos.authentication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.larrykin.chemistpos.authentication.data.LoginViewModel
import com.larrykin.chemistpos.core.naviagation.Screen


@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navController: NavController) {
    var loginState by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = viewModel.username,
            onValueChange = { viewModel.username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Authenticate user
                viewModel.login()
                if (viewModel.loginSuccess) {
                    navController.navigate(Screen.Home.route) //Navigate to home screen
                    loginState="Login Successful"
                } else {
                    loginState = "Login Failed"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = loginState)
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = { /* TODO: Navigate to Forgot Password Screen */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password?")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = {
                navController.navigate(Screen.Register.route) //Navigate to register screen
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
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
