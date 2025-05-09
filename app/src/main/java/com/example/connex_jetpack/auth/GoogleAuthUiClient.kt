package com.example.connex_jetpack.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.connex_jetpack.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GoogleAuthUiClient(

    /*
        private val context: Context
    ) {
        private val oneTapClient: SignInClient = Identity.getSignInClient(context)
        private val auth: FirebaseAuth = Firebase.auth



        fun getSignInRequest(): BeginSignInRequest {
            return BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("898281598321-shlkustg8ntdvns7286p9in86rjibk11.apps.googleusercontent.com") // 👈 lo saco del paso de abajo
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()
        }

        fun signInWithIntent(intent: Intent, onComplete: (Boolean) -> Unit) {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val idToken = credential.googleIdToken
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

            auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
        }

        fun getSignedInUser() = auth.currentUser

        fun signOut() {
            auth.signOut()
        }
 private val auth: FirebaseAuth = Firebase.auth
 fun signOut() {
        auth.signOut()
    }
     */



    private val context: Context
) {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val auth: FirebaseAuth = Firebase.auth

    fun getSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false) // permite seleccionar cualquier cuenta
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun signInWithIntent(
        intent: Intent,
        onResult: (Boolean, FirebaseUser?) -> Unit
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        val user = FirebaseAuth.getInstance().currentUser
                        onResult(task.isSuccessful, user)
                    }
            } else {
                Log.e("GoogleAuthUiClient", "ID Token es null")
                onResult(false, null)
            }
        } catch (e: Exception) {
            Log.e("GoogleAuthUiClient", "Error en signInWithIntent", e)
            onResult(false, null)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}