package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AnkiStudyScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.ankiState.collectAsState()
    val lessonState by viewModel.uiState.collectAsState()
    val cards = uiState.cards
    val currentIndex = uiState.currentIndex
    val currentCard = cards.getOrNull(currentIndex)
    
    val selectedWord by viewModel.selectedWord.collectAsState()
    val selectedWordTranslation by viewModel.selectedWordTranslation.collectAsState()
    val rmsDb by viewModel.rmsDb.collectAsState()
    val useSystemSpeech by viewModel.useSystemSpeechDialog.collectAsState()
    val currentAudioPlayIndex by viewModel.currentAudioPlayIndex.collectAsState()
    val audioRepetitions by viewModel.audioRepetitions.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("repite_prefs", android.content.Context.MODE_PRIVATE) }
    var favoriteDecks by remember {
        val saved = sharedPreferences.getString("anki_decks_favorites", "") ?: ""
        mutableStateOf(saved.split(",").filter { it.isNotBlank() }.toSet())
    }
    val currentDeck = uiState.currentDeck
    val isCurrentDeckFavorite = currentDeck != null && favoriteDecks.contains(currentDeck)

    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.getOrNull(0) ?: ""
            viewModel.analyzeAnkiSpokenText(spokenText)
        }
    }

    // Automatically speak when the answer is revealed
    LaunchedEffect(uiState.showAnswer, currentIndex) {
        if (uiState.showAnswer && currentCard != null) {
            viewModel.speakAnkiCurrent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = getArabicCategoryName(uiState.currentDeck ?: "جميع المجموعات"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = when(uiState.testMode) {
                                "difficult" -> "اختبار الصعب فقط ⚠️"
                                "random" -> "اختبار عشوائي 🎲"
                                else -> "دراسة اعتيادية 📖"
                            },
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    val isSwapped = uiState.swapLanguages
                    Button(
                        onClick = { viewModel.toggleAnkiLanguageSwap() },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoBlue.copy(alpha = 0.15f)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isSwapped) "إسباني ➔ عربي" else "عربي ➔ إسباني",
                            color = DuoBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (cards.isNotEmpty()) {
                        Text(
                            text = "${currentIndex + 1} / ${cards.size}",
                            modifier = Modifier.padding(end = 16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DuoBlue
                        )
                    }
                }
            )
        }
    ) { padding ->
        val cardsCount = cards.size
        if (cards.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("أحسنت! لقد أنهيت جميع البطاقات هنا.", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)) {
                        Text("العودة للقائمة", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (isCurrentDeckFavorite) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (currentDeck != null) {
                                    val updated = favoriteDecks - currentDeck
                                    favoriteDecks = updated
                                    sharedPreferences.edit().putString("anki_decks_favorites", updated.joinToString(",")).apply()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إزالة هذه المجموعة من المفضلة ⭐", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF8F9FA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Progress Bar
                    val progress = (currentIndex.toFloat() + 1) / cards.size
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = DuoBlue,
                        trackColor = Color.LightGray.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Flashcard container
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 280.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            // Card Body
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(16.dp))

                                // Dynamic Program Repetition Audio Playback Badge
                                if (currentAudioPlayIndex > 0) {
                                    Surface(
                                        color = DuoBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.VolumeUp,
                                                contentDescription = null,
                                                tint = DuoBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "استمع: $currentAudioPlayIndex / $audioRepetitions",
                                                color = DuoBlue,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }

                                // Front: Show Arabic (translation) or original depending on swapLanguages
                                if (currentCard != null) {
                                    val showSpanishOnFront = uiState.swapLanguages
                                    if (showSpanishOnFront) {
                                        val words = currentCard.text.replace(" 🔴", "🔴").split(" ")
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            words.forEachIndexed { index, word ->
                                                val isHighlighted = lessonState.highlightedIndex == index
                                                val isWrong = uiState.wrongWords.contains(word.lowercase().replace("[^a-z]".toRegex(), ""))
                                                WordItem(
                                                    word = word,
                                                    isHighlighted = isHighlighted,
                                                    isWrong = isWrong,
                                                    isCorrect = false,
                                                    isPassageMode = currentCard.contentType != "word",
                                                    isSingleWord = words.size == 1,
                                                    onClick = { viewModel.onWordClick(word) }
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = currentCard.translation,
                                            fontSize = if (currentCard.translation.length > 50) 18.sp else if (currentCard.translation.length > 30) 22.sp else 28.sp,
                                            lineHeight = if (currentCard.translation.length > 50) 24.sp else if (currentCard.translation.length > 30) 30.sp else 36.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.DarkGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // Back: Show target language sentence/word and tools when revealed
                                if (currentCard != null) {
                                    if (uiState.showAnswer) {
                                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                                        val showSpanishOnBack = !uiState.swapLanguages
                                        if (showSpanishOnBack) {
                                            val words = currentCard.text.replace(" 🔴", "🔴").split(" ")
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                words.forEachIndexed { index, word ->
                                                    val isHighlighted = lessonState.highlightedIndex == index
                                                    val isWrong = uiState.wrongWords.contains(word.lowercase().replace("[^a-z]".toRegex(), ""))
                                                    WordItem(
                                                        word = word,
                                                        isHighlighted = isHighlighted,
                                                        isWrong = isWrong,
                                                        isCorrect = false,
                                                        isPassageMode = currentCard.contentType != "word",
                                                        isSingleWord = words.size == 1,
                                                        onClick = { viewModel.onWordClick(word) }
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = currentCard.translation,
                                                fontSize = if (currentCard.translation.length > 50) 16.sp else if (currentCard.translation.length > 30) 20.sp else 26.sp,
                                                lineHeight = if (currentCard.translation.length > 50) 22.sp else if (currentCard.translation.length > 30) 28.sp else 34.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DuoBlue,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        if (!currentCard.phoneticHint.isNullOrBlank()) {
                                            Text(
                                                text = "[ ${currentCard.phoneticHint} ]",
                                                fontSize = 15.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    // Voice check panel
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Play TTS button (only if answer is revealed)
                                        if (uiState.showAnswer) {
                                            IconButton(
                                                onClick = { viewModel.speakAnkiCurrent() },
                                                modifier = Modifier
                                                    .size(54.dp)
                                                    .background(DuoBlue.copy(alpha = 0.1f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = if (currentAudioPlayIndex > 0) Icons.Rounded.VolumeDown else Icons.Rounded.VolumeUp,
                                                    contentDescription = "Speak",
                                                    tint = DuoBlue,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(24.dp))
                                        }

                                        // Mic / record button for AI pronunciation check
                                        IconButton(
                                            onClick = {
                                                if (useSystemSpeech) {
                                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                        val langTag = if (currentCard.targetLang == "es") "es-ES" else "en-US"
                                                        val promptText = if (currentCard != null) {
                                                            val langName = if (currentCard.targetLang == "es") "الإسبانية" else "الإنجليزية"
                                                            "انطق باللغة $langName 🎙️"
                                                        } else {
                                                            "اسمعك.. تحدث الآن 🎙️"
                                                        }
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
                                                        putExtra(RecognizerIntent.EXTRA_PROMPT, promptText)
                                                    }
                                                    try {
                                                        sttLauncher.launch(intent)
                                                    } catch (e: Exception) {
                                                        viewModel.analyzeAnkiSpokenText("ERR: لا يتوفر محرك صوتي في النظام ⚠️")
                                                    }
                                                } else {
                                                    viewModel.onAnkiRecordClick()
                                                }
                                            },
                                            modifier = Modifier
                                                .size(72.dp)
                                                .background(
                                                    if (uiState.isRecording) Color.Red else DuoGreen,
                                                    CircleShape
                                                )
                                        ) {
                                            Icon(
                                                if (uiState.isRecording) Icons.Rounded.Stop else Icons.Rounded.Mic,
                                                contentDescription = "Record",
                                                tint = Color.White,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }

                                    // AI Pronunciation Feedback
                                    if (uiState.isRecording) {
                                        Text(
                                            "تحدث الآن... جاري الاستماع 🎤",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                        
                                        val partialText by viewModel.partialText.collectAsState()
                                        if (partialText.isNotEmpty()) {
                                            Text(
                                                text = "\"$partialText\"",
                                                fontSize = 18.sp,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                        
                                        // RMS Db wave animation helper
                                        val barHeight = (rmsDb * 2).coerceIn(2f, 40f)
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .width(60.dp)
                                                .height(barHeight.dp)
                                                .background(Color.Red.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        )
                                    } else if (uiState.feedback.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        // AI Pronunciation Accuracy Ring
                                        val score = uiState.pronunciationScore.toInt()
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .background(
                                                    color = when {
                                                        score >= 85 -> DuoGreen.copy(alpha = 0.1f)
                                                        score >= 60 -> DuoOrange.copy(alpha = 0.1f)
                                                        else -> Color.Red.copy(alpha = 0.1f)
                                                    },
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "تقييم نطق الذكاء الاصطناعي: $score%",
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    score >= 85 -> DuoGreen
                                                    score >= 60 -> DuoOrange
                                                    else -> Color.Red
                                                }
                                            )
                                        }

                                        Text(
                                            text = uiState.feedback,
                                            fontSize = 13.sp,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                                        )
                                    }
                                }
                            }

                            // Card Footer / Controls
                            if (!uiState.showAnswer) {
                                Button(
                                    onClick = { viewModel.setShowAnswer(true) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)
                                ) {
                                    Text("كشف الإجابة 👁️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            } else {
                                // Self Evaluation Buttons (Anki Style)
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "ما هي صعوبة حفظ هذه الكلمة/الجملة بالنسبة لك؟",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Hard
                                        Button(
                                            onClick = {
                                                if (currentCard != null) {
                                                    viewModel.updateAnkiCardDifficulty(currentCard.id, 3)
                                                    advanceOrFinish(viewModel, currentIndex, cards.size, onBack)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("صعب 🔴", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Medium
                                        Button(
                                            onClick = {
                                                if (currentCard != null) {
                                                    viewModel.updateAnkiCardDifficulty(currentCard.id, 2)
                                                    advanceOrFinish(viewModel, currentIndex, cards.size, onBack)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue.copy(alpha = 0.7f)),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("متوسط 🔵", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Easy
                                        Button(
                                            onClick = {
                                                if (currentCard != null) {
                                                    viewModel.updateAnkiCardDifficulty(currentCard.id, 1)
                                                    advanceOrFinish(viewModel, currentIndex, cards.size, onBack)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("سهل 🟢", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Next/Prev Card Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { viewModel.previousAnkiCard() },
                            enabled = currentIndex > 0
                        ) {
                            Icon(Icons.Rounded.ChevronRight, contentDescription = null) // Right for RTL/Arabic layout
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("السابق")
                        }

                        TextButton(
                            onClick = { viewModel.nextAnkiCard() },
                            enabled = currentIndex < cards.size - 1
                        ) {
                            Text("تخطي")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Rounded.ChevronLeft, contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    if (selectedWord != null) {
        com.repite.conmigo.ui.components.WordTranslationDialog(
            word = selectedWord!!,
            translation = selectedWordTranslation,
            onDismiss = { viewModel.dismissWordDialog() },
            onPlayNormal = { viewModel.speakWord(selectedWord!!, slow = false) },
            onPlaySlow = { viewModel.speakWord(selectedWord!!, slow = true) },
            onSaveWord = {
                viewModel.addCardToDeck(
                    text = selectedWord!!,
                    translation = selectedWordTranslation?.takeIf { it.isNotBlank() && it != "Translating..." } ?: "بدون ترجمة",
                    category = "الكلمات المختارة",
                    contentType = "word"
                )
                android.widget.Toast.makeText(context, "تم حفظ الكلمة للمراجعة", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }
}

private fun advanceOrFinish(
    viewModel: LessonViewModel,
    currentIndex: Int,
    totalCards: Int,
    onFinish: () -> Unit
) {
    if (currentIndex < totalCards - 1) {
        viewModel.nextAnkiCard()
    } else {
        onFinish()
    }
}

