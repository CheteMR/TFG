package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.R
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.OfertaCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import com.example.connex_jetpack.ui.utils.registrarLike
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


//PANTALLA DONDE SALEN LAS CARDS QUE VE EL TRABAJADOR
@Composable
fun SwipeOfertasScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val uidTrabajador = FirebaseAuth.getInstance().currentUser?.uid
    val listaOfertas = remember { mutableStateListOf<Map<String, Any>>() }
    val filtrosUsuario = remember { mutableStateOf<Map<String, Any>?>(null) }
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(uidTrabajador) {
        uidTrabajador?.let {
            db.collection("trabajadores").document(it).get()
                .addOnSuccessListener { doc ->
                    val filtros = doc.get("filtros") as? Map<String, Any>
                    filtrosUsuario.value = filtros

                    db.collection("empresas").get().addOnSuccessListener { empresas ->
                        listaOfertas.clear()
                        for (empresaDoc in empresas) {
                            val empresaId = empresaDoc.id
                            db.collection("empresas").document(empresaId).collection("ofertas")
                                .get()
                                .addOnSuccessListener { ofertas ->
                                    for (oferta in ofertas) {
                                        val datos = oferta.data.toMutableMap()
                                        datos["id"] = oferta.id
                                        datos["empresaId"] = empresaId // 👈 AÑADIDO para saber quién publica

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
                                            listaOfertas.add(datos)
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
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (listaOfertas.isEmpty()) {
                if (filtrosUsuario.value != null) {
                    Text(
                        text = "No hay ofertas que coincidan con tus filtros.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                if (currentIndex < listaOfertas.size) {
                    val oferta = listaOfertas[currentIndex]
                    val empresaId = oferta["empresaId"] as? String ?: ""
                    val idOferta = oferta["id"] as? String ?: ""

                    OfertaCard(
                        imagenEmpresa = R.drawable.logo_barpin,
                        puesto = oferta["puesto"] as? String ?: "Sin título",
                        distanciaKm = "3.2 km",
                        onVerMas = {},
                        onLike = {
                            val idTrabajador = uidTrabajador ?: return@OfertaCard
                            registrarLike(
                                db = db,
                                idOferta = idOferta,
                                idUsuarioQueDaLike = idTrabajador,
                                idUsuarioObjetivo = empresaId,
                                tipoUsuario = "trabajador",
                                onMatch = {
                                    navController.navigate("pantalla_match/$idTrabajador/$empresaId")
                                }
                            )
                            currentIndex++
                        },
                        onNope = { currentIndex++ },
                        onSuperLike = { currentIndex++ }
                    )
                } else {
                    Text(
                        text = "Has visto todas las ofertas disponibles.",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}






