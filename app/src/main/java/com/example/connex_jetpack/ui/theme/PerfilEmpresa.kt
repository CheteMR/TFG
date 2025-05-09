package com.example.connex_jetpack.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.connex_jetpack.R
import com.example.connex_jetpack.auth.GoogleAuthUiClient
import com.example.connex_jetpack.ui.components.BottomBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEmpresa(navController: NavController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()
    val googleAuthUiClient = remember { GoogleAuthUiClient(context) }
    val auth = FirebaseAuth.getInstance()

    var nombreEmpresa by remember { mutableStateOf<String?>(null) }
    var sector by remember { mutableStateOf<String?>("Tecnología") }
    var descripcion by remember { mutableStateOf<String?>("Empresa líder en innovación.") }
    var provincia by remember { mutableStateOf<String?> ("")}
    val listaOfertas = remember { mutableStateListOf<Map<String, Any>>() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🔁 Cargar datos
    LaunchedEffect(uid) {
        uid?.let {
            db.collection("empresas").document(it).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    nombreEmpresa = doc.getString("nombre")
                    sector = doc.getString("sector")
                    descripcion = doc.getString("descripcion")
                }
            }

            db.collection("empresas").document(it).collection("ofertas").get()
                .addOnSuccessListener { result ->
                    listaOfertas.clear()
                    result.forEach { doc ->
                        val data = doc.data.toMutableMap()
                        data["id"] = doc.id
                        listaOfertas.add(data)
                    }
                }
        }
    }

    Scaffold(
        //bottomBar = {
           // BottomBar(navController = navController, isEmpresa = true)
        //},

        //En el perfil se le pone un botón para cerrar sesión
        topBar = {
            TopAppBar(
                title = { Text("Perfil Empresa") },
                actions = {
                    IconButton(onClick = {
                        // 1) Firebase sign out
                        auth.signOut()
                        // 2) Google sign out
                        googleAuthUiClient.signOut()
                        // 3) Volver al login y limpiar backstack
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },


        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (nombreEmpresa == null) {
            // Animación de carga
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(composition, { progress }, modifier = Modifier.size(140.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD))
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item {
                    Text(
                        "Perfil de Empresa",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFF1B396A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "👔 Nombre: $nombreEmpresa",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text("🏢 Sector: $sector", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "📝 Descripción: $descripcion",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "📝 Descripción: $provincia",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // 👉 Ofertas publicadas
                item {
                    Divider()
                    Text(
                        "Ofertas publicadas",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1B396A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(listaOfertas.size) { index ->
                    val oferta = listaOfertas[index]
                    val puesto = oferta["puesto"] as? String ?: "Sin título"
                    val modalidad = oferta["modalidad"] as? String ?: ""
                    val salario = oferta["salario"] as? String ?: "No especificado"
                    val docId = oferta["id"] as? String

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                docId?.let {
                                    navController.navigate("cards_filtro_empresa/$it")
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("• $puesto", color = Color.Black)
                                Text("Modalidad: $modalidad", color = Color.Gray)
                                Text("Salario: $salario €/mes", color = Color.DarkGray)
                            }

                            IconButton(onClick = {
                                if (uid != null && docId != null) {
                                    val ofertaBackup = listaOfertas[index]
                                    db.collection("empresas").document(uid)
                                        .collection("ofertas").document(docId)
                                        .delete()
                                        .addOnSuccessListener {
                                            listaOfertas.removeAt(index)
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Oferta eliminada",
                                                    actionLabel = "Deshacer"
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    db.collection("empresas").document(uid)
                                                        .collection("ofertas").document(docId)
                                                        .set(ofertaBackup)
                                                        .addOnSuccessListener {
                                                            listaOfertas.add(index, ofertaBackup)
                                                        }
                                                }
                                            }
                                        }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }

                item {
                    Divider()
                    Button(
                        onClick = { navController.navigate("crear_oferta") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A))
                    ) {
                        Text("Crear nueva oferta", color = Color.White)
                    }
                }
            }
        }
    }
}
