package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
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
fun FillBlankQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    
    var sentenceParts by remember { mutableStateOf(listOf<String>()) }
    var blankIndex by remember { mutableStateOf(-1) }
    var options by remember { mutableStateOf(listOf<String>()) }
    var selectedWord by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }

    // Initialize the question
    LaunchedEffect(uiState.currentIndex, uiState.sentences) {
        if (currentSentence != null) {
            val words = currentSentence.text.split(" ").filter { it.isNotBlank() }
            if (words.size > 1) {
                sentenceParts = words
                blankIndex = (0 until words.size).random()
                val correctWord = words[blankIndex]
                
                // Get other words for distractors
                val otherWords = uiState.sentences
                    .flatMap { it.text.split(" ") }
                    .filter { it.isNotBlank() && it.lowercase() != correctWord.lowercase() }
                    .distinct()
                    .shuffled()
                    .take(3)
                
                options = (otherWords + correctWord).shuffled()
            } else {
                // Fallback for single words - use other sentences as distractors
                sentenceParts = listOf("____")
                blankIndex = 0
                val correctWord = currentSentence.text
                val otherWords = uiState.sentences
                    .map { it.text }
                    .filter { it.lowercase() != correctWord.lowercase() }
                    .distinct()
                    .shuffled()
                    .take(3)
                
                // If not enough words from sentences, add some common ones
                val fallbackWords = listOf("hola", "gracias", "por favor", "si", "no")
                val finalOthers = if (otherWords.size < 3) {
                    (otherWords + fallbackWords).distinct().filter { it.lowercase() != correctWord.lowercase() }.take(3)
                } else otherWords

                options = (finalOthers + correctWord).shuffled()
            }
            selectedWord = null
            isAnswered = false
        }
    }

    Scaffold(
        topBar = {
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
                    color = DuoPurple,
                    trackColor = Color(0xFFE5E5E5)
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
                            isAnswered = true
                        }
                    },
                    enabled = selectedWord != null || isAnswered,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAnswered) DuoBlue else DuoGreen
                    )
                ) {
                    Text(
                        if (isAnswered) stringResource(R.string.next) else stringResource(R.string.check),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val isSingleWord = sentenceParts.size == 1
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isSingleWord) stringResource(R.string.quiz_choose_correct_word) else stringResource(R.string.quiz_fill_blank_instruction),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${stringResource(R.string.quiz_translation_label)} ${currentSentence?.translation ?: ""}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Sentence row with blank
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center
                ) {
                    sentenceParts.forEachIndexed { index, word ->
                        if (index == blankIndex) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .widthIn(min = 100.dp)
                                    .heightIn(min = 50.dp)
                                    .background(
                                        if (isAnswered) {
                                            if (selectedWord == word) DuoGreen.copy(alpha = 0.1f) else DuoRed.copy(alpha = 0.1f)
                                        } else Color(0xFFF0F0F0),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        2.dp, 
                                        if (isAnswered) {
                                            if (selectedWord == word) DuoGreen else DuoRed
                                        } else DuoBlue.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = selectedWord ?: "____",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedWord != null) DuoBlue else Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        } else {
                            Text(
                                text = word,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(6.dp),
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(64.dp))

                // Word Options Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(options) { option ->
                        val isSelected = selectedWord == option
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp)
                                .clickable(enabled = !isAnswered) { selectedWord = option },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) DuoBlue.copy(alpha = 0.1f) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp, 
                                if (isSelected) DuoBlue else Color(0xFFE5E5E5)
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) DuoBlue else Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Success Overlay
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val correctWord = sentenceParts.getOrNull(blankIndex)
                SuccessAnimation(isVisible = isAnswered && selectedWord == correctWord)
            }
        }
    }
}

private val DuoPurple = Color(0xFFA100FF)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}
