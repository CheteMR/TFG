package com.example.connex_jetpack.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.DesplegableContrato
import com.example.connex_jetpack.ui.components.DesplegableSector
import com.example.connex_jetpack.ui.components.DesplegableUbicacion
import com.example.connex_jetpack.ui.components.ModalidadSelector
import com.example.connex_jetpack.ui.components.SalarioSlider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun CrearOferta(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController, isEmpresa = true)
        }
    ) { innerPadding ->

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val maxChars = 300
        val scrollState = rememberScrollState()

        // 👉 Estados del formulario
        var puesto by remember { mutableStateOf("") }
        var sectorSeleccionado by remember { mutableStateOf("") }
        var modalidadSeleccionada by remember { mutableStateOf("") }
        var provinciaSeleccionada by remember { mutableStateOf("") }
        var salarioSlider by remember { mutableStateOf(1000f) }
        var contratoSeleccionado by remember { mutableStateOf("") }
        var experiencia by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var vacantes by remember { mutableStateOf("") }

        // ✅ Contenido con scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = puesto,
                onValueChange = { puesto = it },
                label = { Text("Puesto de trabajo") },
                singleLine = true,
                colors = outlinedColors(),
                modifier = Modifier.fillMaxWidth()
            )

            DesplegableSector(
                selectedSector = sectorSeleccionado,
                onSectorSelected = { sectorSeleccionado = it }
            )

            ModalidadSelector(
                selectedModalidad = modalidadSeleccionada,
                onModalidadSelected = { modalidadSeleccionada = it }
            )

            DesplegableUbicacion(
                selectedProvincia = provinciaSeleccionada,
                onSelectedProvincia = { provinciaSeleccionada = it }
            )

            SalarioSlider(
                salario = salarioSlider,
                onSalarioChanged = { salarioSlider = it }
            )

            DesplegableContrato(
                selectedContrato = contratoSeleccionado,
                onSelectedContrato = { contratoSeleccionado = it }
            )

            OutlinedTextField(
                value = experiencia,
                onValueChange = { experiencia = it },
                label = { Text("Experiencia") },
                singleLine = true,
                colors = outlinedColors(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = {
                    if (it.length <= maxChars) descripcion = it
                },
                label = { Text("Descripción") },
                placeholder = { Text("Escribe una descripción del puesto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                maxLines = 6,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                colors = outlinedColors()
            )
            Text(
                text = "${descripcion.length} / $maxChars caracteres",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                color = Color.Gray
            )

            OutlinedTextField(
                value = vacantes,
                onValueChange = { vacantes = it },
                label = { Text("Vacantes") },
                singleLine = true,
                colors = outlinedColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // ✅ Botón de publicar oferta
            Button(
                onClick = {
                    val salarioFinal = salarioSlider.toInt().toString()

                    val nuevaOferta = hashMapOf(
                        "empresaId" to uid, // 👈 Añadir este campo
                        "puesto" to puesto,
                        "sector" to sectorSeleccionado,
                        "modalidad" to modalidadSeleccionada,
                        "provincia" to provinciaSeleccionada,
                        "salario" to salarioFinal,
                        "experiencia" to experiencia,
                        "descripcion" to descripcion,
                        "vacantes" to vacantes,
                        "tipoContrato" to contratoSeleccionado
                    )

                    uid?.let {
                        db.collection("empresas").document(it)
                            .collection("ofertas")
                            .add(nuevaOferta)
                            .addOnSuccessListener {
                                Log.d("CrearOferta", "Oferta publicada con éxito")
                                navController.navigate("oferta_publicada")
                            }
                            .addOnFailureListener { e ->
                                Log.e("CrearOferta", "Error al publicar: ${e.message}")
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar oferta", color = Color.White)
            }
        }
    }
}

@Composable
fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF1B396A),
    unfocusedBorderColor = Color.Gray,
    cursorColor = Color(0xFF1B396A),
    focusedLabelColor = Color(0xFF1B396A),
    unfocusedLabelColor = Color.Gray,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black
)