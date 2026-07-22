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
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
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
fun BeginnerScreen(
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
                    text = "المستوى المبتدئ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = DuoGreen,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "تعلم الأساسيات: النطق السليم للحروف والكلمات البسيطة",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Lessons
            item {
                LessonCard(
                    title = "الحروف الإسبانية",
                    subtitle = "نطق الحروف الأبجدية",
                    icon = "🔡",
                    color = DuoGreen,
                    onClick = { onNavigateToLesson("Pingo: Alphabet") }
                )
            }
            item {
                var showRangeDialog by remember { mutableStateOf(false) }
                
                LessonCard(
                    title = "الأرقام الإسبانية",
                    subtitle = "تعلم الأرقام من 1 إلى 100",
                    icon = "🔢",
                    color = DuoGreen,
                    onClick = { showRangeDialog = true }
                )
                
                if (showRangeDialog) {
                    RangePickerDialog(
                        onDismiss = { showRangeDialog = false },
                        onConfirm = { from, to ->
                            showRangeDialog = false
                            viewModel.setQuizMode(false)
                            viewModel.selectCustomNumbers(from, to)
                            onNavigateToLesson("Pingo: CustomNumbers")
                        }
                    )
                }
            }
            item {
                LessonCard(
                    title = "الألوان",
                    subtitle = "أسماء الألوان الأساسية",
                    icon = "🎨",
                    color = DuoGreen,
                    onClick = { onNavigateToLesson("Pingo: Colors") }
                )
            }

            // Quizzes Section
            item {
                Text(
                    text = "اختبارات المستوى",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp),
                    textAlign = TextAlign.Right
                )
            }

            item {
                ActionCard(
                    title = "اختبار النطق الحر",
                    icon = Icons.Rounded.Quiz,
                    color = DuoBlue,
                    onClick = { onNavigateToLesson("quiz_mcq_beginner") }
                )
            }
            item {
                ActionCard(
                    title = "اختبار صحيح أم خطأ",
                    icon = Icons.Rounded.Assignment,
                    color = Color(0xFFFF9600),
                    onClick = { onNavigateToLesson("quiz_tf_beginner") }
                )
            }
            item {
                ActionCard(
                    title = "اختبار أكمل الفراغ",
                    icon = Icons.Rounded.Assignment,
                    color = Color(0xFFA100FF),
                    onClick = { onNavigateToLesson("quiz_fill_blank_beginner") }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RangePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var fromText by remember { mutableStateOf("1") }
    var toText by remember { mutableStateOf("100") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "اختر نطاق الأرقام",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "حدد نطاق الأرقام التي تود تعلمها (من 1 إلى 100):",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = toText,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                toText = it
                                errorMessage = null
                            }
                        },
                        label = { Text("إلى") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fromText,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                fromText = it
                                errorMessage = null
                            }
                        },
                        label = { Text("من") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "اختيارات سريعة:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                
                val quickOptions = listOf(
                    1 to 10,
                    1 to 20,
                    1 to 50,
                    1 to 100,
                    50 to 100
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickOptions.take(3).forEach { range ->
                        val isSelected = fromText == range.first.toString() && toText == range.second.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) DuoGreen.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) DuoGreen else Color(0xFFCCCCCC),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    fromText = range.first.toString()
                                    toText = range.second.toString()
                                    errorMessage = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${range.first} - ${range.second}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DuoGreen else Color.DarkGray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickOptions.drop(3).forEach { range ->
                        val isSelected = fromText == range.first.toString() && toText == range.second.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) DuoGreen.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) DuoGreen else Color(0xFFCCCCCC),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    fromText = range.first.toString()
                                    toText = range.second.toString()
                                    errorMessage = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${range.first} - ${range.second}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DuoGreen else Color.DarkGray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fromVal = fromText.toIntOrNull()
                    val toVal = toText.toIntOrNull()
                    if (fromVal == null || toVal == null) {
                        errorMessage = "الرجاء إدخال أرقام صالحة"
                    } else if (fromVal < 1 || toVal > 100) {
                        errorMessage = "يجب أن تكون الأرقام بين 1 و 100"
                    } else if (fromVal > toVal) {
                        errorMessage = "يجب أن يكون رقم البدء أصغر من رقم النهاية"
                    } else {
                        onConfirm(fromVal, toVal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
            ) {
                Text("ابدأ التعلم", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.Gray)
            }
        }
    )
}
