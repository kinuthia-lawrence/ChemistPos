package com.larrykin.chemistpos.core.presentation.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DatePicker(label: String, selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dateText = remember { mutableStateOf(dateFormat.format(selectedDate)) }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Button(onClick = {
            // Show date picker dialog
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val newDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                    dateText.value = dateFormat.format(newDate)
                    onDateSelected(newDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text(text = dateText.value)
        }
    }
}