package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.ui.components.MicPulse
import com.repite.conmigo.ui.components.SuccessAnimation
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoRed
import com.repite.conmigo.ui.theme.White

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HiddenAudioQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rmsDb by viewModel.rmsDb.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    
    // Auto-play the audio when sentence changes
    LaunchedEffect(uiState.currentIndex) {
        if (currentSentence != null) {
            viewModel.speakCurrent()
        }
    }

    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.getOrNull(0) ?: ""
            viewModel.analyzeSpokenText(spokenText)
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 8.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.Close, contentDescription = "اغلاق", tint = Color.Gray)
                    }
                    
                    val progress = if (uiState.sentences.isNotEmpty()) {
                        (uiState.currentIndex.toFloat()) / uiState.sentences.size.toFloat()
                    } else 0f
                    
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = DuoGreen,
                        trackColor = Color(0xFFE5E5E5)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = "Lives",
                        tint = DuoRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "5",
                        modifier = Modifier.padding(start = 4.dp),
                        fontWeight = FontWeight.Bold,
                        color = DuoRed
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val isCorrect = uiState.lastAccuracy >= 70f
                    
                    Button(
                        onClick = { 
                            if (isCorrect) {
                                viewModel.nextSentence()
                            } else {
                                viewModel.nextSentence() // Skip logic
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCorrect) DuoGreen else Color(0xFFE5E5E5)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = if (isCorrect) "تحقق / التالي" else "تخطّي الاختبار",
                            color = if (isCorrect) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Pulse and Feedback Box (Audio Button)
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Big audio button
                    Surface(
                        modifier = Modifier.size(120.dp).clickable { viewModel.speakCurrent() },
                        shape = CircleShape,
                        color = DuoBlue.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, DuoBlue)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.VolumeUp,
                                contentDescription = "Speak",
                                modifier = Modifier.size(64.dp),
                                tint = DuoBlue
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "استمع للكلمة جيداً ثم انطقها",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    val isCorrect = uiState.lastAccuracy >= 70f

                    // The Hidden Word (or Revealed if Correct)
                    if (currentSentence != null) {
                        AnimatedContent(targetState = isCorrect) { correct ->
                            if (correct) {
                                Text(
                                    text = currentSentence.text,
                                    fontSize = 70.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DuoGreen
                                )
                            } else {
                                Text(
                                    text = "❓❓❓",
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DuoBlue,
                                    letterSpacing = 8.sp
                                )
                            }
                        }
                    }
                }

                // Success Animation Overlay
                SuccessAnimation(isVisible = uiState.lastAccuracy >= 70f)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Mic Action (Placed here since bottom bar is now for navigation)
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(bottom = 32.dp)) {
                MicPulse(rmsDb, uiState.isRecording)
                Surface(
                    modifier = Modifier.size(90.dp).clickable { viewModel.onRecordClick() },
                    shape = CircleShape,
                    color = if (uiState.isRecording) DuoRed else DuoBlue,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (uiState.isRecording) Icons.Rounded.GraphicEq else Icons.Rounded.Mic,
                            contentDescription = "Mic",
                            tint = White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            // Accuracy Feedback
            if (uiState.feedback.isNotEmpty()) {
                val currentL = uiState.sentences.getOrNull(uiState.currentIndex)?.language ?: "es"
                FeedbackCard(
                    score = uiState.lastAccuracy, 
                    feedback = uiState.feedback, 
                    transcription = uiState.lastTranscription, 
                    currentLanguage = currentL,
                    onDismiss = { viewModel.dismissFeedback() }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
