package com.larrykin.chemistpos.core.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomAlertDialogWithChoice(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    alertState: String
) {
    val showDialog = remember { mutableStateOf(true) }
    val backgroundColor = if (alertState == "confirm") Color(0xFF014605) else Color.Red

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            title = {
                Text(text = "$title!!")
            },
            text = {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontStyle = FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                        onConfirm()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF014605))
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Preview
@Composable
fun CustomAlertDialogWithChoicePreview() {
    CustomAlertDialogWithChoice(
        title = "Success",
        message = "User with email created successfully",
        onDismiss = {},
        onConfirm = {},
        alertState = "confirm"
    )
}