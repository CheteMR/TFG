package com.example.connex_jetpack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.connex_jetpack.R
import coil.compose.rememberAsyncImagePainter

//CARD DONDE SE VE AL TRABAJADOR (SOLO DEBE VERLAS LAS EMPRESAS)
@Composable
fun TrabajadorCard(
    fotoUrl: String?,
    profesion: String,
    distanciaKm: String,
    onVerMas: () -> Unit,
    onLike: () -> Unit,
    onNope: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E8ADB))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val maxCardHeight = maxHeight * 0.9f

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxCardHeight)
                    .padding(10.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 🔁 Imagen con Coil (cargar URL o imagen local si no hay URL)
                    val painter = if (!fotoUrl.isNullOrEmpty()) {
                        rememberAsyncImagePainter(fotoUrl)
                    } else {
                        painterResource(id = R.drawable.avatar) // asegúrate de tener esta imagen en drawable
                    }

                    Image(
                        painter = painter,
                        contentDescription = "Foto del trabajador",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = profesion,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B396A)
                        )
                        Text(
                            text = "A $distanciaKm de ti",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onNope,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Nope", color = Color.White)
                        }

                        Button(
                            onClick = onVerMas,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ver más", color = Color.Black)
                        }

                        Button(
                            onClick = onLike,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                            shape = RoundedCornerShape(12.dp)

                        ) {
                            Text("Like", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}