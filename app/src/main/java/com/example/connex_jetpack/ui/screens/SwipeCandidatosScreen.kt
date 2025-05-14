package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.R
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.TrabajadorCard
import com.example.connex_jetpack.ui.utils.registrarLike
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SwipeCandidatosScreen(
    navController: NavController,
    ofertaId: String,
    empresaId: String
) {
    val db = FirebaseFirestore.getInstance()
    val listaTrabajadores = remember { mutableStateListOf<Map<String, Any>>() }
    var currentIndex by remember { mutableStateOf(0) }
    var filtrosOferta by remember { mutableStateOf<Map<String, Any>?>(null) }

    // 🔄 Cargar filtros de la oferta
    LaunchedEffect(ofertaId) {
        db.collection("empresas").document(empresaId).collection("ofertas").document(ofertaId)
            .get().addOnSuccessListener { doc ->
                val filtros = doc.data
                filtrosOferta = filtros

                // 🔄 Buscar trabajadores compatibles
                db.collection("trabajadores").get().addOnSuccessListener { trabajadores ->
                    listaTrabajadores.clear()
                    for (trabajadorDoc in trabajadores) {
                        val datos = trabajadorDoc.data.toMutableMap()
                        datos["id"] = trabajadorDoc.id

                        val coincide = filtros?.let {
                            val sectorOk = it["sector"] == "" || it["sector"] == datos["sector"]
                            val contratoOk = it["contrato"] == "" || it["contrato"] == datos["contrato"]
                            val modalidadOk = it["modalidad"] == "" || it["modalidad"] == datos["modalidad"]
                            val provinciaOk = it["provincia"] == "" || it["provincia"] == datos["provincia"]
                            val salarioFiltro = (it["salario"] as? Long)?.toInt() ?: 0
                            val salarioTrabajador = when (val valor = datos["salarioDeseado"]) {
                                is String -> valor.toIntOrNull()
                                is Long -> valor.toInt()
                                is Int -> valor
                                else -> null
                            }
                            val salarioOk = salarioTrabajador?.let { s -> s <= salarioFiltro } ?: true
                            sectorOk && contratoOk && modalidadOk && provinciaOk && salarioOk
                        } ?: true

                        if (coincide) {
                            listaTrabajadores.add(datos)
                        }
                    }
                }
            }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController, isEmpresa = true) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4E8ADB))
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (listaTrabajadores.isEmpty()) {
                if (filtrosOferta != null) {
                    Text(
                        text = "No hay candidatos que coincidan con esta oferta.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                if (currentIndex < listaTrabajadores.size) {
                    val trabajador = listaTrabajadores[currentIndex]

                    TrabajadorCard(
                        fotoUrl = trabajador["fotoPerfilUrl"] as? String, // ⚠️ Puedes personalizarlo más adelante
                        profesion = trabajador["profesion"] as? String ?: "Profesión no disponible",
                        distanciaKm = "2.1", // ⚠️ Luego puedes calcular distancia real por GPS
                        onVerMas = {
                            // Navegar a pantalla de detalles
                        },
                        onLike = {
                            val idEmpresa = FirebaseAuth.getInstance().currentUser?.uid ?: return@TrabajadorCard
                            val idTrabajador = trabajador["id"] as? String ?: return@TrabajadorCard
                            registrarLike(
                                db = db,
                                idOferta = ofertaId,
                                idUsuarioQueDaLike = idEmpresa,
                                idUsuarioObjetivo = idTrabajador,
                                tipoUsuario = "empresa",
                                onMatch = {
                                    navController.navigate("pantalla_match/$ofertaId/$idTrabajador/$idEmpresa")
                                }
                            )
                            currentIndex++
                        },
                        onNope = {
                            currentIndex++
                        }
                    )
                } else {
                    Text(
                        text = "Has visto todos los candidatos disponibles.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}