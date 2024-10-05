package com.larrykin.chemistpos.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
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
            .padding(top = 80.dp, bottom = 0.dp, start = 80.dp, end = 0.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = androidx.compose.material3.MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .clip(shape = androidx.compose.material3.MaterialTheme.shapes.medium)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row (
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.Bottom
            ){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp)
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture"
                    )
                }

            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Username : ${userProfile.username}",
                color = Color.Black,
                fontStyle =  FontStyle.Italic
            )
            Text(text = "Email : ${userProfile.email}", color = Color.Black,fontStyle =  FontStyle.Italic)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onEdit) {
                Text(text = "Edit Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            // Button for logout
            ElevatedButton(onClick = onLogout) {
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