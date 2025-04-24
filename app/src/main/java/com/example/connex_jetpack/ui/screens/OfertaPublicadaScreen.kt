package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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



@Composable
fun OfertaPublicadaScreen(navController: NavController) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_success))
    val progress by animateLottieCompositionAsState(composition)

    Scaffold(
        containerColor = Color(0xFFE3F2FD) // Fondo azul claro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ Animación de éxito
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Texto principal
            Text(
                text = "¡Oferta publicada con éxito!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B396A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Texto secundario
            Text(
                text = "Tu oferta ya está visible para los candidatos.",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ✅ Botón para volver al perfil
            Button(
                onClick = { navController.navigate("profileempresa") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B396A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Volver al perfil", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Opción de crear otra oferta
            TextButton(onClick = { navController.navigate("crear_oferta") }) {
                Text("Crear otra oferta", color = Color(0xFF1B396A))
            }
        }
    }
}