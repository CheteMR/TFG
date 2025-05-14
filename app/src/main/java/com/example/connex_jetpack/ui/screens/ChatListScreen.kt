package com.example.connex_jetpack.ui.screens

import android.R.attr.type
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Screen that lists all chats the user participates in
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsListScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Query chats where user is participant, ordered by lastTimestamp desc
    val query = db.collection("chats")
        .whereArrayContains("participantes", currentUserId)
        .orderBy("lastTimestamp", Query.Direction.DESCENDING)

    var chats by remember { mutableStateOf<List<com.google.firebase.firestore.DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(Unit) {
        query.snapshots().collectLatest { snapshot ->
            chats = snapshot.documents
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Chats") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chats, key = { it.id }) { chatDoc ->
                val participants = chatDoc.get("participantes") as? List<String> ?: emptyList()
                val otherId = participants.firstOrNull { it != currentUserId } ?: ""
                val lastMsg = chatDoc.getString("lastMessage") ?: ""
                ChatListItem(otherId, lastMsg) {
                    navController.navigate("chat/${chatDoc.id}")
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    otherId: String,
    lastMsg: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = otherId, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lastMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Screen for an individual chat
@Composable
fun ChatScreen(navController: NavController, chatId: String) {
    val db = FirebaseFirestore.getInstance()
    val messagesCollection = db.collection("chats").document(chatId)
        .collection("messages")
    val messagesQuery = messagesCollection
        .orderBy("timestamp", Query.Direction.ASCENDING)

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var messages by remember { mutableStateOf<List<com.google.firebase.firestore.DocumentSnapshot>>(emptyList()) }

    LaunchedEffect(chatId) {
        messagesQuery.snapshots().collectLatest { snap ->
            messages = snap.documents
        }
    }

    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages, key = { it.id }) { doc ->
                val text = doc.getString("text") ?: ""
                val sender = doc.getString("senderId") ?: ""
                MessageBubble(text = text, isMine = sender == currentUserId)
            }
        }
        Divider()
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Escribe un mensaje...") },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                if (inputText.isNotBlank()) {
                    messagesCollection.add(
                        mapOf(
                            "senderId" to currentUserId,
                            "text" to inputText,
                            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )
                    )
                    db.collection("chats").document(chatId)
                        .update(
                            "lastMessage", inputText,
                            "lastTimestamp", com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )
                    inputText = ""
                }
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }
}

@Composable
fun MessageBubble(text: String, isMine: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isMine) Color(0xFF81C784) else Color.LightGray,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                color = if (isMine) Color.White else Color.Black
            )
        }
    }
}





