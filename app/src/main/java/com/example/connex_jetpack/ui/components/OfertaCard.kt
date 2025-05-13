package com.example.connex_jetpack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.connex_jetpack.R
import kotlinx.coroutines.launch

                //CARD DONDE SE VE LA OFERTA DE LA EMPRESA (SOLO DEBEN VERLA LOS TRABAJADORES)
@Composable
fun OfertaCard(
    imagenEmpresa: Int,
    puesto: String,
    distanciaKm: String,
    onVerMas: () -> Unit,
    onLike: () -> Unit,
    onNope: () -> Unit,
    onSuperLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val likeScale = remember { Animatable(1f) }
    val nopeScale = remember { Animatable(1f) }
    val superLikeScale = remember { Animatable(1f) }
    val cardOffsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var triggerLike by remember { mutableStateOf(false) }
    var triggerNope by remember { mutableStateOf(false) }
    var triggerSuperLike by remember { mutableStateOf(false) }

    // Animaciones
    LaunchedEffect(triggerLike) {
       if (triggerLike) {
           likeScale.animateTo(1.2f, tween(120))
           likeScale.animateTo(1f, tween(100))
           triggerLike = false
           }
       }
       LaunchedEffect(triggerNope) {
            if (triggerNope) {
                nopeScale.animateTo(1.2f, tween(100))
                nopeScale.animateTo(1f, tween(100))
                triggerNope = false
                }
            }
            LaunchedEffect(triggerSuperLike) {
                 if (triggerSuperLike) {
                 superLikeScale.animateTo(1.4f, tween(80))
                 superLikeScale.animateTo(1f, tween(120))
                 triggerSuperLike = false
                 }
            }
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
                                .weight(1f) // que ocupe el espacio disponible sin forzar altura
                        ) {
                            val maxCardHeight = maxHeight * 0.85f // Nunca más del 85% del alto disponible

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = maxCardHeight)
                                    .offset { IntOffset(cardOffsetX.value.toInt(), 0) }
                                    .shadow(8.dp, RoundedCornerShape(20.dp))
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Image(
                                            painter = painterResource(id = imagenEmpresa),
                                            contentDescription = "Imagen empresa",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomStart)
                                                .background(Color(0x99000000))
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = puesto,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "A $distanciaKm de ti",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.LightGray
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones Like/Nope/SuperLike
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // NOPE
                            ActionButton(
                                icon = R.drawable.nope,
                                color = Color(0xFFFFCDD2),
                                scale = nopeScale.value
                            ) {
                                onNope()
                                triggerNope = true
                                scope.launch {
                                    cardOffsetX.animateTo(-1000f, tween(300))
                                    cardOffsetX.snapTo(0f)
                                }
                            }

                            // SUPERLIKE
                            ActionButton(
                                icon = R.drawable.superlike,
                                color = Color(0xFFFFF9C4),
                                scale = superLikeScale.value
                            ) {
                                onSuperLike()
                                triggerSuperLike = true
                            }

                            // LIKE
                            ActionButton(
                                icon = R.drawable.like,
                                color = Color(0xFFC8E6C9),
                                scale = likeScale.value
                            ) {
                                onLike()
                                triggerLike = true
                                scope.launch {
                                    cardOffsetX.animateTo(1000f, tween(300))
                                    cardOffsetX.snapTo(0f)
                                }
                            }
                        }
                    }
                }

@Composable
fun ActionButton(icon: Int, color: Color, scale: Float, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(40.dp)
        )
    }
}