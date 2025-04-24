package com.example.connex_jetpack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ModalidadSelector(
    selectedModalidad: String,
    onModalidadSelected: (String) -> Unit
) {
    val opciones = listOf("Presencial", "Teletrabajo", "Híbrido")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Modalidad", style = MaterialTheme.typography.titleMedium)

        opciones.forEach { modalidad ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onModalidadSelected(modalidad) }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = selectedModalidad == modalidad,
                    onCheckedChange = { onModalidadSelected(modalidad) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF1B396A)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(modalidad)
            }
        }
    }
}