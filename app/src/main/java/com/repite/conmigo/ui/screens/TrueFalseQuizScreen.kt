package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.ui.components.SuccessAnimation
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoRed
import androidx.compose.ui.res.stringResource
import com.repite.conmigo.R
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrueFalseQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    
    var displayedTranslation by remember { mutableStateOf("") }
    var isActuallyCorrect by remember { mutableStateOf(true) }
    var userChoice by remember { mutableStateOf<Boolean?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    // Generate question when sentence changes
    LaunchedEffect(uiState.currentIndex, uiState.sentences) {
        if (currentSentence != null) {
            isActuallyCorrect = Random.nextBoolean()
            if (isActuallyCorrect) {
                displayedTranslation = currentSentence.translation
            } else {
                // Get a random wrong translation from other sentences
                val otherTranslations = uiState.sentences
                    .filter { it.translation != currentSentence.translation }
                    .map { it.translation }
                displayedTranslation = if (otherTranslations.isNotEmpty()) {
                    otherTranslations.random()
                } else {
                    currentSentence.translation + " (خطأ)" // Fallback
                }
            }
            userChoice = null
            isAnswered = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.Close, contentDescription = "Back", tint = Color.Gray)
                    }
                    val progress = if (uiState.sentences.isNotEmpty()) {
                        (uiState.currentIndex.toFloat()) / uiState.sentences.size.toFloat()
                    } else 0f
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(8.dp)),
                        color = DuoOrange, // Different color for TF quiz
                        trackColor = Color(0xFFE5E5E5)
                    )
                }
            }
        },
        bottomBar = {
            if (isAnswered) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                    Button(
                        onClick = { viewModel.nextSentence() },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)
                    ) {
                        Text(stringResource(R.string.next), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.quiz_true_false_instruction),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.DarkGray
                )
                
                Spacer(modifier = Modifier.height(40.dp))

                // Spanish Phrase Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentSentence?.text ?: "",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = DuoBlue,
                            textAlign = TextAlign.Center
                        )
                        IconButton(onClick = { viewModel.speakCurrent() }) {
                            Icon(Icons.Rounded.VolumeUp, "Listen", tint = DuoBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.quiz_translation_label), fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                // Translation Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = DuoBlue.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DuoBlue.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = displayedTranslation,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                if (!isAnswered) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // WRONG Button
                        Button(
                            onClick = { 
                                userChoice = false
                                isAnswered = true
                            },
                            modifier = Modifier.weight(1f).height(80.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoRed)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(28.dp))
                                Text(stringResource(R.string.false_label), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }

                        // CORRECT Button
                        Button(
                            onClick = { 
                                userChoice = true
                                isAnswered = true
                            },
                            modifier = Modifier.weight(1f).height(80.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(28.dp))
                                Text(stringResource(R.string.true_label), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                } else {
                    // Result Feedback
                    val isCorrect = userChoice == isActuallyCorrect
                    Text(
                        text = if (isCorrect) stringResource(R.string.quiz_result_correct) else stringResource(R.string.quiz_result_wrong),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isCorrect) DuoGreen else DuoRed
                    )
                    if (!isCorrect) {
                        Text(
                            text = stringResource(R.string.correct_translation_is, currentSentence?.translation ?: ""),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Success Overlay
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                SuccessAnimation(isVisible = isAnswered && userChoice == isActuallyCorrect)
            }
        }
    }
}

private val DuoOrange = Color(0xFFFF9600)
