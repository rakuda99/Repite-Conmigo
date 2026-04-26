package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortAnswerQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    
    var userAnswer by remember { mutableStateOf("") }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.currentIndex, uiState.sentences) {
        if (currentSentence != null) {
            userAnswer = ""
            isAnswered = false
            isCorrect = false
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.Gray)
                }
                Text(
                    "اكتب الترجمة الصحيحة 📝",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                Button(
                    onClick = {
                        if (isAnswered) {
                            viewModel.nextSentence()
                        } else {
                            if (userAnswer.isNotBlank()) {
                                isAnswered = true
                                val expected = currentSentence?.translation?.lowercase()?.trim() ?: ""
                                val actual = userAnswer.lowercase().trim()
                                isCorrect = actual == expected
                            }
                        }
                    },
                    enabled = userAnswer.isNotBlank() || isAnswered,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAnswered) (if (isCorrect) DuoGreen else DuoRed) else DuoBlue
                    )
                ) {
                    Text(
                        text = if (isAnswered) "التالي" else "تحقق من الإجابة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentSentence != null) {
                // Question Card
                Surface(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF7F7F7),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = currentSentence.text,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = DuoBlue,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Answer Input
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = { if (!isAnswered) userAnswer = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("اكتب ترجمة الجملة بالعربية...") },
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isAnswered,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DuoBlue,
                        unfocusedBorderColor = Color(0xFFE5E5E5)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feedback section
                AnimatedVisibility(
                    visible = isAnswered,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCorrect) DuoGreen.copy(alpha = 0.1f) else DuoRed.copy(alpha = 0.1f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                                contentDescription = null,
                                tint = if (isCorrect) DuoGreen else DuoRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isCorrect) "رائع! إجابة صحيحة." else "الإجابة الصحيحة هي:",
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) DuoGreen else DuoRed
                            )
                        }
                        if (!isCorrect) {
                            Text(
                                text = currentSentence.translation,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            SuccessAnimation(isVisible = isAnswered && isCorrect)
        }
    }
}
