package com.example.connex_jetpack.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SalarioSlider(
    salario: Float,
    onSalarioChanged: (Float) -> Unit
) {
    Column {
        Text("Salario: ", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${salario.toInt()} € / mes",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF1B396A)
        )

        Slider(
            value = salario,
            onValueChange = onSalarioChanged,
            valueRange = 1000f..5000f,
            steps = 100, // Esto hará que haya puntos intermedios fijos: 1000, 2000, 3000, 4000, 5000
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF1B396A),
                activeTrackColor = Color(0xFF1B396A)
            )
        )
    }
}