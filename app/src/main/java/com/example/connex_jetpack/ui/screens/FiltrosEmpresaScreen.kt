package com.example.connex_jetpack.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.DesplegableContrato
import com.example.connex_jetpack.ui.components.DesplegableSector
import com.example.connex_jetpack.ui.components.DesplegableUbicacion
import com.example.connex_jetpack.ui.components.ModalidadSelector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FiltrosEmpresaScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()

    var sector by remember { mutableStateOf("") }
    var contrato by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var distanciaKm by remember { mutableStateOf(10f) }
    var salarioMin by remember { mutableStateOf(1000f) }

    Scaffold(
        bottomBar = { BottomBar(navController = navController, isEmpresa = true) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Filtra candidatos", style = MaterialTheme.typography.titleLarge, color = Color(0xFF1B396A))

            // 👉 SECTOR
            DesplegableSector(
                selectedSector = sector,
                onSectorSelected = { sector = it }
            )

            // 👉 CONTRATO
            DesplegableContrato(
                selectedContrato = contrato,
                onSelectedContrato = { contrato = it }
            )

            // 👉 MODALIDAD
            ModalidadSelector(
                selectedModalidad = modalidad,
                onModalidadSelected = { modalidad = it }
            )

            // 👉 UBICACIÓN / PROVINCIA
            DesplegableUbicacion(
                selectedProvincia = provincia,
                onSelectedProvincia = { provincia = it }
            )

            // 👉 DISTANCIA (Slider)
            Column {
                Text("Distancia máxima (km): ${distanciaKm.toInt()}")
                Slider(
                    value = distanciaKm,
                    onValueChange = { distanciaKm = it },
                    valueRange = 1f..100f,
                    steps = 100,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF1B396A),
                        activeTrackColor = Color(0xFF1B396A)
                    )
                )
            }

            // 👉 SALARIO (Slider)
            Column {
                Text("Salario mínimo esperado: ${salarioMin.toInt()} €/mes")
                Slider(
                    value = salarioMin,
                    onValueChange = { salarioMin = it },
                    valueRange = 1000f..5000f,
                    steps = 500,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF1B396A),
                        activeTrackColor = Color(0xFF1B396A)
                    )
                )
            }

            Button(
                onClick = {
                    val filtros = mapOf(
                        "sector" to sector,
                        "contrato" to contrato,
                        "modalidad" to modalidad,
                        "provincia" to provincia,
                        "distancia" to distanciaKm.toInt(),
                        "salario" to salarioMin.toInt()
                    )
                    db.collection("empresas").document(uid)
                        .update("filtros", filtros)
                        .addOnSuccessListener {
                            Log.d("FiltrosEmpresa", "Filtros guardados correctamente")
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Log.e("FiltrosEmpresa", "Error al guardar: ${it.message}")
                        }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar filtros", color = Color.White)
            }
        }
    }
}