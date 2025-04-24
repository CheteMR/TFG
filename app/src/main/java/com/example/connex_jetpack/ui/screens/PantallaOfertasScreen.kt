package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.R
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.OfertaCard
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


//PANTALLA DONDE SALEN LAS CARDS QUE VE EL TRABAJADOR
@Composable
fun PantallaOfertasScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val listaOfertasFiltradas = remember { mutableStateListOf<Map<String, Any>>() }
    val filtrosUsuario = remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(uid) {
        uid?.let {
            // 🔹 Primero obtenemos los filtros del trabajador
            db.collection("trabajadores").document(it).get()
                .addOnSuccessListener { doc ->
                    val filtros = doc.get("filtros") as? Map<String, Any>
                    filtrosUsuario.value = filtros

                    // 🔹 Luego traemos todas las ofertas de todas las empresas
                    db.collection("empresas").get().addOnSuccessListener { empresas ->
                        listaOfertasFiltradas.clear()
                        for (empresaDoc in empresas) {
                            val empresaId = empresaDoc.id
                            db.collection("empresas").document(empresaId).collection("ofertas").get()
                                .addOnSuccessListener { ofertas ->
                                    for (oferta in ofertas) {
                                        val datos = oferta.data

                                        // ✅ Aplica los filtros:
                                        val coincide = filtros?.let {
                                            val sectorOk = it["sector"] == "" || it["sector"] == datos["sector"]
                                            val contratoOk = it["contrato"] == "" || it["contrato"] == datos["tipoContrato"]
                                            val modalidadOk = it["modalidad"] == "" || it["modalidad"] == datos["modalidad"]
                                            val provinciaOk = it["provincia"] == "" || it["provincia"] == datos["provincia"]

                                            val salarioFiltro = (it["salario"] as? Long)?.toInt() ?: 0
                                            val salarioOferta = when (val valor = datos["salario"]) {
                                                is String -> valor.toIntOrNull()
                                                is Long -> valor.toInt()
                                                is Int -> valor
                                                else -> null
                                            }

                                            val salarioOk = salarioOferta?.let { s -> s >= salarioFiltro } ?: true

                                            sectorOk && contratoOk && modalidadOk && provinciaOk && salarioOk
                                        } ?: true

                                        if (coincide) {
                                            listaOfertasFiltradas.add(datos)
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController, isEmpresa = false) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4E8ADB))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (listaOfertasFiltradas.isEmpty()) {
                CircularProgressIndicator(color = Color.White)
            } else {
                // Por ahora solo mostramos una (mejorar luego con swipe completo)
                val oferta = listaOfertasFiltradas.first()
                OfertaCard(
                    imagenEmpresa = R.drawable.logo_barpin,
                    puesto = oferta["puesto"] as? String ?: "Sin título",
                    distanciaKm = "3.2 km", // Temporal, se calculará con GPS
                    onVerMas = {},
                    onLike = {},
                    onNope = {},
                    onSuperLike = {}
                )
            }
        }
    }
}

