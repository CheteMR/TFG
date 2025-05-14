package com.example.connex_jetpack.ui.utils

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun registrarLike(
    db: FirebaseFirestore,
    idOferta: String,
    idUsuarioQueDaLike: String,
    idUsuarioObjetivo: String,
    tipoUsuario: String,
    onMatch: () -> Unit = {},
    onLikeRegistrado: () -> Unit = {}
) {
    val likesRef = db.collection("matches").document(idOferta)
    // Campos para almacenar likes
    val userField = if (tipoUsuario == "trabajador") "likes_trabajadores" else "likes_empresas"
    val oppositeField = if (tipoUsuario == "trabajador") "likes_empresas" else "likes_trabajadores"

    db.runTransaction { transaction ->
        val snapshot = transaction.get(likesRef)
        val currentLikes = snapshot.get(userField) as? List<String> ?: emptyList()
        val updatedLikes = (currentLikes + idUsuarioQueDaLike).distinct()
        transaction.update(likesRef, userField, updatedLikes)

        val oppositeLikes = snapshot.get(oppositeField) as? List<String> ?: emptyList()
        // Match si el opuesto ya había dado like al usuario objetivo
        oppositeLikes.contains(idUsuarioObjetivo)
    }.addOnSuccessListener { isMatch ->
        if (isMatch) {
            // Determinar IDs de trabajador y empresa según rol
            val trabajadorId: String
            val empresaId: String
            if (tipoUsuario == "trabajador") {
                trabajadorId = idUsuarioQueDaLike
                empresaId = idUsuarioObjetivo
            } else {
                trabajadorId = idUsuarioObjetivo
                empresaId = idUsuarioQueDaLike
            }
            // chatId consistente: ofertaId_trabajadorId_empresaId
            val chatId = "${idOferta}_${trabajadorId}_${empresaId}"
            val chatRef = db.collection("chats").document(chatId)
            chatRef.get().addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    chatRef.set(
                        mapOf(
                            "participantes" to listOf(trabajadorId, empresaId),
                            "ofertaId" to idOferta,
                            "lastMessage" to "",
                            "lastTimestamp" to FieldValue.serverTimestamp()
                        )
                    ).addOnSuccessListener {
                        onMatch()
                    }
                } else {
                    onMatch()
                }
            }
        } else {
            onLikeRegistrado()
        }
    }.addOnFailureListener {
        // Si no existía el doc de matches, crearlo y reintentar
        val initialData = mapOf(
            "likes_trabajadores" to emptyList<String>(),
            "likes_empresas" to emptyList<String>()
        )
        likesRef.set(initialData).addOnSuccessListener {
            registrarLike(
                db,
                idOferta,
                idUsuarioQueDaLike,
                idUsuarioObjetivo,
                tipoUsuario,
                onMatch,
                onLikeRegistrado
            )
        }
    }
}



