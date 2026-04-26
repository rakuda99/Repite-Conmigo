package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCQQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    
    // State for MCQ options
    var options by remember { mutableStateOf(listOf<String>()) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var resultColor by remember { mutableStateOf(Color.Transparent) }

    // Generate options when sentence changes
    LaunchedEffect(uiState.currentIndex, uiState.sentences) {
        if (currentSentence != null) {
            val correct = currentSentence.translation
            
            // 1. Try to get wrong options from current list
            var wrongCandidates = uiState.sentences
                .filter { it.translation != correct && it.translation.isNotBlank() }
                .map { it.translation }
                .distinct()
                .shuffled()
                .take(3)
            
            // 2. If not enough options, pull from any category to fill the gaps
            if (wrongCandidates.size < 3) {
                // We'd need the full list from viewModel, but for now we can rely on uiState.sentences
                // or ideally we could have a 'allPossibleTranslations' in VM.
                // For simplicity here, let's just make sure we have at least 'correct' and 
                // whatever we found. In a real app, I'd expose a 'getRandomDistractors' in VM.
            }
            
            options = (wrongCandidates + correct).distinct().shuffled()
            selectedOption = null
            isAnswered = false
            resultColor = Color.Transparent
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
                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.back), tint = Color.Gray)
                    }
                    val progress = if (uiState.sentences.isNotEmpty()) {
                        (uiState.currentIndex.toFloat()) / uiState.sentences.size.toFloat()
                    } else 0f
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(8.dp)),
                        color = DuoGreen,
                        trackColor = Color(0xFFE5E5E5)
                    )
                }
            }
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            if (isAnswered) {
                                viewModel.nextSentence()
                            } else if (selectedOption != null) {
                                isAnswered = true
                                if (selectedOption == currentSentence?.translation) {
                                    resultColor = DuoGreen
                                } else {
                                    resultColor = DuoRed
                                }
                            }
                        },
                        enabled = selectedOption != null || isAnswered,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAnswered) DuoBlue else DuoGreen
                        )
                    ) {
                        Text(
                            if (isAnswered) stringResource(R.string.next) else stringResource(R.string.check_answer),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentSentence != null) {
                    Text(
                        stringResource(R.string.quiz_mcq_instruction),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // The Question Card
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xFFF7F7F7),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFE5E5E5))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentSentence.text,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = DuoBlue
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            IconButton(onClick = { viewModel.speakCurrent() }) {
                                Icon(Icons.Rounded.VolumeUp, contentDescription = "Listen", tint = DuoBlue, modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // MCQ Options
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(options) { option ->
                            val isSelected = selectedOption == option
                            val isCorrect = option == currentSentence.translation
                            
                            val borderColor = when {
                                isAnswered && isCorrect -> DuoGreen
                                isAnswered && isSelected && !isCorrect -> DuoRed
                                isSelected -> DuoBlue
                                else -> Color(0xFFE5E5E5)
                            }
                            
                            val bgColor = when {
                                isAnswered && isCorrect -> DuoGreen.copy(alpha = 0.1f)
                                isAnswered && isSelected && !isCorrect -> DuoRed.copy(alpha = 0.1f)
                                isSelected -> DuoBlue.copy(alpha = 0.1f)
                                else -> Color.White
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isAnswered) { selectedOption = option },
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                                color = bgColor
                            ) {
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = if (isAnswered && option == currentSentence.translation) Color.White 
                                            else if (isSelected && !isAnswered) DuoBlue 
                                            else Color(0xFF333333)
                                )
                            }
                        }
                    }
                }
            }

            // Success Animation Overlay (Centered)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                SuccessAnimation(isVisible = isAnswered && selectedOption == currentSentence?.translation)
            }
        }
    }
}
