package com.repite.conmigo.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.data.Sentence
import com.repite.conmigo.logic.SpeechAnalyzer
import com.repite.conmigo.logic.SpeechToTextManager
import com.repite.conmigo.logic.TTSManager
import com.repite.conmigo.ui.components.MicPulse
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import com.repite.conmigo.ui.theme.DuoYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PracticeResult(
    val score: Float,
    val transcription: String,
    val feedback: String
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StoryReaderScreen(
    viewModel: LessonViewModel,
    deckName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Get sentences belonging to this story deck
    val allSentencesState = viewModel.allSentences.collectAsState()
    val sentences = remember(allSentencesState.value, deckName) {
        allSentencesState.value.filter { it.category == deckName }
    }
    
    // Use shared managers from viewModel to avoid SpeechRecognizer binding conflicts
    val ttsManager = viewModel.ttsManager
    val sttManager = viewModel.sttManager
    
    // State management
    var activeSentenceIndex by remember { mutableStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var autoAdvance by remember { mutableStateOf(true) }
    var currentSpeed by remember { mutableStateOf(1.0f) } // 0.6f, 1.0f, 1.4f
    
    // Voice practice results mapped by Sentence ID
    val practiceResults = remember { mutableStateMapOf<Int, PracticeResult>() }
    
    // Recording states
    var isRecording by remember { mutableStateOf(false) }
    val rmsDb by sttManager.rmsDb.collectAsState()
    val highlightedWordIndex by ttsManager.highlightedWordIndex.collectAsState()
    
    // List scrolling state
    val listState = rememberLazyListState()
    
    // Clean up managers when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.stop()
            sttManager.stopListening()
        }
    }
    
    // Monitor TTS playing status to handle auto-advance
    val isTtsSpeaking by ttsManager.isSpeaking.collectAsState()
    var wasTtsSpeaking by remember { mutableStateOf(false) }
    
    LaunchedEffect(isTtsSpeaking) {
        if (wasTtsSpeaking && !isTtsSpeaking) {
            // Finished speaking current sentence
            if (isPlaying) {
                if (autoAdvance && activeSentenceIndex < sentences.size - 1) {
                    delay(2000) // 2 seconds pause for user reflection/repeating
                    activeSentenceIndex++
                    val nextSentence = sentences[activeSentenceIndex]
                    // Scroll to make sure it's visible
                    coroutineScope.launch {
                        listState.animateScrollToItem(activeSentenceIndex)
                    }
                    ttsManager.speak(nextSentence.text, nextSentence.targetLang, currentSpeed)
                } else if (!autoAdvance || activeSentenceIndex == sentences.size - 1) {
                    isPlaying = false
                }
            }
        }
        wasTtsSpeaking = isTtsSpeaking
    }
    
    // Scroll active item into view when index changes
    LaunchedEffect(activeSentenceIndex) {
        if (sentences.isNotEmpty()) {
            listState.animateScrollToItem(activeSentenceIndex)
        }
    }

    val activeSentence = sentences.getOrNull(activeSentenceIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = deckName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        ttsManager.stop()
                        sttManager.stopListening()
                        onBack()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Control panel
            if (sentences.isNotEmpty() && activeSentence != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Quick status or feedback line
                        val result = practiceResults[activeSentence.id]
                        if (result != null) {
                            val color = if (result.score >= 90) DuoGreen else if (result.score >= 70) DuoOrange else Color.Red
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "دقة النطق: ${result.score.toInt()}% - ${result.feedback}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = color
                                )
                            }
                        } else if (isRecording) {
                            Text(
                                text = "جاري الاستماع... تحدّث الآن باللغة الإسبانية 🎙️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DuoBlue
                            )
                        } else {
                            Text(
                                text = "حدد جملة ثم انقر تشغيل أو كرر النطق خلفي",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        // Controls Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Speed cycle button
                            IconButton(
                                onClick = {
                                    currentSpeed = when (currentSpeed) {
                                        1.0f -> 0.6f
                                        0.6f -> 1.4f
                                        else -> 1.0f
                                    }
                                    // If speaking, restart with new speed
                                    if (isTtsSpeaking) {
                                        ttsManager.speak(activeSentence.text, activeSentence.targetLang, currentSpeed)
                                    }
                                }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = when(currentSpeed) {
                                            0.6f -> Icons.Rounded.DirectionsWalk // snail emoji representation
                                            1.4f -> Icons.Rounded.DirectionsRun // lightning representation
                                            else -> Icons.Rounded.DirectionsRun
                                        },
                                        contentDescription = "Speed",
                                        tint = DuoBlue
                                    )
                                    Text(
                                        text = when(currentSpeed) {
                                            0.6f -> "بطيء 🐌"
                                            1.4f -> "سريع ⚡"
                                            else -> "عادي 🚶"
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DuoBlue
                                    )
                                }
                            }

                            // 2. Previous button
                            IconButton(
                                onClick = {
                                    if (activeSentenceIndex > 0) {
                                        ttsManager.stop()
                                        isPlaying = false
                                        activeSentenceIndex--
                                    }
                                },
                                enabled = activeSentenceIndex > 0
                            ) {
                                Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous")
                            }

                            // 3. Main Play/Pause Button
                            FloatingActionButton(
                                onClick = {
                                    if (isPlaying || isTtsSpeaking) {
                                        ttsManager.stop()
                                        isPlaying = false
                                    } else {
                                        isPlaying = true
                                        ttsManager.speak(activeSentence.text, activeSentence.targetLang, currentSpeed)
                                    }
                                },
                                containerColor = DuoBlue,
                                contentColor = Color.White,
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = if (isPlaying || isTtsSpeaking) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            // 4. Next button
                            IconButton(
                                onClick = {
                                    if (activeSentenceIndex < sentences.size - 1) {
                                        ttsManager.stop()
                                        isPlaying = false
                                        activeSentenceIndex++
                                    }
                                },
                                enabled = activeSentenceIndex < sentences.size - 1
                            ) {
                                Icon(Icons.Rounded.SkipNext, contentDescription = "Next")
                            }

                            // 5. Auto advance Toggle
                            IconButton(
                                onClick = { autoAdvance = !autoAdvance }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (autoAdvance) Icons.Rounded.Autorenew else Icons.Rounded.Block,
                                        contentDescription = "Auto Advance",
                                        tint = if (autoAdvance) DuoGreen else Color.Gray
                                    )
                                    Text(
                                        text = if (autoAdvance) "تلقائي 🔁" else "يدوي 🖐️",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (autoAdvance) DuoGreen else Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Microphone repeating bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (isRecording) {
                                        sttManager.stopListening()
                                        isRecording = false
                                    } else {
                                        ttsManager.stop()
                                        isPlaying = false
                                        isRecording = true
                                        sttManager.startListening(activeSentence.targetLang) { spokenText ->
                                            isRecording = false
                                            if (spokenText.isNotEmpty()) {
                                                if (spokenText.startsWith("ERR:")) {
                                                    Toast.makeText(context, spokenText.substring(4), Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val cleanSpoken = spokenText.split("|").firstOrNull() ?: spokenText
                                                    val acc = SpeechAnalyzer.calculateAccuracy(activeSentence.text, cleanSpoken)
                                                    val feedback = SpeechAnalyzer.getFeedback(activeSentence.text, cleanSpoken)
                                                    practiceResults[activeSentence.id] = PracticeResult(
                                                        score = acc,
                                                        transcription = cleanSpoken,
                                                        feedback = feedback
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecording) Color.Red else DuoGreen
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isRecording) Icons.Rounded.Stop else Icons.Rounded.Mic,
                                        contentDescription = "Repeat"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isRecording) "إيقاف التسجيل ⏹️" else "تكرار ونطق الجملة 🎙️",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (sentences.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = DuoBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("جاري تحميل أسطر القصة...", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
                ) {
                    itemsIndexed(sentences) { index, sentence ->
                        val isActive = index == activeSentenceIndex
                        
                        // Card representing one sentence
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    ttsManager.stop()
                                    isPlaying = false
                                    activeSentenceIndex = index
                                },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (isActive) 3.dp else 1.dp,
                                color = if (isActive) DuoBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) DuoBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Line indicator
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "السطر ${index + 1} من ${sentences.size}",
                                        fontSize = 11.sp,
                                        color = if (isActive) DuoBlue else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Individual mic icon
                                        IconButton(
                                            onClick = {
                                                ttsManager.stop()
                                                activeSentenceIndex = index
                                                isPlaying = false
                                                isRecording = true
                                                sttManager.startListening(sentence.targetLang) { spokenText ->
                                                    isRecording = false
                                                    if (spokenText.isNotEmpty()) {
                                                        if (spokenText.startsWith("ERR:")) {
                                                            Toast.makeText(context, spokenText.substring(4), Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            val cleanSpoken = spokenText.split("|").firstOrNull() ?: spokenText
                                                            val acc = SpeechAnalyzer.calculateAccuracy(sentence.text, cleanSpoken)
                                                            val feedback = SpeechAnalyzer.getFeedback(sentence.text, cleanSpoken)
                                                            practiceResults[sentence.id] = PracticeResult(
                                                                score = acc,
                                                                transcription = cleanSpoken,
                                                                feedback = feedback
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Mic,
                                                contentDescription = "Repeat line",
                                                tint = if (isActive) DuoGreen else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Individual speak icon
                                        IconButton(
                                            onClick = {
                                                ttsManager.stop()
                                                activeSentenceIndex = index
                                                ttsManager.speak(sentence.text, sentence.targetLang, currentSpeed)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.VolumeUp,
                                                contentDescription = "Speak line",
                                                tint = if (isActive) DuoBlue else Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Spanish sentence with highlighting support
                                val words = sentence.text.split(" ")
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    words.forEachIndexed { wordIdx, word ->
                                        val isWordHighlighted = isActive && highlightedWordIndex == wordIdx
                                        Text(
                                            text = "$word ",
                                            fontSize = 20.sp,
                                            fontWeight = if (isWordHighlighted) FontWeight.ExtraBold else FontWeight.Normal,
                                            color = if (isWordHighlighted) DuoYellow else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Arabic translation
                                val isAr = isArabic(sentence.translation)
                                CompositionLocalProvider(LocalLayoutDirection provides (if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr)) {
                                    Text(
                                        text = sentence.translation,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = if (isAr) TextAlign.Right else TextAlign.Left
                                    )
                                }

                                // Personal practice result under card
                                val result = practiceResults[sentence.id]
                                if (result != null) {
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    Text(
                                        text = "نطقك: ${result.transcription}",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Pulsing mic animation overlay
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        MicPulse(rmsDb = rmsDb, isListening = isRecording)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "تحدّث الآن بالإسبانية...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                sttManager.stopListening()
                                isRecording = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("إلغاء ❌", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun isArabic(text: String): Boolean {
    for (i in 0 until text.length) {
        val code = text.codePointAt(i)
        if (code in 0x0600..0x06FF) {
            return true
        }
    }
    return false
}
