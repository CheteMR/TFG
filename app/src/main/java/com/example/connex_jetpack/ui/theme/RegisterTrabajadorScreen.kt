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
fun RegisterTrabajadorScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    // 🔹 Mapa de sectores con profesiones
    val sectores = mapOf(
        "Tecnología e Informática" to listOf(
            "Desarrollador de Software",
            "Ciberseguridad",
            "Inteligencia Artificial",
            "Soporte Técnico",
            "Desarrollador Web",
            "Administrador de Redes",
            "Analista de Datos"
        ),
        "Ingeniería y Construcción" to listOf(
            "Ingeniería Civil",
            "Ingeniería Industrial",
            "Ingeniería Mecánica",
            "Ingeniería Electrónica",
            "Ingeniería Química",
            "Arquitecto",
            "Diseño de Interiores",
            "Energías Renovables",
            "Obras públicas",
            "Ingeniería Naval",
            "Ingeniería de Materiales",
        ),
        "Salud y Medicina" to listOf(
            "Medicina general",
            "Pediatría",
            "Enfermería",
            "Farmacia",
            "Biotecnología",
            "Psicología y Psiquiatría",
            "Odontología",
            "Fisioterapua y Rehabilitación",
            "Veterinaria",
            "Nutrición y Dietética",
            "Asistencia Sanitaria y Geriatría"
        ),
        "Educación y Profesorado" to listOf(
            "Educación Infantil",
            "Educación Primaria",
            "Formación Profesional",
            "Educación Especial",
            "Idiomas y Traductores",
            "Pedagogía y Psicopedagogía"
        ),
        "Administración y Finanzas" to listOf(
            "Contabilidad y Auditoría",
            "Banca y Servicios financieros",
            "Inversiones, Banca privada y Bolsa",
            "Seguros",
            "RRHH y Gestión de Talento",
            "Administración y Secretariado",
        ),
        "Marketing" to listOf(
            "Marketing Digital",
            "Publicidad y Relaciones Públicas",
            "Diseño gráfico y Branding",
            "Copywriting y Creación de Contenido",
            "Community Management y RRSS",
            "Ventas",
            "E-Commerce",
            "Atención al Cliente y Call Center"
        ),
        "Industria y Manufactura" to listOf(
            "Producción industrial",
            "Control de Calidad",
            "Mecánica y Reparación",
            "Mantenimiento industrial",
            "Industria textil",
            "Minería y extracción",
            "Metalurgia y Siderurgia",
            "Petroquímica y Refinería"
        ),
        "Transporte y Logística" to listOf(
            "Transporte terrestre (camiones, taxis, VTC)",
            "Transporte marítimo",
            "Transporte aéreo y aeronaútica",
            "Logísitica y Distribución",
            "Almacén y Cadena de Suministro",
            "Mensajería y Paquetería"
        ),
        "Hostelería y Turismo" to listOf(
            "Restauración",
            "Hotelería y Alojamiento",
            "Turismo y Guías Turísticos",
            "Agencias de Viaje",
            "Entretenimiento y Ocio",
            "Organización de Eventos"
        ),
        "Arte, Cultura y Medios" to listOf(
            "Periodismo y Comunicación audiovisual",
            "Fotografía y Producción de vídeo",
            "Cine y TV",
            "DJ/Músico/Producción",
            "Radio y Locución",
            "Diseño de Moda",
            "Pintura, Escultura",
            "Ilustración y Animación"
        ),
        "Agricultura, Pesca y Medio Ambiente" to listOf(
            "Agricultura y Ganadería",
            "Pesca y Acuicultura",
            "Protección del Medio Ambiente",
            "Energías Renovables y Sostenibilidad",
            "Ingeniería Agrónoma"
        ),
        "Derecho y Legal" to listOf(
            "Abogado",
            "Procurador",
            "Pasante",
            "Mediador",
            "Oficial de Notaría",
            "Asesoría legal"
        ),
        "Ciencia e Investigación" to listOf(
            "Investigación científica",
            "Física/Química",
            "Matemáticas y Estadística"
        ),
        "Deportes y Bienestar" to listOf(
            "Entrenador Personal",
            "Fisioterapia deportiva"
        )
    )

    val sectorNombres = sectores.keys.toList()
    var expandedSector by remember { mutableStateOf(false) }
    var sectorSeleccionado by remember { mutableStateOf(sectorNombres[0]) }
    var expandedProfesion by remember { mutableStateOf(false) }
    var profesionSeleccionada by remember {
        mutableStateOf(sectores[sectorSeleccionado]?.get(0) ?: "")
    }

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
            sectorSeleccionado.isNotBlank() &&
            profesionSeleccionada.isNotBlank() &&
            password.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E8ADB))
            .imePadding()
    )

        // 🔹 FORMULARIO SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
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
                    Text("Registro de Trabajador", style = estiloTexto)

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre y Apellidos") },
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

                    Text("Selecciona tu sector", color = Color.Black)

                    ExposedDropdownMenuBox(
                        expanded = expandedSector,
                        onExpandedChange = { expandedSector = !expandedSector }
                    ) {
                        OutlinedTextField(
                            value = sectorSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sector") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSector)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedSector,
                            onDismissRequest = { expandedSector = false }
                        ) {
                            sectorNombres.forEach { sector ->
                                DropdownMenuItem(
                                    text = { Text(sector) },
                                    onClick = {
                                        sectorSeleccionado = sector
                                        profesionSeleccionada = sectores[sector]?.get(0) ?: ""
                                        expandedSector = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Selecciona tu profesión", color = Color.Black)

                    ExposedDropdownMenuBox(
                        expanded = expandedProfesion,
                        onExpandedChange = { expandedProfesion = !expandedProfesion }
                    ) {
                        OutlinedTextField(
                            value = profesionSeleccionada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Profesión") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProfesion)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedProfesion,
                            onDismissRequest = { expandedProfesion = false }
                        ) {
                            sectores[sectorSeleccionado]?.forEach { profesion ->
                                DropdownMenuItem(
                                    text = { Text(profesion) },
                                    onClick = {
                                        profesionSeleccionada = profesion
                                        expandedProfesion = false
                                    }
                                )
                            }
                        }
                    }

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

                            // Crear cuenta en Firebase Auth
                            auth.createUserWithEmailAndPassword(correo, password)
                                .addOnSuccessListener { result ->
                                    val uid = result.user?.uid ?: return@addOnSuccessListener

                                    val trabajadorData = hashMapOf(
                                        "nombre" to nombre,
                                        "correo" to correo,
                                        "teléfono" to telefono,
                                        "sector" to sectorSeleccionado,
                                        "profesión" to profesionSeleccionada,
                                        "tipo" to "trabajador"
                                    )

                                    db.collection("trabajadores").document(uid)
                                        .set(trabajadorData)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Registro completado", Toast.LENGTH_SHORT).show()
                                            navController.navigate("registro_completado")
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
