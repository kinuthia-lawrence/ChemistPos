package com.larrykin.chemistpos.core.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CustomFilterField(
    medicineNames: List<String>,
    name: String,
    onNameChange: (String) -> Unit,
    labelText: String,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None

) {
    val expanded = remember { mutableStateOf(false) }
    val filteredNames = remember { mutableStateOf(medicineNames) }

    Column {
        TextField(
            value = name,
            onValueChange = {
                onNameChange(it)
                filteredNames.value = medicineNames.filter { medicineName ->
                    medicineName.contains(it, ignoreCase = true)
                }
                expanded.value = true
            },
            label = { Text(labelText) },
            leadingIcon = { if (leadingIcon != null) Icon(imageVector = leadingIcon, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(30),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded.value && filteredNames.value.isNotEmpty(),
            onDismissRequest = { expanded.value = false }
        ) {
            filteredNames.value.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion) },
                    onClick = {
                        onNameChange(suggestion)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PrevMedicineNameField() {
    CustomFilterField(
        medicineNames = listOf("Paracetamol", "Ibuprofen", "Aspirin"),
        name = "",
        onNameChange = {},
        labelText = "Medicine Name",
        leadingIcon = null,
        keyboardType = KeyboardType.Text,
        visualTransformation = VisualTransformation.None,

    )
}