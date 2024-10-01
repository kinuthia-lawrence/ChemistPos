import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomAlertDialog(title: String, message: String, onDismiss: () -> Unit, alertState: String) {
    val showDialog = remember { mutableStateOf(true) }
    val backgroundColor = if (alertState == "success") Color(0xFF014605) else Color.Red

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            title = {
                Text(
                    text = "$title!!",
                    style = MaterialTheme.typography.titleLarge,
                    color = backgroundColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .fillMaxWidth()
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = {
                            showDialog.value = false
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
                    ) {
                        Text(text = "Dismiss", fontStyle = FontStyle.Italic)
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun CustomAlertDialogPreview() {
    CustomAlertDialog(title = "Title", message = "This is a message", onDismiss = {}, alertState = "success")
}