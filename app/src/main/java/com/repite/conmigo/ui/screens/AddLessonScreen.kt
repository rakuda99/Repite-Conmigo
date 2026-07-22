package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.FileUpload
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Assignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit,
    onStartTraining: (String) -> Unit,
    initialCategory: String = ""
) {
    var titleInput by remember { mutableStateOf(initialCategory) }
    var textInput by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("es") }
    var explicitContentType by remember { mutableStateOf<String?>("word") }
    val context = androidx.compose.ui.platform.LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val fileContent = inputStream.bufferedReader().use { reader -> reader.readText() }
                    textInput = fileContent
                    if (titleInput.isBlank()) {
                        titleInput = "درس مستورد"
                    }
                }
            } catch (e: Exception) {
                // Ignore or handle
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إضافة جمل للتدريب", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            Text("عنوان المجموعة (مثلاً: درس السفر، مقابلة عمل):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = titleInput,
                onValueChange = { titleInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("أدخل عنواناً...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("اللغة الهدف:", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedLang == "es",
                    onClick = { selectedLang = "es" },
                    label = { Text("الإسبانية") }
                )
                FilterChip(
                    selected = selectedLang == "en",
                    onClick = { selectedLang = "en" },
                    label = { Text("الإنجليزية") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("نوع المحتوى:", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = explicitContentType == "word",
                    onClick = { explicitContentType = "word" },
                    label = { Text("كلمات (Words)") }
                )
                FilterChip(
                    selected = explicitContentType == "sentence",
                    onClick = { explicitContentType = "sentence" },
                    label = { Text("جمل (Sentences)") }
                )
                FilterChip(
                    selected = explicitContentType == null,
                    onClick = { explicitContentType = null },
                    label = { Text("تلقائي") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("أدخل الجمل أو الكلمات (كل جملة في سطر):", style = MaterialTheme.typography.titleMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button to upload from file (.txt)
                Button(
                    onClick = { filePickerLauncher.launch("text/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue.copy(alpha = 0.1f)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.FileUpload, contentDescription = null, tint = DuoBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحميل ملف (.txt)", color = DuoBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                
                // Button to paste text from clipboard
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                Button(
                    onClick = {
                        clipboardManager.getText()?.text?.let { pastedText ->
                            textInput = pastedText
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue.copy(alpha = 0.1f)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.Assignment, contentDescription = null, tint = DuoBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("لصق النص", color = DuoBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = { Text("مثال:\nHola amigo\n¿Cómo estás?") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val isAnkiMultiDeck = textInput.contains("\t") && 
                (textInput.contains("#separator:tab") || textInput.split("\n").any { it.split("\t").size >= 5 })

            Button(
                onClick = {
                    if (textInput.isNotBlank()) {
                        val finalTitle = if (titleInput.isBlank()) "مجموعة مستوردة" else titleInput
                        viewModel.addCustomSentences(textInput, selectedLang, finalTitle, explicitContentType)
                        if (isAnkiMultiDeck) {
                            onBack()
                        } else {
                            onStartTraining(finalTitle)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                enabled = textInput.isNotBlank() && (titleInput.isNotBlank() || isAnkiMultiDeck)
            ) {
                Text("حفظ والبدء في التدريب", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // --- Cloud Library Section ---
            Text(
                "المكتبة السحابية 🌍",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DuoBlue
            )
            Text(
                "اختر من دروسك المرفوعة سابقاً:",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            var cloudLessons by remember { mutableStateOf<List<com.repite.conmigo.data.LessonMetadata>>(emptyList()) }
            var isFetchingLibrary by remember { mutableStateOf(false) }
            val context = androidx.compose.ui.platform.LocalContext.current

            LaunchedEffect(Unit) {
                isFetchingLibrary = true
                try {
                    cloudLessons = viewModel.fetchCloudCatalog()
                } catch(e: Exception) {}
                isFetchingLibrary = false
            }

            if (isFetchingLibrary) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DuoBlue)
                }
            } else if (cloudLessons.isEmpty()) {
                Text("لا توجد دروس في المكتبة حالياً.", fontSize = 12.sp, color = Color.LightGray)
            } else {
                cloudLessons.forEach { meta ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { 
                            meta.url?.let { viewModel.importRemoteLesson(it, context) }
                                onStartTraining(meta.title)
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = DuoBlue.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, DuoBlue.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.CloudDownload, contentDescription = null, tint = DuoBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(meta.title, fontWeight = FontWeight.Bold)
                                Text("اللغة: ${meta.target_lang}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // --- Manual URL Import Fallback ---
            var urlInput by remember { mutableStateOf("") }

            Text("أو استيراد يدوي برابط مباشر (JSON):", style = MaterialTheme.typography.titleMedium, color = DuoGreen)
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://example.com/lesson.json") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.importRemoteLesson(urlInput, context)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                enabled = urlInput.startsWith("http")
            ) {
                Text("استيراد الدرس يدوياً", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = { viewModel.clearAllSentences() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("مسح جميع الجمل الموجودة", color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}
