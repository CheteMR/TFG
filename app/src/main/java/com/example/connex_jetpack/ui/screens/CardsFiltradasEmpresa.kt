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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                            }
                        }
                    }
                }
            }
        }
    }
}


