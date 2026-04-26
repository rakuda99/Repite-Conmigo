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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
fun MatchingQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // State for matching
    var spanishWords by remember { mutableStateOf(listOf<String>()) }
    var arabicWords by remember { mutableStateOf(listOf<String>()) }
    var selectedSpanish by remember { mutableStateOf<String?>(null) }
    var selectedArabic by remember { mutableStateOf<String?>(null) }
    var matchedPairs by remember { mutableStateOf(setOf<Pair<String, String>>()) }
    var wrongPairs by remember { mutableStateOf(setOf<Pair<String, String>>()) }
    var isFinished by remember { mutableStateOf(false) }

    // Map for checking
    val wordMap = remember(uiState.currentIndex, uiState.sentences) {
        if (uiState.sentences.isEmpty()) return@remember emptyMap<String, String>()
        
        // Use currentIndex as a seed to pick 5 random items every time we change.
        // This ensures TURN 1 and TURN 2 are completely different sets.
        val random = java.util.Random(uiState.currentIndex.toLong())
        val indices = (0 until uiState.sentences.size).shuffled(random).take(minOf(5, uiState.sentences.size))
        indices.map { uiState.sentences[it] }.associate { it.text to it.translation }
    }

    LaunchedEffect(uiState.currentIndex, uiState.sentences) {
        if (wordMap.isNotEmpty()) {
            spanishWords = wordMap.keys.toList().shuffled()
            arabicWords = wordMap.values.toList().shuffled()
            matchedPairs = emptySet()
            wrongPairs = emptySet()
            isFinished = false
        }
    }

    // Logic for matching
    LaunchedEffect(selectedSpanish, selectedArabic) {
        if (selectedSpanish != null && selectedArabic != null) {
            val correctArabic = wordMap[selectedSpanish]
            if (correctArabic == selectedArabic) {
                matchedPairs = matchedPairs + (selectedSpanish!! to selectedArabic!!)
                selectedSpanish = null
                selectedArabic = null
                if (matchedPairs.size == spanishWords.size) {
                    isFinished = true
                }
            } else {
                val pair = selectedSpanish!! to selectedArabic!!
                wrongPairs = wrongPairs + pair
                kotlinx.coroutines.delay(500)
                wrongPairs = wrongPairs - pair
                selectedSpanish = null
                selectedArabic = null
            }
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
                    stringResource(R.string.quiz_matching_instruction),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        bottomBar = {
            if (isFinished) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
                    Button(
                        onClick = { viewModel.nextSentence() },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
                    ) {
                        Text(stringResource(R.string.well_done_next), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Spanish Column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    spanishWords.forEach { word ->
                        val isMatched = matchedPairs.any { it.first == word }
                        val isSelected = selectedSpanish == word
                        val isWrong = wrongPairs.any { it.first == word }

                        MatchingChip(
                            text = word,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            isWrong = isWrong,
                            onClick = { if (!isMatched) selectedSpanish = word }
                        )
                    }
                }

                // Arabic Column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    arabicWords.forEach { word ->
                        val isMatched = matchedPairs.any { it.second == word }
                        val isSelected = selectedArabic == word
                        val isWrong = wrongPairs.any { it.second == word }

                        MatchingChip(
                            text = word,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            isWrong = isWrong,
                            onClick = { if (!isMatched) selectedArabic = word }
                        )
                    }
                }
            }

            SuccessAnimation(isVisible = isFinished)
        }
    }
}

@Composable
fun MatchingChip(
    text: String,
    isSelected: Boolean,
    isMatched: Boolean,
    isWrong: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isMatched -> DuoGreen
        isWrong -> DuoRed
        isSelected -> DuoBlue
        else -> Color(0xFFE5E5E5)
    }

    val bgColor = when {
        isMatched -> DuoGreen.copy(alpha = 0.1f)
        isWrong -> DuoRed.copy(alpha = 0.1f)
        isSelected -> DuoBlue.copy(alpha = 0.1f)
        else -> Color.White
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .alpha(if (isMatched) 0.5f else 1.0f)
            .clickable(enabled = !isMatched) { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        color = bgColor
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(8.dp)) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMatched) DuoGreen else Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }
}
