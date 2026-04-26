package com.repite.conmigo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.ui.text.style.TextAlign
import com.repite.conmigo.ui.components.LessonCard
import com.repite.conmigo.ui.components.ActionCard
import com.repite.conmigo.ui.components.OutlinedActionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    onAddLesson: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "المستوى المتقدم",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = DuoOrange,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "تحديات متقدمة: قراءة نصوص، تحليل لغوي، ومواضيع معقدة",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Lessons
            item {
                LessonCard(
                    title = "فعل الكينونة",
                    subtitle = "الفرق بين Ser و Estar",
                    icon = "🧠",
                    color = DuoOrange,
                    onClick = { onNavigateToLesson("Pingo: Ser y Estar") }
                )
            }
            item {
                LessonCard(
                    title = "الماضي التام",
                    subtitle = "التعبير عن أحداث منتهية",
                    icon = "📜",
                    color = DuoOrange,
                    onClick = { onNavigateToLesson("Pingo: Past Tense") }
                )
            }
            item {
                LessonCard(
                    title = "أدوات الربط",
                    subtitle = "تكوين جمل معقدة",
                    icon = "🔗",
                    color = DuoOrange,
                    onClick = { onNavigateToLesson("Pingo: Linkers") }
                )
            }

            // Quizzes Section
            item {
                Text(
                    text = "اختبارات المستوى",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    textAlign = TextAlign.Right
                )
            }

            item {
                ActionCard(
                    title = "تحدي النطق المتقدم",
                    icon = Icons.Rounded.Quiz,
                    color = DuoBlue,
                    onClick = { onNavigateToLesson("quiz_mcq_advanced") }
                )
            }
            item {
                ActionCard(
                    title = "تحدي صحيح أم خطأ المتقدم",
                    icon = Icons.Rounded.Assignment,
                    color = Color(0xFFFF9600),
                    onClick = { onNavigateToLesson("quiz_tf_advanced") }
                )
            }
            item {
                ActionCard(
                    title = "تحدي أكمل الفراغ المتقدم",
                    icon = Icons.Rounded.Assignment,
                    color = Color(0xFFA100FF),
                    onClick = { onNavigateToLesson("quiz_fill_blank_advanced") }
                )
            }

            // Custom Lesson
            item {
                OutlinedActionCard(
                    title = "إضافة درس مخصص متقدم",
                    icon = Icons.Rounded.Add,
                    color = DuoOrange,
                    onClick = onAddLesson
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
