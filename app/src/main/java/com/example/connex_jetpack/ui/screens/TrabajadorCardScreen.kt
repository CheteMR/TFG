package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.connex_jetpack.R

import com.example.connex_jetpack.ui.components.BottomBar
import com.example.connex_jetpack.ui.components.OfertaCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TrabajadorCardScreen(navController: NavController) {


    Scaffold(

        bottomBar = {
            BottomBar(navController = navController, isEmpresa = true)
        }


    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4E8ADB))
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OfertaCard(
                imagenEmpresa = R.drawable.electricista, // ✅ Imagen temporal
                puesto = "Electricista",
                distanciaKm = "6 km",
                onVerMas = {
                    // Aquí puedes poner un Toast o navegación a detalles
                    // navController.navigate("detalle_oferta/123")
                },
                onLike = {
                    // Acción al dar like (por ahora un println o log)
                    println("Like dado")
                },
                onNope = {
                    // Acción al dar Nope
                    println("Oferta descartada")
                },
                onSuperLike = {
                    // Acción al dar Superlike
                    println("¡Has dado un SuperLike!")
                }
            )
        }
    }
}
