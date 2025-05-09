@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.connex_jetpack.ui.theme

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.connex_jetpack.auth.GoogleAuthUiClient
import com.example.connex_jetpack.ui.components.BottomBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.SetOptions
import java.util.*

@Composable
fun ProfileTrabajador(navController: NavController) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val googleAuthUiClient = remember { GoogleAuthUiClient(context) }
    val auth = FirebaseAuth.getInstance()

    // Estados
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var sector by remember { mutableStateOf("") }
    var profesion by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var fotoPerfilUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // 🔁 Cargar datos al entrar
    LaunchedEffect(uid) {
        db.collection("trabajadores").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    nombre = doc.getString("nombre") ?: ""
                    apellidos = doc.getString("apellidos") ?: ""
                    sector = doc.getString("sector") ?: ""
                    profesion = doc.getString("profesion") ?: ""
                    provincia = doc.getString("provincia") ?: ""
                    bio = doc.getString("bio") ?: ""
                    fotoPerfilUrl = doc.getString("fotoPerfil")
                }
            }
            .addOnFailureListener {
                Log.e("ProfileTrabajador", "Error al obtener datos: ${it.message}")
            }
    }

    // 📸 Subida de foto
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            isUploading = true
            val ref = storage.reference.child("fotos_perfil/${UUID.randomUUID()}")
            ref.putFile(it).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    fotoPerfilUrl = downloadUri.toString()
                    db.collection("trabajadores").document(uid)
                        .update("fotoPerfil", fotoPerfilUrl)
                    isUploading = false
                }
            }.addOnFailureListener {
                isUploading = false
                Log.e("Upload", "Error al subir imagen: ${it.message}")
            }
        }
    }

    Scaffold(
        //bottomBar = {
           // BottomBar(navController = navController, isEmpresa = false)

        //}

        //En el perfil se le pone un botón para cerrar sesión
                topBar = {
            TopAppBar(
                title = { Text("Perfil Trabajador") },
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

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFE3F2FD))
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 👤 Imagen con icono de cámara centrado
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (fotoPerfilUrl != null) {
                    AsyncImage(
                        model = fotoPerfilUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Cambiar foto",
                        tint = Color(0xFF1B396A),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }



            // ✏️ Campos editables
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = sector,
                onValueChange = { sector = it },
                label = { Text("Sector") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = profesion,
                onValueChange = { profesion = it },
                label = { Text("Profesión") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = provincia,
                onValueChange = { provincia = it },
                label = { Text("Provincia") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 250) bio = it },
                label = { Text("Biografía") },
                placeholder = { Text("Háblanos un poco de ti...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // 💾 Botón guardar cambios
            Button(
                onClick = {
                    db.collection("trabajadores").document(uid).set(
                        /*
                        Se cambia el .update por el .set principalmente para que
                        actualice o cree los campos sin borrar los que ya existen,
                        incluyendo fotoPerfil.
                         */
                        mapOf(
                            "nombre" to nombre,
                            "apellidos" to apellidos,
                            "sector" to sector,
                            "profesion" to profesion,
                            "provincia" to provincia,
                            "bio" to bio
                        ),
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                        Log.d("Guardar", "Perfil actualizado")
                        // Redirigimos a la pantalla principal del trabajador
                        navController.navigate("cards_trabajador"){
                            popUpTo("profiletrabajador") { inclusive = true}
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error al guardar cambios: ${it.message}", Toast.LENGTH_LONG).show()
                        Log.e("Guardar", "Fallo al actualizar: ${it.message}")
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A))
            ) {
                Text("Guardar cambios", color = Color.White)
            }
        }
    }
}