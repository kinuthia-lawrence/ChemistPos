package com.larrykin.chemistpos.home.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.larrykin.chemistpos.authentication.data.Role
import com.larrykin.chemistpos.core.presentation.ui.CustomAlertDialogWithChoice
import com.larrykin.chemistpos.core.data.LoggedInUser
import com.larrykin.chemistpos.home.presentation.viewModels.ProfileViewModel
import java.util.Date

@Composable
fun ProfileContent(
    userProfile: LoggedInUser,
    onLogout: () -> Unit,
    profileViewModel: ProfileViewModel,
    parentNavController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showLogoutDialog = remember { mutableStateOf(false) }
    var showEditProfileDialog = remember { mutableStateOf(false) }



    if (showLogoutDialog.value) {
       CustomAlertDialogWithChoice(
           title = "Logout",
           message = "Are you sure you want to logout?",
           onDismiss = { showLogoutDialog.value = false },
           onConfirm = { onLogout() },
           alertState = "confirm"
       )
    }
    if (showEditProfileDialog.value) {
       CustomAlertDialogWithChoice(
           title = "Edit Profile",
           message = "Are you sure you want to edit your profile?, you will require Admin privileges",
           onDismiss = { showEditProfileDialog.value = false },
           onConfirm = { parentNavController.navigate("settings") },
           alertState = "confirm"
       )
    }
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
                IconButton(onClick = { Toast.makeText(context, "Adding Profile Picture Coming Soon!", Toast.LENGTH_SHORT).show() }) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Email : ${userProfile.email}", color = Color.Black,fontStyle =  FontStyle.Italic)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Role : ${userProfile.role}", color = Color.Black,fontStyle =  FontStyle.Italic)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Chemist Name : ${userProfile.chemistName}", color = Color.Black,fontStyle =  FontStyle.Italic)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Phone Number : ${userProfile.phoneNumber}", color = Color.Black,fontStyle =  FontStyle.Italic)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Created At : ${userProfile.createdAt}", color = Color.Black,fontStyle =  FontStyle.Italic)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { showEditProfileDialog.value = true }) {
                Text(text = "Edit Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))
            // Button for logout
            ElevatedButton(onClick = { showLogoutDialog.value = true }) {
                Text(text = "Logout")
            }
        }
    }
}

