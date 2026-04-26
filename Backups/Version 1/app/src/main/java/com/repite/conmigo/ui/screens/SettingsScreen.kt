package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.repite.conmigo.ui.theme.DuoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val accuracyThreshold by viewModel.accuracyThreshold.collectAsState()
    val learningLang by viewModel.learningLanguage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
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
            Text("Idioma de aprendizaje", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                FilterChip(
                    selected = learningLang == "es",
                    onClick = { viewModel.setLanguage("es") },
                    label = { Text("Español") },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = learningLang == "en",
                    onClick = { viewModel.setLanguage("en") },
                    label = { Text("English") }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Umbral de precisión: ${accuracyThreshold.toInt()}%", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = accuracyThreshold,
                onValueChange = { viewModel.setThreshold(it) },
                valueRange = 0f..100f,
                steps = 10,
                colors = SliderDefaults.colors(thumbColor = DuoGreen, activeTrackColor = DuoGreen)
            )
            Text(
                "Cuanto más alto, más difícil será pasar a la siguiente frase.",
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("الذاكرة الخارجية والذكاء الذاتي 💾", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            var backupMsg by remember { mutableStateOf("") }
            
            Button(
                onClick = { backupMsg = viewModel.backup() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("عمل نسخة احتياطية للتقدم (ملف خارجي)")
            }
            
            OutlinedButton(
                onClick = { 
                    viewModel.restore()
                    backupMsg = "تمت استعادة البيانات بنجاح! 🔄"
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("استعادة التقدم من الملف")
            }

            if (backupMsg.isNotEmpty()) {
                Text(backupMsg, color = DuoGreen, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }

            OutlinedButton(
                onClick = { 
                    viewModel.clearAllSentences()
                    backupMsg = "تم مسح البيانات! أعد تشغيل التطبيق لتحميل الحروف والكلمات الجديدة. 🗑️"
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = androidx.compose.ui.graphics.Color.Red),
                border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Red)
            ) {
                Text("تحديث مسارات الدروس وحذف الكاش 🗑️")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
            ) {
                Text("إغـلاق الإعـدادات")
            }
        }
    }
}
