package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.connex_jetpack.R
import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.utils.registrarLike
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
        },
        bottomBar = { BottomBar(navController = navController, isEmpresa = true) }
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
                    val sectorText = cand["sector"] as? String ?: "Sector desconocido"
                    val provinciaText = cand["provincia"] as? String ?: "Ubicación desconocida"
                    val idCandidato = cand["uid"] as? String ?: return@forEach
                    val fotoPerfilUrl = cand["fotoPerfil"] as? String

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val painter = if (!fotoPerfilUrl.isNullOrEmpty()) {
                                rememberAsyncImagePainter(fotoPerfilUrl)
                            } else {
                                painterResource(id = R.drawable.avatar)
                            }

                            Image(
                                painter = painter,
                                contentDescription = "Foto del trabajador",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(MaterialTheme.shapes.medium)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(nombre, style = MaterialTheme.typography.titleMedium)
                            Text("📍 $provinciaText")
                            Text("🔧 $sectorText")

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        registrarLike(
                                            db = db,
                                            idOferta = idOferta,
                                            idUsuarioQueDaLike = uidEmpresa,
                                            idUsuarioObjetivo = idCandidato,
                                            tipoUsuario = "empresa",
                                            onMatch = {
                                                navController.navigate("pantalla_match/$idOferta/$idCandidato/$uidEmpresa")
                                            },
                                            onLikeRegistrado = {}
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                                ) {
                                    Text("Like", color = Color.White)
                                }

                                Button(
                                    onClick = {
                                        // Acción de 'Nope' opcional
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
                                ) {
                                    Text("Nope", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

