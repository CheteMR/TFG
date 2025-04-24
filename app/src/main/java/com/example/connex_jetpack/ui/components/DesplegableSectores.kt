package com.example.connex_jetpack.ui.components

import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesplegableSector(selectedSector: String, onSectorSelected: (String) -> Unit) {
    val sectores = listOf(
        "Tecnología e Informática",
        "Ingeniería y Construcción",
        "Veterinaria y Medicina",
        "Educación y Profesorado",
        "Administración y Finanzas",
        "Marketing",
        "Industria y Manufactura",
        "Transporte y Logística",
        "Hostelería y Turismo",
        "Arte, Cultura y Medios",
        "Agricultura, Pesca y Medio Ambiente",
        "Derecho y Legal",
        "Ciencia e Investigación",
        "Deportes y Bienestar"
        )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedSector,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sector") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sectores.forEach { sector ->
                DropdownMenuItem(
                    text = { Text(sector) },
                    onClick = {
                        onSectorSelected(sector)
                        expanded = false
                    }
                )
            }
        }
    }
}