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
import com.repite.conmigo.ui.theme.DuoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
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
                        onBack()
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
