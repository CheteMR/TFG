package com.example.connex_jetpack.ui.theme

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun LoginTelefonoScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var numeroTelefono by remember { mutableStateOf("") }
    var codigoEnviado by remember { mutableStateOf(false) }
    var codigoVerificacion by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    val activity = context as Activity

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneCredential(credential, firebaseAuth, db, navController, context)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verifId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            verificationId = verifId
            resendToken = token
            codigoEnviado = true
            Toast.makeText(context, "Código enviado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!codigoEnviado) {
            OutlinedTextField(
                value = numeroTelefono,
                onValueChange = { numeroTelefono = it },
                label = { Text("Número de teléfono (+34...)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(numeroTelefono)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }) {
                Text("Enviar código")
            }
        } else {
            OutlinedTextField(
                value = codigoVerificacion,
                onValueChange = { codigoVerificacion = it },
                label = { Text("Código de verificación") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val cred = PhoneAuthProvider.getCredential(verificationId!!, codigoVerificacion)
                signInWithPhoneCredential(cred, firebaseAuth, db, navController, context)
            }) {
                Text("Verificar y continuar")
            }
        }
    }
}



private fun signInWithPhoneCredential(
    credential: PhoneAuthCredential,
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore,
    navController: NavController,
    context: Context
) {
    firebaseAuth.signInWithCredential(credential)
        .addOnSuccessListener { result ->
            val uid = result.user?.uid ?: return@addOnSuccessListener

            // Guardar en Firestore como trabajador
            db.collection("trabajadores").document(uid).set(
                mapOf(
                    "nombre" to "",
                    "telefono" to result.user?.phoneNumber.orEmpty(),
                    "bio" to "",
                    "sector" to "",
                    "profesion" to "",
                    "provincia" to "",
                    "apellidos" to ""
                ),
                SetOptions.merge()
            ).addOnSuccessListener {
                Toast.makeText(context, "Sesión iniciada con éxito", Toast.LENGTH_SHORT).show()
                navController.navigate("cards_trabajador") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al iniciar sesión: ${it.message}", Toast.LENGTH_LONG).show()
        }
}