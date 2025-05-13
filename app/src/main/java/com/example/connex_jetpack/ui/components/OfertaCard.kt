package com.example.connex_jetpack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.swipeable
import androidx.compose.material.FractionalThreshold
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OfertaCard(
    imagenEmpresa: Int,
    puesto: String,
    distanciaKm: String,
    onVerMas: () -> Unit,
    onLike: () -> Unit,
    onNope: () -> Unit,
    onSuperLike: () -> Unit
) {
    val swipeState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, -300f to -1, 300f to 1) // swipe izquierda/nope, centro, derecha/like
    val scope = rememberCoroutineScope()

    // 🔁 Ejecutar acción cuando se arrastra suficientemente
    LaunchedEffect(swipeState.currentValue) {
        when (swipeState.currentValue) {
            -1 -> {
                onNope()
                swipeState.snapTo(0)
            }
            1 -> {
                onLike()
                swipeState.snapTo(0)
            }
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
                .weight(1f)
        ) {
            val maxCardHeight = maxHeight * 0.85f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxCardHeight)
                    .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
                    .swipeable(
                        state = swipeState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Horizontal
                    )
                    .shadow(8.dp, RoundedCornerShape(20.dp))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
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

        // Botones
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton(
                icon = R.drawable.nope,
                color = Color(0xFFFFCDD2),
                scale = 1f
            ) {
                scope.launch {
                    swipeState.animateTo(-1)
                }
            }

            ActionButton(
                icon = R.drawable.superlike,
                color = Color(0xFFFFF9C4),
                scale = 1f
            ) {
                onSuperLike()
            }

            ActionButton(
                icon = R.drawable.like,
                color = Color(0xFFC8E6C9),
                scale = 1f
            ) {
                scope.launch {
                    swipeState.animateTo(1)
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