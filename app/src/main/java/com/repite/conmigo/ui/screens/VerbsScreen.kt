package com.repite.conmigo.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.repite.conmigo.logic.SpeechAnalyzer
import com.repite.conmigo.models.Verb
import com.repite.conmigo.ui.theme.DuoBlue
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerbsScreen(
    viewModel: LessonViewModel
) {
    val verbs by viewModel.verbs.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val isGenerating by viewModel.isGeneratingVerbs.collectAsState()
    val progressMsg by viewModel.generatingVerbsProgress.collectAsState()

    val filteredVerbs = remember(searchQuery, verbs) {
        if (searchQuery.isBlank()) verbs else verbs.filter {
            it.infinitive.contains(searchQuery, ignoreCase = true) ||
            it.meaning.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = DuoBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "إضافة فعل")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
        Text(
            text = "تصريف الأفعال",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "أهم 100 فعل في اللغة الإسبانية - تدرب على السلسلة كاملة",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث عن فعل بالإسبانية أو معناه بالعربية...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (filteredVerbs.isEmpty() && verbs.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لم يتم العثور على أفعال مطابقة لبحثك.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredVerbs) { verb ->
                    VerbCard(verb = verb, viewModel = viewModel)
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
                }
            }
        }
    }
    }
    if (showAddDialog) {
        AddVerbDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { verbsText ->
                viewModel.generateAndAddVerbsBatch(verbsText)
                showAddDialog = false
            }
        )
    }

    if (isGenerating) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss */ },
            title = { Text("جاري معالجة الأفعال...") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    CircularProgressIndicator(color = DuoBlue)
                    Text(progressMsg ?: "جاري التحميل...", textAlign = TextAlign.Center)
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun AddVerbDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var verbsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مجموعة أفعال (AI)") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "قم بنسخ ولصق مجموعة من الأفعال هنا (ضع كل فعل في سطر، أو افصل بينها بفاصلة). سيقوم الذكاء الاصطناعي بجلب معانيها، وتصريفاتها، وتوليد أمثلة لها تلقائياً.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = verbsText,
                    onValueChange = { verbsText = it },
                    label = { Text("الأفعال الإسبانية") },
                    placeholder = { Text("مثال:\ncomer\nhablar\nvivir") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    singleLine = false
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (verbsText.isNotBlank()) {
                        onConfirm(verbsText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)
            ) {
                Text("جلب وإضافة", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun VerbCard(verb: Verb, viewModel: LessonViewModel) {
    val phrase = "${verb.infinitive} ${verb.past} ${verb.present} ${verb.future}"
    val phraseWithPauses = "${verb.infinitive}. ${verb.past}. ${verb.present}. ${verb.future}"
    
    var isRecording by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf<Int?>(null) }
    var partialText by remember { mutableStateOf("") }
    
    val partialTextFlow by viewModel.sttManager.partialText.collectAsState()
    
    LaunchedEffect(isRecording) {
        if (isRecording) {
            partialText = partialTextFlow
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${verb.id}",
                    color = DuoBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = verb.meaning,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { VerbColumn(verb.infinitive, verb.meaning, Color(0xFF29B6F6)) }
                item { VerbColumn(verb.past, verb.past_meaning, Color(0xFFFFCA28)) }
                item { VerbColumn(verb.present, verb.present_meaning, Color(0xFF66BB6A)) }
                item { VerbColumn(verb.future, verb.future_meaning, Color(0xFFEF5350)) }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.ttsManager.speak(phraseWithPauses, "es", 0.9f)
                    },
                    modifier = Modifier.background(DuoBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Rounded.VolumeUp, contentDescription = "استمع", tint = DuoBlue)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(
                    onClick = {
                        if (isRecording) {
                            viewModel.sttManager.stopListening()
                            isRecording = false
                        } else {
                            score = null
                            isRecording = true
                            viewModel.sttManager.startListening("es") { resultText ->
                                val accuracy = SpeechAnalyzer.calculateAccuracy(phrase, resultText)
                                score = accuracy.toInt()
                                isRecording = false
                            }
                        }
                    },
                    modifier = Modifier.background(if (isRecording) Color.Red.copy(alpha = 0.1f) else DuoBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        Icons.Rounded.Mic, 
                        contentDescription = "تحدث", 
                        tint = if (isRecording) Color.Red else DuoBlue
                    )
                }
                
                if (score != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "$score%",
                        fontWeight = FontWeight.Bold,
                        color = when {
                            score!! >= 80 -> Color(0xFF4CAF50)
                            score!! >= 50 -> Color(0xFFFF9800)
                            else -> Color.Red
                        },
                        fontSize = 18.sp
                    )
                }
            }
            
            if (isRecording && partialTextFlow.isNotBlank()) {
                Text(
                    text = partialTextFlow,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun VerbColumn(word: String, meaning: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = word, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color, maxLines = 1)
        Text(text = meaning, fontSize = 13.sp, color = color, maxLines = 1)
    }
}
