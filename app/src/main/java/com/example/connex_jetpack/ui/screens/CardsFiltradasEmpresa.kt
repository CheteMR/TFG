package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.utils.registrarLike
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
/*
@Composable
fun CardsFiltradasEmpresa(navController: NavController, idOferta: String) {
    val uidEmpresa = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val candidatos = remember { mutableStateListOf<Map<String, Any>>() }
    var isLoading by remember { mutableStateOf(true) }

    // 🔍 Cargar filtros de la oferta y buscar candidatos que los cumplan
    LaunchedEffect(idOferta) {
        db.collection("empresas").document(uidEmpresa)
            .collection("ofertas").document(idOferta)
            .get()
            .addOnSuccessListener { doc ->
                val filtros = doc.data ?: return@addOnSuccessListener
                val sector = filtros["sector"] as? String
                val modalidad = filtros["modalidad"] as? String
                val contrato = filtros["tipoContrato"] as? String
                val provincia = filtros["provincia"] as? String
                val salario = (filtros["salario"] as? String)?.toIntOrNull()

                db.collection("trabajadores").get()
                    .addOnSuccessListener { result ->
                        candidatos.clear()
                        for (trabajador in result) {
                            val trab = trabajador.data
                            if (
                                (sector == null || trab["sector"] == sector) &&
                                (modalidad == null || trab["modalidad"] == modalidad) &&
                                (contrato == null || trab["contrato"] == contrato) &&
                                (provincia == null || trab["provincia"] == provincia) &&
                                (salario == null || (trab["salario"] as? Long ?: 0) <= salario)
                            ) {
                                candidatos.add(trab)
                            }
                        }

                    }
            }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController, isEmpresa = true) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            if (candidatos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay más candidatos que cumplan con los filtros seleccionados.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    candidatos.forEach { cand ->
                        val nombre = cand["nombre"] as? String ?: "Sin nombre"
                        val sector = cand["sector"] as? String ?: "Sector desconocido"
                        val provincia = cand["provincia"] as? String ?: "Ubicación desconocida"
                        val idCandidato = cand["uid"] as? String ?: return@forEach

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text("👤 $nombre", style = MaterialTheme.typography.titleMedium)
                                Text("📍 $provincia")
                                Text("🔧 Sector: $sector")

                                Button(
                                    onClick = {
                                        registrarLike(
                                            db = db,
                                            idOferta = idOferta,
                                            idUsuario = idCandidato,
                                            tipoUsuario = "empresa",
                                            onMatch = {
                                                navController.navigate("match_screen") // 👈 Asegúrate de tener esta ruta
                                            },
                                            onLikeRegistrado = {
                                                // Puedes mostrar un Toast si quieres
                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF1B396A
                                        )
                                    )
                                ) {
                                    Text("💙 Me interesa", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

*/
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsFiltradasEmpresa(navController: NavController, idOferta: String) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uidEmpresa = auth.currentUser?.uid ?: return
    var candidatos by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var loading by remember { mutableStateOf(true) }

    val sector = remember { mutableStateOf<String?>(null) }
    val contrato = remember { mutableStateOf<String?>(null) }
    val modalidad = remember { mutableStateOf<String?>(null) }
    val provincia = remember { mutableStateOf<String?>(null) }
    val salario = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(idOferta) {
        db.collection("empresas").document(uidEmpresa).collection("ofertas").document(idOferta)
            .get().addOnSuccessListener { doc ->
                sector.value = doc.getString("sector")
                contrato.value = doc.getString("tipoContrato")
                modalidad.value = doc.getString("modalidad")
                provincia.value = doc.getString("provincia")
                salario.value = (doc.get("salario") as? Long)?.toInt()

                db.collection("trabajadores").get().addOnSuccessListener { snapshot ->
                    val filtrados = mutableListOf<Map<String, Any>>()
                    for (trabajador in snapshot) {
                        val trab = trabajador.data.toMutableMap()
                        trab["uid"] = trabajador.id

                        val filtrosTrabajador = trab["filtros"] as? Map<String, Any>
                        if (filtrosTrabajador != null) {
                            val coincide =
                                (sector.value == null || filtrosTrabajador["sector"] == sector.value) &&
                                (modalidad.value == null || filtrosTrabajador["modalidad"] == modalidad.value) &&
                                (contrato.value == null || filtrosTrabajador["contrato"] == contrato.value) &&
                                (provincia.value == null || filtrosTrabajador["provincia"] == provincia.value) &&
                                (salario.value == null || (filtrosTrabajador["salario"] as? Long ?: 0) <= salario.value!!)

                            if (coincide) {
                                filtrados.add(trab)
                            }
                        }
                    }
                    candidatos = filtrados
                    loading = false
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Candidatos filtrados") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else if (candidatos.isEmpty()) {
                Text("No hay candidatos que coincidan con la oferta.")
            } else {
                candidatos.forEach { cand ->
                    val nombre = cand["nombre"] as? String ?: "Sin nombre"
                    val sector = cand["sector"] as? String ?: "Sector desconocido"
                    val provincia = cand["provincia"] as? String ?: "Ubicación desconocida"
                    val idCandidato = cand["uid"] as? String ?: return@forEach

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("👤 $nombre", style = MaterialTheme.typography.titleMedium)
                            Text("📍 $provincia")
                            Text("🔧 Sector: $sector")

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    registrarLike(
                                        db = db,
                                        idOferta = idOferta,
                                        idUsuario = idCandidato,
                                        tipoUsuario = "empresa",
                                        onMatch = {
                                            navController.navigate("pantalla_match")
                                        },
                                        onLikeRegistrado = {

                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A))
                            ) {
                                Text("💙 Me interesa", color = Color.White)
                                //Añadir un botón de No me interesa
                            }
                        }
                    }
                }
            }
        }
    }
}
