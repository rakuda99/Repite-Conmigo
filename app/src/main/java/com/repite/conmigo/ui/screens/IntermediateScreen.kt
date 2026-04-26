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
fun IntermediateScreen(
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
                    text = "المستوى المتوسط",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = DuoBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "تطوير المهارات: محادثات، جمل أطول، وتحسين القواعد",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Lessons
            item {
                LessonCard(
                    title = "الجمل اليومية",
                    subtitle = "تعبيرات مستخدمة بكثرة",
                    icon = "🗣️",
                    color = DuoBlue,
                    onClick = { onNavigateToLesson("Pingo: Conversation") }
                )
            }
            item {
                LessonCard(
                    title = "الوقت والأيام",
                    subtitle = "كيف تخبر بالأوقات",
                    icon = "🕒",
                    color = DuoBlue,
                    onClick = { onNavigateToLesson("Pingo: Time") }
                )
            }
            item {
                LessonCard(
                    title = "في المطعم",
                    subtitle = "طلب الطعام باللغة الإسبانية",
                    icon = "🍴",
                    color = DuoBlue,
                    onClick = { onNavigateToLesson("Pingo: Restaurant") }
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
                    title = "اختبار الاستماع والنطق",
                    icon = Icons.Rounded.Quiz,
                    color = DuoBlue,
                    onClick = { onNavigateToLesson("quiz_mcq_intermediate") }
                )
            }
            item {
                ActionCard(
                    title = "اختبار صحيح أم خطأ",
                    icon = Icons.Rounded.Assignment,
                    color = Color(0xFFFF9600),
                    onClick = { onNavigateToLesson("quiz_tf_intermediate") }
                )
            }
            item {
                ActionCard(
                    title = "اختبار أكمل الفراغ",
                    icon = Icons.Rounded.Assignment,
                    color = Color(0xFFA100FF),
                    onClick = { onNavigateToLesson("quiz_fill_blank_intermediate") }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
