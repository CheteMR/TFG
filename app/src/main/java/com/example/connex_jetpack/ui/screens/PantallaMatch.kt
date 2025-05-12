package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PantallaMatch(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFFFFEBEE)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉 ¡Es un Match!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Ahora podéis empezar a hablar y coordinar una entrevista.")
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    navController.navigate("cards_trabajador") {
                        popUpTo("match_screen") { inclusive = true }
                    }
                }
            ) {
                Text("Seguir navegando")
            }
        }
    }
}





