package com.example.connex_jetpack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.connex_jetpack.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeOfertaCards(
    listaOfertas: List<Map<String, Any>>,
    onLike: (Map<String, Any>) -> Unit,
    onNope: (Map<String, Any>) -> Unit,
    onSuperLike: (Map<String, Any>) -> Unit,
    onVerMas: (Map<String, Any>) -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableStateOf(0) }
    val offsetX = remember { Animatable(0f) }

    if (currentIndex >= listaOfertas.size) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay más ofertas disponibles", color = Color.White)
        }
        return
    }

    val oferta = listaOfertas[currentIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OfertaCard(
            imagenEmpresa = R.drawable.logo_barpin,
            puesto = oferta["puesto"] as? String ?: "Sin título",
            distanciaKm = "3.2 km", // cambiar por GPS real
            onVerMas = { onVerMas(oferta) },
            onLike = {
                scope.launch {
                    offsetX.animateTo(1000f, tween(300))
                    onLike(oferta)
                    offsetX.snapTo(0f)
                    currentIndex++
                }
            },
            onNope = {
                scope.launch {
                    offsetX.animateTo(-1000f, tween(300))
                    onNope(oferta)
                    offsetX.snapTo(0f)
                    currentIndex++
                }
            },
            onSuperLike = {
                onSuperLike(oferta)
                currentIndex++
            },
            modifier = Modifier
                .graphicsLayer {
                    translationX = offsetX.value
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value > 300f) {
                                    onLike(oferta)
                                    currentIndex++
                                } else if (offsetX.value < -300f) {
                                    onNope(oferta)
                                    currentIndex++
                                }
                                offsetX.animateTo(0f, tween(300))
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                            }
                        }
                    )
                }
        )
    }
}