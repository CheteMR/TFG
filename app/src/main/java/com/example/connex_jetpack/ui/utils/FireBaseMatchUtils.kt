package com.example.connex_jetpack.ui.utils

import com.google.firebase.firestore.FirebaseFirestore





fun registrarLike(
    db: FirebaseFirestore,
    idOferta: String,
    idUsuario: String,
    tipoUsuario: String,
    onMatch: () -> Unit = {},
    onLikeRegistrado: () -> Unit = {}
) {
    val likesRef = db.collection("matches").document(idOferta)

    val userField = if (tipoUsuario == "trabajador") "likes_trabajadores" else "likes_empresas"
    val oppositeField = if (tipoUsuario == "trabajador") "likes_empresas" else "likes_trabajadores"

    db.runTransaction { transaction ->
        val snapshot = transaction.get(likesRef)
        val currentLikes = snapshot.get(userField) as? List<String> ?: emptyList()

        // Evitamos duplicar el like
        if (!currentLikes.contains(idUsuario)) {
            val updatedLikes = currentLikes + idUsuario
            transaction.update(likesRef, userField, updatedLikes)
        }

        val oppositeLikes = snapshot.get(oppositeField) as? List<String> ?: emptyList()
        val isMatch = oppositeLikes.contains(idUsuario)

        if (isMatch) {
            onMatch()
        } else {
            onLikeRegistrado()
        }
    }.addOnFailureListener {
        val initialData = mapOf(
            "likes_trabajadores" to emptyList<String>(),
            "likes_empresas" to emptyList<String>()
        )
        likesRef.set(initialData).addOnSuccessListener {
            registrarLike(db, idOferta, idUsuario, tipoUsuario, onMatch, onLikeRegistrado)
        }
    }
}
