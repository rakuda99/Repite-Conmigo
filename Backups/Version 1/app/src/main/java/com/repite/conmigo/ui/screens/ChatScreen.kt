package com.repite.conmigo.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import com.repite.conmigo.ui.theme.DuoRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val learningLang by viewModel.learningLanguage.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Auto-speak any new AI message
    LaunchedEffect(messages) {
        val lastMsg = messages.lastOrNull()
        if (lastMsg != null && !lastMsg.isUser) {
            val speechText = lastMsg.text.split(".").first()
            viewModel.speakText(speechText, learningLang)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المُحاور الذكي 🎙️🤖", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg) {
                        val speechText = msg.text.split(".").first()
                        viewModel.speakText(speechText, learningLang)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(85.dp)
                        .padding(bottom = 12.dp)
                        .clickable {
                            viewModel.onRecordClick(isInChat = true)
                        },
                    shape = CircleShape,
                    color = if (uiState.isRecording) DuoRed else DuoBlue,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (uiState.isRecording) Icons.Rounded.GraphicEq else Icons.Rounded.Mic,
                            contentDescription = "Speak",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
            
            if (uiState.isRecording) {
                Text(
                    "أنا اسمعك.. تحدث بطلاقة 🎙️",
                    style = MaterialTheme.typography.bodySmall,
                    color = DuoRed,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatBubble(message: ChatMessage, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (message.isUser) DuoBlue else Color(0xFFF0F0F0),
            shape = RoundedCornerShape(
                topStart = 16.dp, 
                topEnd = 16.dp, 
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier.clickable { 
                val speechText = message.text.split(".").first()
                onClick() 
            }
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isUser) Color.White else Color.Black,
                fontSize = 16.sp
            )
        }
    }
}
