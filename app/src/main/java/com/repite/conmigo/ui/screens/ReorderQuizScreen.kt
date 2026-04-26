package com.repite.conmigo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReorderQuizScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex)
    
    var scrambledWords by remember { mutableStateOf(listOf<String>()) }
    var selectedWords by remember { mutableStateOf(listOf<String>()) }
    var isAnswered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.currentIndex, uiState.sentences) {
        if (currentSentence != null) {
            val words = currentSentence.text.split(" ").filter { it.isNotBlank() }
            scrambledWords = words.shuffled()
            selectedWords = emptyList()
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
                    "رتب الكلمات لتكون الجملة الصحيحة 🧩",
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
                            val userText = selectedWords.joinToString(" ")
                            isAnswered = true
                            isCorrect = userText.lowercase() == currentSentence?.text?.lowercase()
                        }
                    },
                    enabled = (selectedWords.isNotEmpty() || isAnswered),
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAnswered) (if (isCorrect) DuoGreen else DuoRed) else DuoBlue
                    )
                ) {
                    Text(
                        if (isAnswered) (if (isCorrect) "أحسنت! التالي" else "حاول مرة أخرى") else "تحقق من الترتيب",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "الترجمة: ${currentSentence?.translation ?: ""}",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Right
            )

            // Target area (selected words)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .minHeight(120.dp)
                    .background(Color(0xFFF7F7F7), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    selectedWords.forEach { word ->
                        WordChip(
                            text = word,
                            isSelected = true,
                            onClick = {
                                if (!isAnswered) {
                                    selectedWords = selectedWords - word
                                    scrambledWords = scrambledWords + word
                                }
                            }
                        )
                    }
                    if (selectedWords.isEmpty()) {
                        Text(
                            "اضغط على الكلمات بالأسفل لترتيبها هنا..",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Source area (scrambled words)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                scrambledWords.forEach { word ->
                    WordChip(
                        text = word,
                        isSelected = false,
                        onClick = {
                            if (!isAnswered) {
                                selectedWords = selectedWords + word
                                scrambledWords = scrambledWords - word
                            }
                        }
                    )
                }
            }

            SuccessAnimation(isVisible = isAnswered && isCorrect)
        }
    }
}

@Composable
fun WordChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) DuoBlue.copy(alpha = 0.1f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, if (isSelected) DuoBlue else Color(0xFFE5E5E5))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) DuoBlue else Color.DarkGray
        )
    }
}

private fun Modifier.minHeight(minHeight: androidx.compose.ui.unit.Dp): Modifier = this.then(
    Modifier.defaultMinSize(minHeight = minHeight)
)

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
