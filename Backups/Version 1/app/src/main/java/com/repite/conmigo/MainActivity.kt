package com.repite.conmigo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import com.repite.conmigo.data.AppDatabase
import com.repite.conmigo.data.LessonRepository
import com.repite.conmigo.logic.TTSManager
import com.repite.conmigo.logic.SpeechToTextManager
import com.repite.conmigo.logic.TranslationManager
import com.repite.conmigo.logic.AudioRecorder
import com.repite.conmigo.ui.screens.*
import com.repite.conmigo.ui.theme.RepiteConmigoTheme

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request microphone permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }

        setContent {
            RepiteConmigoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Simple manual DI for demonstration
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { LessonRepository(database.lessonDao()) }
    val ttsManager = remember { TTSManager(context) }
    val sttManager = remember { SpeechToTextManager(context) }
    val translationManager = remember { TranslationManager() }
    val audioRecorder = remember { AudioRecorder(context) }
    val backupManager = remember { com.repite.conmigo.logic.BackupManager(context) }
    
    val viewModel = remember { LessonViewModel(repository, ttsManager, translationManager, sttManager, audioRecorder, backupManager) }

    LaunchedEffect(Unit) {
        viewModel.loadPingoDataIfRequested(context)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val bottomRoutes = listOf("home", "lessons_hub", "stats", "settings")
            if (currentRoute in bottomRoutes) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية") },
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.MenuBook, contentDescription = "الدروس") },
                        label = { Text("الدروس") },
                        selected = currentRoute == "lessons_hub",
                        onClick = { navController.navigate("lessons_hub") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.AutoGraph, contentDescription = "التقدم") },
                        label = { Text("التقدم") },
                        selected = currentRoute == "stats",
                        onClick = { navController.navigate("stats") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Settings, contentDescription = "الإعدادات") },
                        label = { Text("الإعدادات") },
                        selected = currentRoute == "settings",
                        onClick = { navController.navigate("settings") { launchSingleTop = true; restoreState = true } }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToBeginner = { navController.navigate("beginner_level") },
                    onNavigateToIntermediate = { navController.navigate("intermediate_level") },
                    onNavigateToAdvanced = { navController.navigate("advanced_level") },
                    onNavigateToMCQ = { 
                        viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                        navController.navigate("mcq_quiz") 
                    },
                    onNavigateToTrueFalse = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                        navController.navigate("true_false_quiz")
                    },
                    onNavigateToFillBlank = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("fill_blank_quiz")
                    },
                    onNavigateToReorder = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("reorder_quiz")
                    },
                    onNavigateToMatching = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                        navController.navigate("matching_quiz")
                    },
                    onNavigateToShortAnswer = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("short_answer_quiz")
                    },
                    onNavigateToReverseShortAnswer = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("reverse_short_answer_quiz")
                    }
                )
            }
            composable("lessons_hub") {
                // Temporary fallback or list of all custom lessons if they click "Lessons" nav
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToBeginner = { navController.navigate("beginner_level") },
                    onNavigateToIntermediate = { navController.navigate("intermediate_level") },
                    onNavigateToAdvanced = { navController.navigate("advanced_level") },
                    onNavigateToMCQ = { 
                        viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                        navController.navigate("mcq_quiz") 
                    },
                    onNavigateToTrueFalse = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                        navController.navigate("true_false_quiz")
                    },
                    onNavigateToFillBlank = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("fill_blank_quiz")
                    },
                    onNavigateToReorder = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("reorder_quiz")
                    },
                    onNavigateToMatching = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                        navController.navigate("matching_quiz")
                    },
                    onNavigateToShortAnswer = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("short_answer_quiz")
                    },
                    onNavigateToReverseShortAnswer = {
                        viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                        navController.navigate("reverse_short_answer_quiz")
                    }
                )
            }
            composable("beginner_level") {
                BeginnerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToLesson = { lessonId -> 
                        when(lessonId) {
                            "alphabet" -> { 
                                viewModel.setQuizMode(false); viewModel.selectContentType("word"); viewModel.selectCategory("Letters (الحروف)") 
                                navController.navigate("learning")    
                            }
                            "sounds" -> { 
                                viewModel.setQuizMode(false); viewModel.selectContentType("word"); viewModel.selectCategory("Sounds (الأصوات)") 
                                navController.navigate("learning")
                            }
                            "quiz_alphabet" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory("Letters (الحروف)")
                                navController.navigate("hidden_audio_quiz")
                            }
                            "quiz_sounds" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory("Sounds (الأصوات)")
                                navController.navigate("hidden_audio_quiz")
                            }
                            "quiz_random_beginner" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null) // all
                                navController.navigate("hidden_audio_quiz")
                            }
                            "quiz_common" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory("Pingo: Alphabet")
                                navController.navigate("hidden_audio_quiz")
                            }
                            "quiz_mcq_beginner" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("mcq_quiz")
                            }
                            "quiz_tf_beginner" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("true_false_quiz")
                            }
                            "quiz_fill_blank_beginner" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("fill_blank_quiz")
                            }
                            else -> { 
                                viewModel.setQuizMode(false); viewModel.selectContentType("word"); viewModel.selectCategory("Pingo: Alphabet") 
                                navController.navigate("learning") 
                            }
                        }
                    },
                    onAddLesson = { navController.navigate("add_lesson") }
                )
            }
            composable("intermediate_level") {
                IntermediateScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToLesson = { lessonId -> 
                        when(lessonId) {
                            "quiz_mcq_intermediate" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("mcq_quiz")
                            }
                            "quiz_tf_intermediate" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("true_false_quiz")
                            }
                            "quiz_fill_blank_intermediate" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("fill_blank_quiz")
                            }
                            "quiz_intermediate_all" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("hidden_audio_quiz")
                            }
                            "common_words" -> { viewModel.setQuizMode(false); viewModel.selectContentType("word"); viewModel.selectCategory("Pingo: Alphabet"); navController.navigate("learning") }
                            "irregular_words" -> { viewModel.setQuizMode(false); viewModel.selectContentType("word"); viewModel.selectCategory("Irregular Words"); navController.navigate("learning") }
                            else -> { viewModel.setQuizMode(false); viewModel.selectContentType("word"); viewModel.selectCategory("Pingo: Alphabet"); navController.navigate("learning") }
                        }
                    },
                    onAddLesson = { navController.navigate("add_lesson") }
                )
            }
            composable("advanced_level") {
                AdvancedScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToLesson = { lessonId -> 
                        val route = when(lessonId) {
                            "short_sentences" -> { viewModel.setQuizMode(false); viewModel.selectContentType("sentence"); viewModel.selectCategory("Pingo: Ser y Estar"); "learning" }
                            "intonation" -> { viewModel.setQuizMode(false); viewModel.selectContentType("sentence"); viewModel.selectCategory("Pingo: Verbos"); "learning" }
                            "long_texts" -> { viewModel.setQuizMode(false); viewModel.selectContentType("passage"); viewModel.selectCategory("Pingo: Advanced"); "learning" }
                            "quiz_long_speech" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("passage"); viewModel.selectCategory("Pingo: Advanced")
                                "hidden_audio_quiz"
                            }
                            "quiz_mcq_advanced" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                                "mcq_quiz"
                            }
                            "quiz_tf_advanced" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                                "true_false_quiz"
                            }
                            "quiz_fill_blank_advanced" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("sentence"); viewModel.selectCategory(null)
                                "fill_blank_quiz"
                            }
                            else -> { viewModel.setQuizMode(false); viewModel.selectContentType("sentence"); viewModel.selectCategory("Pingo: Ser y Estar"); "learning" }
                        }
                        navController.navigate(route) 
                    },
                    onAddLesson = { navController.navigate("add_lesson") }
                )
            }
            composable("true_false_quiz") {
                TrueFalseQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("fill_blank_quiz") {
                FillBlankQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("mcq_quiz") {
                MCQQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("hidden_audio_quiz") {
                HiddenAudioQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("matching_quiz") {
                MatchingQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("reorder_quiz") {
                ReorderQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("short_answer_quiz") {
                ShortAnswerQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("reverse_short_answer_quiz") {
                ReverseShortAnswerQuizScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("chat") {
                ChatScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("stats") {
                StatsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("learning") {
                LearningScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("settings") {
                SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("add_lesson") {
                AddLessonScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
