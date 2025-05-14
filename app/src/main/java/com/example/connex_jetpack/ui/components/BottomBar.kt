package com.example.connex_jetpack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.material3.Text
import com.example.connex_jetpack.utils.isEmpresaGlobal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun BottomBar(navController: NavController, isEmpresa: Boolean = isEmpresaGlobal.value) {
    NavigationBar(containerColor = Color.White) {
        // 🔹 Botón INICIO
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = {
                if (isEmpresa) {
                    val empresaId = FirebaseAuth.getInstance().currentUser?.uid ?: return@NavigationBarItem
                    val db = FirebaseFirestore.getInstance()
                    db.collection("empresas").document(empresaId)
                        .collection("ofertas")
                        .limit(1)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val oferta = snapshot.documents.firstOrNull()
                            if (oferta != null) {
                                val ofertaId = oferta.id
                                navController.navigate("cards_empresa/$ofertaId/$empresaId")
                            } else {
                                navController.navigate("profileempresa")
                            }
                        }
                } else {
                    navController.navigate("cards_trabajador")
                }
            }
        )

        // 🔹 Solo para EMPRESAS: botón CREAR OFERTA
        if (isEmpresa) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva oferta") },
                label = { Text("Crear oferta") },
                selected = false,
                onClick = {
                    navController.navigate("crear_oferta")
                }
            )
        }

        // 🔹 Botón FILTROS (para ambos tipos)
        NavigationBarItem(
            icon = { Icon(Icons.Default.Tune, contentDescription = "Filtros") },
            label = { Text("Filtros") },
            selected = false,
            onClick = {
                if (isEmpresa) navController.navigate("filtros_empresa")
                else navController.navigate("filtros_trabajador")
            }
        )

        // 🔹 CHATS
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
            label = { Text("Chats") },
            selected = false,
            onClick = { navController.navigate("chats_list") }
        )

        // 🔹 PERFIL
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = false,
            onClick = {
                if (isEmpresa) navController.navigate("profileempresa")
                else navController.navigate("profiletrabajador")
            }
        )
    }
}