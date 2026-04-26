package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.repite.conmigo.logic.AuthService
import androidx.compose.ui.res.stringResource
import com.repite.conmigo.R
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoRed
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.launch

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
                    selected = currentLang == "en" || currentLang == "auto",
                    onClick = { 
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        // Update profile in background
                        scope.launch {
                            authService.getUserProfile()?.let { 
                                authService.saveUserProfile(it.copy(motherTongue = "en"))
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.lang_english)) }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(stringResource(R.string.learning_language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            Row(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("es", "en", "fr", "de", "it").forEach { lang ->
                    FilterChip(
                        selected = learningLang == lang,
                        onClick = { viewModel.setLanguage(lang) },
                        label = { Text(lang.uppercase()) }
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Native Language (Translation)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DuoBlue)
            val nativeLang by viewModel.nativeLanguage.collectAsState()
            Row(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Scrollable row of targets
                listOf("ar", "en", "ru", "tr", "fr").forEach { lang ->
                    FilterChip(
                        selected = nativeLang == lang,
                        onClick = { 
                            viewModel.setNativeLanguage(lang)
                            // Skip profile update for brevity in this step, or include it
                        },
                        label = { Text(lang.uppercase()) }
                    )
                }
            }

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

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.lesson_management), color = DuoBlue, fontWeight = FontWeight.Bold)
            
            Button(
                onClick = { viewModel.syncRemoteContent() },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.FileUpload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.import_lessons))
            }
            
            OutlinedButton(
                onClick = { /* Implementation for JSON export */ },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.FileDownload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.export_lessons))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.external_memory), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            var backupMsg by remember { mutableStateOf("") }
            
            Button(
                onClick = { backupMsg = viewModel.backup() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.backup_progress))
            }
            
            OutlinedButton(
                onClick = { 
                    viewModel.restore()
                    backupMsg = "restore_success" 
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.restore_progress))
            }

            if (backupMsg.isNotEmpty()) {
                val displayMsg = if (backupMsg == "restore_success") stringResource(R.string.restore_success) else backupMsg
                Text(displayMsg, color = DuoGreen, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
            ) {
                Text(stringResource(R.string.close_settings))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        authService.signOut()
                        onLogout()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DuoRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, DuoRed)
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(stringResource(R.string.logout), fontWeight = FontWeight.Bold)
            }
        }
    }
}
