package com.example.connex_jetpack.ui.theme

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.connex_jetpack.R
import com.example.connex_jetpack.auth.GoogleAuthUiClient
import com.example.connex_jetpack.utils.isEmpresaGlobal
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as Activity
    val googleAuthUiClient = remember { GoogleAuthUiClient(context) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                googleAuthUiClient.signInWithIntent(intent) { success ->
                    if (success) {
                        navController.navigate("profile")
                    } else {
                        Log.e("Login", "Error al iniciar sesión con Google")
                    }
                }
            }
        }
    }

    var emailManual by remember { mutableStateOf("") }
    var passwordManual by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4E8ADB))
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "Logo de Connex",
                modifier = Modifier
                    .fillMaxWidth(0.9f) // 90% del ancho de pantalla
                    .aspectRatio(1f)    // lo mantiene cuadrado
                    .sizeIn(maxWidth = 360.dp) // no pasa de 360dp para que no se rompa
            )

            val fuenteTextos = FontFamily(Font(R.font.sairastencilone))
            val estiloTexto = TextStyle(
                fontSize = 22.sp,
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

            Text("SWIPE.", style = estiloTexto)
            Text("MATCH.", style = estiloTexto)
            Text("WORK.", style = estiloTexto)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = emailManual,
                onValueChange = { emailManual = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.LightGray,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = passwordManual,
                onValueChange = { passwordManual = it },
                label = { Text("Contraseña", color = Color.Black) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.LightGray,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                    auth.signInWithEmailAndPassword(emailManual, passwordManual)
                        .addOnSuccessListener { authResult ->
                            val uid = authResult.user?.uid
                            if (uid != null) {
                                db.collection("trabajadores").document(uid).get()
                                    .addOnSuccessListener { docTrabajador ->
                                        if (docTrabajador.exists()) {
                                            isEmpresaGlobal.value = false
                                            navController.navigate("cards_trabajador")
                                        } else {
                                            db.collection("empresas").document(uid).get()
                                                .addOnSuccessListener { docEmpresa ->
                                                    if (docEmpresa.exists()) {
                                                        isEmpresaGlobal.value = true
                                                        navController.navigate("cards_empresa")
                                                    } else {
                                                        Toast.makeText(context, "Tipo de usuario no encontrado", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Error buscando empresa: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error buscando trabajador: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(6.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B396A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            BotonLogeoGoogle {
                val signInRequest = googleAuthUiClient.getSignInRequest()
                Identity.getSignInClient(context)
                    .beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        launcher.launch(
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.e("Login", "Fallo al iniciar SignIn: ${e.localizedMessage}")
                        Toast.makeText(context, "Error al iniciar con Google", Toast.LENGTH_SHORT).show()
                    }
            }

            BotonLoginLinkedIn {
                Log.d("LinkedInSignIn", "Botón de LinkedIn presionado")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿No estás registrado aún?",
                fontSize = 14.sp,
                color = Color.White,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable {
                        navController.navigate("registro")
                    }
            )
        }
    }
}

@Composable
fun BotonLogeoGoogle(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(50.dp)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continuar con Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BotonLoginLinkedIn(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(50.dp)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.linkedin_icon),
                contentDescription = "LinkedIn Logo",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continuar con LinkedIn",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
