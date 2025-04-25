package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.connex_jetpack.R
import com.airbnb.lottie.compose.*
import com.example.connex_jetpack.utils.isEmpresaGlobal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.setValue


@Composable
fun RegistroCompletadoScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_success))
    val progress by animateLottieCompositionAsState(composition)
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        uid?.let {
            db.collection("trabajadores").document(it).get()
                .addOnSuccessListener { docTrabajador ->
                    if (docTrabajador.exists()) {
                        isEmpresaGlobal.value = false
                    } else {
                        db.collection("empresas").document(it).get()
                            .addOnSuccessListener { docEmpresa ->
                                if (docEmpresa.exists()) {
                                    isEmpresaGlobal.value = true
                                }
                            }
                    }
                    cargando = false
                }
        }
    }

    Scaffold(
        containerColor = Color(0xFFE3F2FD)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ Animación
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Registro completado con éxito!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B396A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ve a tus filtros o completa tu perfil para que puedan encontrarte.",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(36.dp))

            if (!cargando) {
                Button(
                    onClick = {
                        if (isEmpresaGlobal.value) {
                            navController.navigate("profileempresa")
                        } else {
                            navController.navigate("profiletrabajador")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A))
                ) {
                    Text("Volver al perfil", color = Color.White, fontSize = 16.sp)
                }
            } else {
                CircularProgressIndicator(color = Color(0xFF1B396A))
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}