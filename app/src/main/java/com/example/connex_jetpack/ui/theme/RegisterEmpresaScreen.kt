package com.example.connex_jetpack.ui.theme

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.connex_jetpack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEmpresaScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cif by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) } //Menú desplegable -> PLEGADO
    val context = LocalContext.current



    val fuenteTextos = FontFamily(Font(R.font.sairastencilone))
    val estiloTexto = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
        textAlign = TextAlign.Center,
        fontFamily = fuenteTextos,
        shadow = Shadow(color = Color(0xFF252525), offset = Offset(2f, 2f), blurRadius = 8f)
    )

        val formularioValido = nombre.isNotBlank() &&
            correo.isNotBlank() &&
            telefono.isNotBlank() &&
            cif.isNotBlank() &&
            password.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E8ADB))
    ) {
        // 🔹 HEADER FIJO
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Menú
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Inicio") }, onClick = {
                            menuExpanded = false; navController.navigate("home")
                        })
                        DropdownMenuItem(text = { Text("Configuración") }, onClick = {
                            menuExpanded = false; navController.navigate("settings")
                        })
                        DropdownMenuItem(text = { Text("Cerrar Sesión") }, onClick = {
                            menuExpanded = false
                        })
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )

                IconButton(onClick = { navController.navigate("edit_profile") }) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                }
            }
        }

        // 🔹 FORMULARIO SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Registro de Empresa", style = estiloTexto)

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre empresa") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cif,
                        onValueChange = { cif = it },
                        label = { Text("CIF") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it.trim() },
                        label = { Text("Correo Electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val auth = FirebaseAuth.getInstance()
                            val db = FirebaseFirestore.getInstance()

                            // Validación de email
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                                Toast.makeText(context, "Correo no válido", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Validación de contraseña
                            if (password.length < 6) {
                                Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            //Validación del CIF
                            val regexCIF = Regex("^[A-HJ-NP-SUVW]\\d{7}[0-9A-J]\$")
                            val cifLimpio = cif.trim().uppercase()
                            if (!regexCIF.matches(cif)){
                                Toast.makeText(context, "CIF no válido.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            //Verificar si el CIF ya existe en BBDD
                            db.collection("empresas")
                                .whereEqualTo("cif", cif)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        Toast.makeText(context, "Ya existe una empresa con ese CIF", Toast.LENGTH_SHORT).show()
                                        return@addOnSuccessListener
                                    }
                                }

                            // Crear cuenta en Firebase Auth
                            auth.createUserWithEmailAndPassword(correo, password)
                                .addOnSuccessListener { result ->
                                    val uid = result.user?.uid ?: return@addOnSuccessListener

                                    val empresaData = hashMapOf(
                                        "nombre" to nombre,
                                        "correo" to correo,
                                        "teléfono" to telefono,
                                        "cif" to cif,
                                        "tipo" to "empresa"
                                    )

                                    db.collection("empresas").document(uid)
                                        .set(empresaData)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Registro completado", Toast.LENGTH_SHORT).show()
                                            navController.navigate("profile")
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error al registrar usuario: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        },
                        enabled = formularioValido,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (formularioValido) Color(0xFF4E8ADB) else Color.LightGray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Registrarse",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Volver", color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
