package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.repite.conmigo.ui.screens.LessonViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.speech.RecognizerIntent
import android.app.Activity
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import com.repite.conmigo.ui.theme.DuoRed
import com.repite.conmigo.ui.theme.DuoYellow
import com.repite.conmigo.ui.components.MicPulse
import com.repite.conmigo.ui.components.WordTranslationDialog
import com.repite.conmigo.ui.components.SuccessAnimation
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rmsDb by viewModel.rmsDb.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    var showTranslation by remember { mutableStateOf(false) }

    val selectedWord by viewModel.selectedWord.collectAsState()

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = "العودة",
                        tint = Color.Black
                    )
                }
                
                // BACK SENTENCE BUTTON
                IconButton(onClick = { viewModel.previousSentence() }) {
                    Icon(Icons.Rounded.ChevronLeft, contentDescription = "السابق", tint = DuoBlue)
                }

                Text("${uiState.currentIndex + 1}/${uiState.sentences.size}", fontWeight = FontWeight.Bold)

                // NEXT SENTENCE BUTTON
                IconButton(onClick = { viewModel.nextSentence() }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = "التالي", tint = DuoBlue)
                }

                Spacer(modifier = Modifier.weight(1f))
                
                LinearProgressIndicator(
                    progress = (uiState.currentIndex + 1).toFloat() / uiState.sentences.size.coerceAtLeast(1),
                    modifier = Modifier.width(80.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = DuoGreen,
                    trackColor = Color(0xFFE5E5E5)
                )
            }
        },
        bottomBar = {
            // FIXED: MICROPHONE PINNED TO BOTTOM
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val speechRate by viewModel.speechRate.collectAsState()

                    // Left Block: Trans + Speed
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showTranslation = !showTranslation }) {
                            Icon(
                                if (showTranslation) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = "Trans",
                                tint = if (showTranslation) DuoBlue else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        
                        TextButton(onClick = { viewModel.toggleSpeechRate() }, modifier = Modifier.width(60.dp)) {
                            Text("${speechRate}x", color = DuoBlue, fontWeight = FontWeight.Black)
                        }
                    }

                    // Main Mic Action
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                        MicPulse(rmsDb, uiState.isRecording)
                        Surface(
                            modifier = Modifier.size(75.dp).clickable { viewModel.onRecordClick() },
                            shape = CircleShape,
                            color = if (uiState.isRecording) DuoRed else DuoBlue,
                            shadowElevation = 8.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (uiState.isRecording) Icons.Rounded.GraphicEq else Icons.Rounded.Mic,
                                    contentDescription = "Mic",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    // Right Block: Prev + Next
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.previousSentence() }) {
                            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Prev", tint = DuoBlue, modifier = Modifier.size(28.dp))
                        }
                        IconButton(onClick = { viewModel.nextSentence() }) {
                            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next", tint = DuoBlue, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        val scrollState = androidx.compose.foundation.rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedWord != null) {
                com.repite.conmigo.ui.components.WordTranslationDialog(
                    word = selectedWord!!,
                    translation = "Traducción...",
                    onDismiss = { viewModel.dismissWordDialog() },
                    onPlayTts = { viewModel.speakWord(selectedWord!!) }
                )
            }

            // Mode Indicator
            Surface(
                modifier = Modifier.padding(bottom = 16.dp),
                color = DuoBlue.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = uiState.sentences.firstOrNull()?.category ?: "مراجعة عشوائية كاملة",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = DuoBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // ACCURACY RING (NEW ASPECT)
            AnimatedVisibility(
                visible = uiState.lastAccuracy > 0f && !uiState.isRecording,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp).padding(4.dp)) {
                    val scoreColor = when {
                        uiState.lastAccuracy >= 85 -> DuoGreen
                        uiState.lastAccuracy >= 60 -> DuoOrange
                        else -> DuoRed
                    }
                    
                    CircularProgressIndicator(
                        progress = uiState.lastAccuracy / 100f,
                        modifier = Modifier.fillMaxSize(),
                        color = scoreColor,
                        strokeWidth = 10.dp,
                        trackColor = scoreColor.copy(alpha = 0.1f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${uiState.lastAccuracy.toInt()}%",
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = scoreColor,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // THE IMAGE (AI Content)
            if (currentSentence?.imageUrl != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF0F2FF),
                    border = BorderStroke(2.dp, DuoBlue.copy(alpha = 0.2f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Support both local resource names and URLs
                        val model = if (currentSentence.imageUrl!!.startsWith("http")) {
                            currentSentence.imageUrl
                        } else {
                            // Map local name to android resource ID if needed, 
                            // but AsyncImage handles resource names if formatted correctly or context provided
                            // For simplicity, we assume we use local res names
                            "android.resource://com.repite.conmigo/drawable/${currentSentence.imageUrl}"
                        }
                        
                        AsyncImage(
                            model = model,
                            contentDescription = "AI Illustration",
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }
            }

            // THE SENTENCE
            if (currentSentence != null) {
                val words = currentSentence.text.split(" ")
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    words.forEachIndexed { index, word ->
                        val isHighlighted = uiState.highlightedIndex == index
                        val isWrong = uiState.wrongWords.contains(word.lowercase().replace("[^a-z]".toRegex(), ""))
                        val isCorrect = uiState.lastAccuracy >= 70f && !isWrong && uiState.lastAccuracy > 0
                        
                        WordItem(
                            word = word,
                            isHighlighted = isHighlighted,
                            isWrong = isWrong,
                            isCorrect = isCorrect,
                            isPassageMode = currentSentence.contentType != "word",
                            onClick = { viewModel.onWordClick(word) }
                        )
                    }
                }

                if (showTranslation) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val translationWords = currentSentence.translation.split(" ")
                        // Simplified approximate mapping for long texts
                        if (uiState.highlightedIndex != -1 && words.isNotEmpty() && translationWords.isNotEmpty()) {
                            val factor = translationWords.size.toFloat() / words.size.toFloat()
                            val approxTargetIndex = (uiState.highlightedIndex * factor).toInt().coerceIn(0, translationWords.size - 1)
                            
                            translationWords.forEachIndexed { tIndex, tWord ->
                                val isTWordHighlighted = tIndex == approxTargetIndex
                                
                                Text(
                                    tWord + " ",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (isTWordHighlighted) DuoYellow else DuoBlue,
                                    fontWeight = if (isTWordHighlighted) FontWeight.ExtraBold else FontWeight.Normal,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            translationWords.forEach { tWord ->
                                Text(
                                    tWord + " ",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = DuoBlue,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Task Hint (Moved down)
            Text(
                "Escucha y repite:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // Pulse and Feedback Box
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Quick Speaker Button
                    IconButton(onClick = { viewModel.speakCurrent() }) {
                        Icon(
                            if (uiState.highlightedIndex != -1) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp,
                            contentDescription = "Speak",
                            modifier = Modifier.size(48.dp),
                            tint = DuoBlue
                        )
                    }
                }

                // Success Animation Overlay (Trigger only for excellence)
                SuccessAnimation(isVisible = uiState.lastAccuracy >= 90f)
            }

            // Accuracy Feedback (Show if there is ANY feedback or result)
            if (uiState.feedback.isNotEmpty()) {
                val currentL = uiState.sentences.getOrNull(uiState.currentIndex)?.targetLang ?: "es"
                FeedbackCard(
                    score = uiState.lastAccuracy, 
                    feedback = uiState.feedback, 
                    transcription = uiState.lastTranscription, 
                    currentLanguage = currentL,
                    onDismiss = { viewModel.dismissFeedback() }
                )
            }

            
            Spacer(modifier = Modifier.height(110.dp)) // Cushion for Bottom Bar
        }

        // Global Sync Overlay
        if (uiState.isSyncing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "جاري مزامنة اللغة العربية... ⏳",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "يرجى الانتظار ثوانٍ قليلة",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WordItem(word: String, isHighlighted: Boolean, isWrong: Boolean, isCorrect: Boolean, isPassageMode: Boolean, onClick: () -> Unit) {
    val backgroundColor = when {
        isWrong -> DuoRed.copy(alpha = 0.15f)
        isCorrect -> DuoGreen.copy(alpha = 0.15f)
        isHighlighted -> DuoYellow.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    
    val borderColor = when {
        isPassageMode -> Color.Transparent
        isWrong -> DuoRed
        isCorrect -> DuoGreen
        isHighlighted -> DuoYellow
        else -> Color(0xFFE5E5E5)
    }

    val isLetterMode = word.length <= 2 && !isPassageMode

    Surface(
        modifier = Modifier
            .padding(if (isPassageMode) 2.dp else if (isLetterMode) 16.dp else 4.dp)
            .height(if (isPassageMode) 40.dp else if (isLetterMode) 120.dp else 54.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(if (isPassageMode) 8.dp else if (isLetterMode) 32.dp else 14.dp),
        color = backgroundColor,
        border = BorderStroke(if (isPassageMode) 0.dp else 2.dp, borderColor),
        shadowElevation = if (isPassageMode) 0.dp else 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = if (isPassageMode) 4.dp else if (isLetterMode) 32.dp else 14.dp)) {
            Text(
                text = word,
                fontSize = if (isPassageMode) 24.sp else if (isLetterMode) 80.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isWrong) DuoRed else if (isCorrect) DuoGreen else Color.DarkGray
            )
        }
    }
}

@Composable
fun FeedbackCard(score: Float, feedback: String, transcription: String, currentLanguage: String, onDismiss: () -> Unit) {
    val color = when {
        score >= 85f -> DuoGreen
        score >= 60f -> DuoOrange
        else -> DuoRed
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        border = BorderStroke(2.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("تقرير النطق الذكي", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.Gray)
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).background(color.copy(alpha = 0.1f), CircleShape)) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = color, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (score >= 95f) {
                Text("🏆", fontSize = 48.sp, modifier = Modifier.padding(bottom = 8.dp))
            }

            Text(
                feedback,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                lineHeight = 28.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (transcription.isNotEmpty()) {
                Surface(
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ما سمعه البرنامج:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(
                            "\"$transcription\"",
                            fontSize = 16.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

