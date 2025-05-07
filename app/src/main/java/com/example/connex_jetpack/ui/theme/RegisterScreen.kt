package com.example.connex_jetpack.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.connex_jetpack.R
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RegisterScreen(navController: NavController) {
    var selectedOption by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val fuenteTextos = FontFamily(Font(R.font.sairastencilone))

    val estiloRotulo = TextStyle(
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontFamily = fuenteTextos,
        shadow = Shadow(
            color = Color(0xFF252525),
            offset = Offset(4f, 4f),
            blurRadius = 8f
        )
    )

    val estiloTexto = TextStyle(
        fontSize = 35.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontFamily = fuenteTextos,
        shadow = Shadow(
            color = Color(0xFF252525),
            offset = Offset(4f, 4f),
            blurRadius = 8f
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E8ADB))
            .padding(16.dp)
            .imePadding() // 👉 se adapta si el teclado está abierto
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 👉 Scroll habilitado
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 🔹 HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 🔹 TÍTULO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Registro", style = estiloRotulo)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Elige el tipo de Usuario", style = estiloTexto)

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 RADIO EMPRESA
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == "Empresa/Empleador",
                    onClick = { selectedOption = "Empresa/Empleador" }
                )
                Text(
                    text = "Empresa/Empleador",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 RADIO TRABAJADOR
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedOption == "Trabajador",
                    onClick = { selectedOption = "Trabajador" }
                )
                Text(
                    text = "Trabajador",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 🔹 BOTÓN SIGUIENTE
            Button(
                onClick = {
                    when (selectedOption) {
                        "Empresa/Empleador" -> navController.navigate("registro_empresa")
                        "Trabajador" -> navController.navigate("registro_trabajador")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = selectedOption.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Siguiente",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 BOTÓN VOLVER
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Volver", fontSize = 18.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
