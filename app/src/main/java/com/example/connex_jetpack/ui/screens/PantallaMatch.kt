package com.example.connex_jetpack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.connex_jetpack.R
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage

@Composable
fun PantallaMatch(
    navController: NavController,
    trabajadorId: String,
    empresaId: String,
    ofertaId: String // añadimos ofertaId para redirigir correctamente
) {
    val db = FirebaseFirestore.getInstance()
    var fotoTrabajador by remember { mutableStateOf<String?>(null) }
    var fotoEmpresa by remember { mutableStateOf<String?>(null) }

    // 🔁 Cargar fotos desde Firestore
    LaunchedEffect(trabajadorId, empresaId) {
        db.collection("trabajadores").document(trabajadorId).get()
            .addOnSuccessListener { doc ->
                fotoTrabajador = doc.getString("fotoPerfil")
            }

        db.collection("empresas").document(empresaId).get()
            .addOnSuccessListener { doc ->
                fotoEmpresa = doc.getString("fotoPerfil")
            }
    }

    Scaffold(containerColor = Color.Black) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MATCH!",
                style = TextStyle(
                    fontSize = 42.sp,
                    color = Color.Cyan,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FotoCircular(url = fotoTrabajador)
                FotoCircular(url = fotoEmpresa)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {

                    val chatId = "${ofertaId}_${trabajadorId}_${empresaId}"
                    navController.navigate("chat/$chatId")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
            ) {
                Text("Chatear ahora", color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = {
                // Volver a la lista de candidatos de la oferta para la empresa
                navController.navigate("cards_empresa/$ofertaId/$empresaId") {
                    popUpTo("pantalla_match/$trabajadorId/$empresaId") { inclusive = true }
                }
            }) {
                Text("Seguir buscando", color = Color.Gray)
            }
        }
    }
}

@Composable
fun FotoCircular(url: String?) {
    val painter = if (!url.isNullOrEmpty()) {
        rememberAsyncImagePainter(url)
    } else {
        painterResource(id = R.drawable.avatar)
    }

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Cyan, CircleShape)
    )
}










