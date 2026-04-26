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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit,
    onStartTraining: (String) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("es") }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
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

            Spacer(modifier = Modifier.height(20.dp))

            Text("اختر اللغة التي تريد ممارستها:", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                FilterChip(
                    selected = selectedLang == "es",
                    onClick = { selectedLang = "es" },
                    label = { Text("الإسبانية") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = selectedLang == "en",
                    onClick = { selectedLang = "en" },
                    label = { Text("الإنجليزية") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("أدخل الجمل أو الكلمات (كل جملة في سطر):", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text("مثال:\nHola amigo\n¿Cómo estás?") },
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titleInput.isNotBlank() && textInput.isNotBlank()) {
                        viewModel.addCustomSentences(textInput, selectedLang, titleInput)
                        onStartTraining(titleInput)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                enabled = titleInput.isNotBlank() && textInput.isNotBlank()
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
