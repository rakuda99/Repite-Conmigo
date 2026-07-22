package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.GTranslate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.repite.conmigo.logic.AuthService
import androidx.compose.ui.res.stringResource
import com.repite.conmigo.R
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoRed
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LessonViewModel,
    authService: AuthService,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onAdminClick: () -> Unit
) {
    val accuracyThreshold by viewModel.accuracyThreshold.collectAsState()
    val learningLang by viewModel.learningLanguage.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
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
                .verticalScroll(scrollState)
        ) {
            // UI LANGUAGE SELECTOR (Per-App Language)
            Text(stringResource(R.string.ui_language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            val currentLocales = AppCompatDelegate.getApplicationLocales()
            val currentLang = if (currentLocales.isEmpty) "auto" else currentLocales.get(0)?.language ?: "en"
            
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                FilterChip(
                    selected = currentLang == "ar",
                    onClick = { 
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("ar")
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        // Update profile in background
                        scope.launch {
                            authService.getUserProfile()?.let { 
                                authService.saveUserProfile(it.copy(motherTongue = "ar"))
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.lang_arabic)) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = currentLang == "es" || currentLang == "auto",
                    onClick = { 
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("es")
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        // Update profile in background
                        scope.launch {
                            authService.getUserProfile()?.let { 
                                authService.saveUserProfile(it.copy(motherTongue = "es"))
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.lang_spanish)) }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(stringResource(R.string.learning_language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            Row(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("es").forEach { lang ->
                    FilterChip(
                        selected = learningLang == lang,
                        onClick = { viewModel.setLanguage(lang) },
                        label = { Text(lang.uppercase()) }
                    )
                }
            }
            
            // NATIVE LANGUAGE (Translation target)
            Text("Native Language (Translation)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            val nativeLang by viewModel.nativeLanguage.collectAsState()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ar").forEach { lang ->
                    FilterChip(
                        selected = nativeLang == lang,
                        onClick = { viewModel.setNativeLanguage(lang) },
                        label = { Text(lang.uppercase()) }
                    )
                }
            }

            Divider(color = DuoGreen, thickness = 2.dp, modifier = Modifier.padding(vertical = 16.dp))

            // CLOUD BACKUP & RESTORE
            Text("النسخ الاحتياطي السحابي (Cloud Backup) ☁️", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            Spacer(modifier = Modifier.height(8.dp))
            
            val syncKey by viewModel.cloudSyncKey.collectAsState()
            var localSyncKeyInput by remember(syncKey) { mutableStateOf(syncKey) }
            var syncStatusMessage by remember { mutableStateOf("") }
            var isSyncOperationLoading by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = localSyncKeyInput,
                onValueChange = { 
                    localSyncKeyInput = it
                    viewModel.setCloudSyncKey(it)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                placeholder = { Text("أدخل رمز المزامنة الخاص بك (مثال: sulai)") },
                label = { Text("رمز المزامنة الشخصي (Sync Key)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
            Text(
                text = "أدخل رمز المزامنة الخاص بك وسيتم حفظ ومزامنة بياناتك واستعادتها من السحابة بشكل تلقائي بالكامل دون الحاجة لأي تدخل منك. لا داعي للقلق بشأن ضياع تقدمك.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = DuoGreen, thickness = 2.dp, modifier = Modifier.padding(vertical = 16.dp))

            // AI SETTINGS
            Text("الذكاء الاصطناعي (AI Settings) 🤖", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            Spacer(modifier = Modifier.height(8.dp))
            
            val geminiKey by viewModel.geminiApiKey.collectAsState()
            var localGeminiKeyInput by remember(geminiKey) { mutableStateOf(geminiKey) }

            OutlinedTextField(
                value = localGeminiKeyInput,
                onValueChange = { 
                    localGeminiKeyInput = it
                    viewModel.setGeminiApiKey(it)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                placeholder = { Text("أدخل مفتاح Gemini API الخاص بك") },
                label = { Text("مفتاح Gemini API") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
            Text(
                text = "أدخل مفتاح الـ API الخاص بـ Gemini لتفعيل ميزة الذكاء الاصطناعي في التطبيق (مثل توليد الجمل للكلمات الصعبة تلقائياً).",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = DuoGreen, thickness = 2.dp, modifier = Modifier.padding(vertical = 16.dp))

            // LESSON & DATA MANAGEMENT
            Text("Lessons & Data Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DuoBlue)
            
            val uiState by viewModel.uiState.collectAsState()
            if (uiState.feedback.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = DuoBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        uiState.feedback,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DuoBlue,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { 
                        scope.launch { viewModel.clearAllSentences() }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete All 🗑️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { 
                        scope.launch {
                            viewModel.resetSeedingState()
                            viewModel.clearAllSentences()
                            viewModel.loadGlobalLessons(context)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset Defaults 🔄", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }


            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Voice Input Settings
            Text("إعدادات الإدخال الصوتي (Voice Input)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            val useSystemSpeech by viewModel.useSystemSpeechDialog.collectAsState()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("استخدام نافذة النظام للتعرف على الصوت", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("قم بتفعيل هذا الخيار إذا كان المايك لا يعمل أو يعلق في هاتفك.", fontSize = 11.sp, color = Color.Gray)
                }
                Switch(
                    checked = useSystemSpeech,
                    onCheckedChange = { viewModel.setUseSystemSpeechDialog(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = DuoGreen, checkedTrackColor = DuoGreen.copy(alpha = 0.5f))
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text(stringResource(R.string.accuracy_threshold, accuracyThreshold.toInt()), style = MaterialTheme.typography.titleMedium)
            Slider(
                value = accuracyThreshold,
                onValueChange = { viewModel.setThreshold(it) },
                valueRange = 0f..100f,
                steps = 10,
                colors = SliderDefaults.colors(thumbColor = DuoGreen, activeTrackColor = DuoGreen)
            )
            Text(
                stringResource(R.string.accuracy_description),
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            val audioRepetitions by viewModel.audioRepetitions.collectAsState()
            Text("عدد تكرارات نطق البرنامج للبطاقة: $audioRepetitions", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = audioRepetitions.toFloat(),
                onValueChange = { viewModel.updateAudioRepetitions(it.toInt()) },
                valueRange = 1f..5f,
                steps = 3,
                colors = SliderDefaults.colors(thumbColor = DuoGreen, activeTrackColor = DuoGreen)
            )
            Text(
                "حدد عدد المرات التي سيقوم فيها البرنامج بنطق الكلمة أو الجملة تلقائياً لمساعدتك على الاستماع وحفظها.",
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            val sessionSize by viewModel.sessionSize.collectAsState()
            Text("حجم جلسة الدراسة والاختبار: $sessionSize كلمات/جمل", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = sessionSize.toFloat(),
                onValueChange = { viewModel.setSessionSize(it.toInt()) },
                valueRange = 5f..100f,
                steps = 94, // 5 to 100 with steps of 1 (95 values)
                colors = SliderDefaults.colors(thumbColor = DuoGreen, activeTrackColor = DuoGreen)
            )
            Text(
                "التحكم في عدد الكلمات والجمل التي يتم تحميلها في جلسة الدراسة أو الاختبارات بالزيادة والنقصان.",
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            val matchingQuizSize by viewModel.matchingQuizSize.collectAsState()
            Text("عدد الكلمات في تمرين التوصيل: $matchingQuizSize كلمات", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = matchingQuizSize.toFloat(),
                onValueChange = { viewModel.setMatchingQuizSize(it.toInt()) },
                valueRange = 3f..10f,
                steps = 6, // 3 to 10 with steps of 1 (6 steps)
                colors = SliderDefaults.colors(thumbColor = DuoGreen, activeTrackColor = DuoGreen)
            )
            Text(
                "التحكم في عدد أزواج الكلمات التي تظهر في شاشة تمرين التوصيل (ماتشين كويز) بالزيادة والنقصان.",
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            if (authService.isAdmin) {
                Button(
                    onClick = onAdminClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.admin_dashboard), color = MaterialTheme.colorScheme.onTertiary)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("عن التطبيق (About)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "تم إنتاج هذا البرنامج بمساعدة الذكاء الاصطناعي، بواسطة الدكتور سليمان السحيم، تحت مظلة rakuda99.\n\nThis program was produced with the help of artificial intelligence, by Dr. Sulaiman Alsuhaim, under the umbrella of rakuda99.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "rakuda99@gmail.com",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = DuoBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
