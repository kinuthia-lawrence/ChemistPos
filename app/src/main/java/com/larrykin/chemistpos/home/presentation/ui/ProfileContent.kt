package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.larrykin.chemistpos.core.presentation.viewModels.LoggedInUser

@Composable
fun ProfileContent(
    userProfile: LoggedInUser,
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(top = 48.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = androidx.compose.material3.MaterialTheme.shapes.medium // Rounded corners
    ) {
        Column(
            modifier = Modifier
                .background(Color.White) // White background for the card
                .padding(16.dp) // Padding inside the card
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(80.dp)
            )

            Text(text = "Username: ${userProfile.username}", color = Color.Black)
            Text(text = "Email: ${userProfile.email}", color = Color.Black)

            // Button to edit user details
            Button(onClick = onEdit) {
                Text(text = "Edit Profile")
            }

            // Button for logout
            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        }
    }
}

@Preview
@Composable
fun ProfileContentPreview() {
    ProfileContent(
        userProfile = LoggedInUser(
            username = "JohnDoe",
            email = "johndoe@gmail.com"
        ),
        onEdit = {},
        onLogout = {}
    )
}