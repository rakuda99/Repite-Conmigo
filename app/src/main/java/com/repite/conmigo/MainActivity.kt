package com.repite.conmigo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.navArgument
import androidx.navigation.compose.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.repite.conmigo.data.AppDatabase
import com.repite.conmigo.data.LessonRepository
import com.repite.conmigo.models.Lesson
import com.repite.conmigo.logic.*
import com.repite.conmigo.ui.screens.*
import com.repite.conmigo.ui.theme.RepiteConmigoTheme
import com.repite.conmigo.ui.theme.DuoBlue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences

class MainActivity : ComponentActivity() {
    private lateinit var authService: AuthService
    // contentService is now managed within AppNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthService(this)
        val sharedPreferences = getSharedPreferences("repite_prefs", Context.MODE_PRIVATE)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }

        // Force Wipe for v35 (Build 1200) to clear legacy Arabic/Categorization mess
        val lastWipe = sharedPreferences.getInt("last_force_wipe", 0)
        if (lastWipe < 1200) {
            val database = AppDatabase.getDatabase(this)
            val repo = LessonRepository(database.lessonDao())
            CoroutineScope(Dispatchers.IO).launch {
                repo.clearAll()
                sharedPreferences.edit()
                    .putInt("last_force_wipe", 1200)
                    .putString("native_lang", "en")
                    .putString("learning_lang", "es")
                    .apply()
            }
        }

        // Handle initial locale: Default to English ONLY if no locale is set at all.
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.isEmpty && authService.currentUser == null) {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(appLocale)
        }

        setContent {
            RepiteConmigoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(authService)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(authService: AuthService) {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { LessonRepository(database.lessonDao()) }
    val contentService = remember { ContentService(context, repository) }
    val ttsManager = remember { TTSManager(context) }
    val sttManager = remember { SpeechToTextManager(context) }
    val translationManager = remember { TranslationManager() }
    val audioRecorder = remember { AudioRecorder(context) }
    val backupManager = remember { BackupManager(context) }
    
    val sharedPreferences = remember { context.getSharedPreferences("repite_prefs", android.content.Context.MODE_PRIVATE) }
    val viewModel = remember { LessonViewModel(context, repository, ttsManager, translationManager, sttManager, audioRecorder, backupManager, sharedPreferences, authService) }

    fun startLesson(
        category: String?,
        contentType: String,
        isQuiz: Boolean = false,
        quizType: String = "",
        practiceMemorized: Boolean = false
    ) {
        viewModel.setQuizMode(isQuiz)
        viewModel.selectContentType(contentType)
        viewModel.selectCategory(category)
        if (isQuiz) {
            viewModel.setPracticeMemorizedMode(practiceMemorized)
            when (quizType) {
                "mcq" -> navController.navigate("mcq_quiz")
                "tf" -> navController.navigate("true_false_quiz")
                "fill_blank" -> navController.navigate("fill_blank_quiz")
                "reorder" -> navController.navigate("reorder_quiz")
                "matching" -> navController.navigate("matching_quiz")
                "short_answer" -> navController.navigate("short_answer_quiz")
                "reverse_short_answer" -> navController.navigate("reverse_short_answer_quiz")
            }
        } else {
            navController.navigate("learning")
        }
    }
    
    var remoteLessons by remember { mutableStateOf<List<Lesson>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        remoteLessons = contentService.getLessons()
        viewModel.loadGlobalLessons(context)
        viewModel.syncAll(context, isManualTrigger = false)
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutDown()
            sttManager.destroy()
            audioRecorder.release()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // No longer need to force English for auth screens
    
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val profile = authService.getUserProfile()
        startDestination = "anki_decks_words"
    }

    if (startDestination == null) {
        // Simple loading indicator while checking auth/profile
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator(color = com.repite.conmigo.ui.theme.DuoBlue)
        }
        return
    }

    Scaffold(
        bottomBar = {
            val bottomRoutes = listOf("anki_decks_words", "anki_decks_sentences", "anki_decks_stories", "verbs", "stats", "settings")
            val isAuthScreen = currentRoute == "admin"
            if (currentRoute in bottomRoutes && !isAuthScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val labelStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Translate, contentDescription = "الكلمات") },
                        label = { Text("الكلمات", style = labelStyle) },
                        selected = currentRoute == "anki_decks_words",
                        onClick = { navController.navigate("anki_decks_words") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Chat, contentDescription = "الجمل") },
                        label = { Text("الجمل", style = labelStyle) },
                        selected = currentRoute == "anki_decks_sentences",
                        onClick = { navController.navigate("anki_decks_sentences") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.AutoStories, contentDescription = "القصص") },
                        label = { Text("القصص", style = labelStyle) },
                        selected = currentRoute == "anki_decks_stories",
                        onClick = { navController.navigate("anki_decks_stories") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.List, contentDescription = "الأفعال") },
                        label = { Text("الأفعال", style = labelStyle) },
                        selected = currentRoute == "verbs",
                        onClick = { navController.navigate("verbs") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.AutoGraph, contentDescription = stringResource(R.string.nav_progress)) },
                        label = { Text(stringResource(R.string.nav_progress), style = labelStyle) },
                        selected = currentRoute == "stats",
                        onClick = { navController.navigate("stats") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.nav_settings)) },
                        label = { Text(stringResource(R.string.nav_settings), style = labelStyle) },
                        selected = currentRoute == "settings",
                        onClick = { navController.navigate("settings") { launchSingleTop = true; restoreState = true } }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = startDestination!!, modifier = Modifier.padding(innerPadding)) {
            composable("setup_profile") {
                SetupProfileScreen(
                    authService = authService,
                    onComplete = { navController.navigate("home") { popUpTo("setup_profile") { inclusive = true } } }
                )
            }
            composable("verbs") {
                VerbsScreen(viewModel = viewModel)
            }
            composable("admin") {
                AdminDashboardScreen(
                    contentService = contentService,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToBeginner = { navController.navigate("beginner_level") },
                    onNavigateToIntermediate = { navController.navigate("intermediate_level") },
                    onNavigateToAdvanced = { navController.navigate("advanced_level") },
                    onNavigateToMCQ = { startLesson(null, "word", isQuiz = true, quizType = "mcq") },
                    onNavigateToTrueFalse = { startLesson(null, "word", isQuiz = true, quizType = "tf") },
                    onNavigateToFillBlank = { startLesson(null, "sentence", isQuiz = true, quizType = "fill_blank") },
                    onNavigateToReorder = { startLesson(null, "sentence", isQuiz = true, quizType = "reorder") },
                    onNavigateToMatching = { startLesson(null, "word", isQuiz = true, quizType = "matching") },
                    onNavigateToShortAnswer = { startLesson(null, "sentence", isQuiz = true, quizType = "short_answer") },
                    onNavigateToReverseShortAnswer = { startLesson(null, "sentence", isQuiz = true, quizType = "reverse_short_answer", practiceMemorized = true) },
                    onNavigateToAnki = { navController.navigate("anki_decks_words") },
                    onNavigateToDeck = { deckName, contentType ->
                        navController.navigate("deck_words_planner/$deckName/$contentType")
                    }
                )
            }
            composable("lessons_hub") {
                val scope = rememberCoroutineScope()
                var hubLessons by remember { mutableStateOf<List<Lesson>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    hubLessons = contentService.getLessons()
                    isLoading = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        // NEW: GLOBAL REFRESH BUTTON FOR EVERYONE
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isLoading = true
                                            contentService.clearAllLocalData()
                                            viewModel.loadGlobalLessons(context) // Ensure DB is repopulated instantly
                                            kotlinx.coroutines.delay(500) // Give DB a moment to process inserts
                                            hubLessons = contentService.getLessons()
                                            isLoading = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue.copy(alpha = 0.1f)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Rounded.Refresh, contentDescription = "Sync", tint = DuoBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Update Lessons 🔄", color = DuoBlue, fontSize = 12.sp)
                                }
                            }
                        }

                        if (isLoading && hubLessons.isEmpty()) {
                            item {
                               Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                   CircularProgressIndicator(color = DuoBlue)
                               }
                            }
                        } else if (hubLessons.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Text("No lessons available yet.\nTap the + button to add your first lesson!", textAlign = TextAlign.Center, color = Color.Gray)
                                }
                            }
                        }
                        
                        items(hubLessons) { lesson ->
                            LessonItem(
                                lesson = lesson,
                                canDelete = true, // Enabled for everyone to allow easy cleanup
                                onDelete = {
                                    scope.launch {
                                        isLoading = true
                                        contentService.deleteLesson(lesson.id)
                                        hubLessons = contentService.getLessons()
                                        isLoading = false
                                    }
                                },
                                onClick = {
                                    viewModel.setQuizMode(false)
                                    viewModel.selectCategory(lesson.categoryId)
                                    if (lesson.content.isNotEmpty()) {
                                        viewModel.selectContentType(lesson.content[0].contentType)
                                    }
                                    navController.navigate("learning")
                                }
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = { navController.navigate("cloud_library") },
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.BottomEnd)
                            .padding(24.dp),
                        containerColor = DuoBlue,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add Lesson")
                    }
                }
            }
            composable("beginner_level") {
                BeginnerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToLesson = { lessonId -> 
                        when(lessonId) {
                            "Pingo: Alphabet" -> startLesson("The Alphabet", "word")
                            "Pingo: Numbers" -> startLesson("Numbers 1-100", "word")
                            "Pingo: Colors" -> startLesson("Colors", "word")
                            "quiz_mcq_beginner" -> startLesson(null, "word", isQuiz = true, quizType = "mcq", practiceMemorized = true)
                            "quiz_tf_beginner" -> startLesson(null, "word", isQuiz = true, quizType = "tf", practiceMemorized = true)
                            "quiz_fill_blank_beginner" -> startLesson(null, "sentence", isQuiz = true, quizType = "fill_blank", practiceMemorized = true)
                            else -> navController.navigate("learning")
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
                            "Pingo: Conversation" -> startLesson("Greetings & Basics", "sentence")
                            "Pingo: Time" -> startLesson("Time", "word")
                            "Pingo: Restaurant" -> startLesson("Restaurant Reservation", "sentence")
                            "Pingo: Directions" -> startLesson("Asking Directions", "sentence")
                            "quiz_mcq_intermediate" -> startLesson(null, "word", isQuiz = true, quizType = "mcq", practiceMemorized = true)
                            "quiz_tf_intermediate" -> startLesson(null, "word", isQuiz = true, quizType = "tf", practiceMemorized = true)
                            "quiz_fill_blank_intermediate" -> startLesson(null, "sentence", isQuiz = true, quizType = "fill_blank", practiceMemorized = true)
                            else -> navController.navigate("learning")
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
                        when(lessonId) {
                            "Pingo: Ser y Estar" -> startLesson("Ser vs Estar", "sentence")
                            "Pingo: Past Tense" -> startLesson("Verbs 2", "sentence")
                            "Pingo: Linkers" -> startLesson("Verbs 1", "word")
                            "quiz_mcq_advanced" -> startLesson(null, "word", isQuiz = true, quizType = "mcq", practiceMemorized = true)
                            "quiz_tf_advanced" -> startLesson(null, "word", isQuiz = true, quizType = "tf", practiceMemorized = true)
                            "quiz_fill_blank_advanced" -> startLesson(null, "sentence", isQuiz = true, quizType = "fill_blank", practiceMemorized = true)
                            else -> navController.navigate("learning")
                        }
                    },
                    onAddLesson = { navController.navigate("add_lesson") }
                )
            }
            composable("true_false_quiz") {
                TrueFalseQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("fill_blank_quiz") {
                FillBlankQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("mcq_quiz") {
                MCQQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("hidden_audio_quiz") {
                HiddenAudioQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("matching_quiz") {
                MatchingQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("reorder_quiz") {
                ReorderQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("short_answer_quiz") {
                ShortAnswerQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("reverse_short_answer_quiz") {
                ReverseShortAnswerQuizScreen(viewModel = viewModel, onBack = { viewModel.setQuizMode(false); navController.popBackStack() })
            }
            composable("stats") {
                StatsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("learning") {
                LearningScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    authService = authService,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onAdminClick = {
                        navController.navigate("admin")
                    }
                )
            }
            composable("cloud_library") {
                CloudLibraryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "add_lesson?category={category}",
                arguments = listOf(navArgument("category") { defaultValue = ""; type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                AddLessonScreen(
                    viewModel = viewModel, 
                    onBack = { navController.popBackStack() },
                    initialCategory = category,
                    onStartTraining = { category ->
                        viewModel.setQuizMode(false)
                        viewModel.selectCategory(category)
                        // Heuristic: if category contains letters or sounds, use word type
                        if (category.contains("حروف", true) || category.contains("letters", true)) {
                            viewModel.selectContentType("word")
                        } else {
                            viewModel.selectContentType("sentence")
                        }
                        navController.navigate("learning") {
                            popUpTo("add_lesson") { inclusive = true }
                        }
                    }
                )
            }
            composable("anki_decks_words") {
                AnkiDecksScreen(
                    viewModel = viewModel,
                    filterType = "word",
                    onBack = { navController.popBackStack() },
                    onNavigateToStudy = { navController.navigate("anki_study") },
                    onNavigateToAddLesson = { navController.navigate("add_lesson") },
                    onNavigateToAddLessonWithCategory = { category -> 
                        navController.navigate("add_lesson?category=$category") 
                    },
                    onNavigateToCloudLibrary = { navController.navigate("cloud_library") },
                    onNavigateToMatching = { navController.navigate("matching_quiz") },
                    onNavigateToMCQ = { navController.navigate("mcq_quiz") },
                    onNavigateToFillBlank = { navController.navigate("fill_blank_quiz") },
                    onNavigateToReorder = { navController.navigate("reorder_quiz") },
                    onNavigateToPlanner = { deck, type ->
                        navController.navigate("deck_words_planner/$deck/$type")
                    },
                    onNavigateToVerbs = { navController.navigate("verbs") }
                )
            }
            composable("anki_decks_sentences") {
                AnkiDecksScreen(
                    viewModel = viewModel,
                    filterType = "sentence",
                    onBack = { navController.popBackStack() },
                    onNavigateToStudy = { navController.navigate("anki_study") },
                    onNavigateToAddLesson = { navController.navigate("add_lesson") },
                    onNavigateToAddLessonWithCategory = { category -> 
                        navController.navigate("add_lesson?category=$category") 
                    },
                    onNavigateToCloudLibrary = { navController.navigate("cloud_library") },
                    onNavigateToMatching = { navController.navigate("matching_quiz") },
                    onNavigateToMCQ = { navController.navigate("mcq_quiz") },
                    onNavigateToFillBlank = { navController.navigate("fill_blank_quiz") },
                    onNavigateToReorder = { navController.navigate("reorder_quiz") },
                    onNavigateToPlanner = { deck, type ->
                        navController.navigate("deck_words_planner/$deck/$type")
                    },
                    onNavigateToVerbs = { navController.navigate("verbs") }
                )
            }
            composable("anki_decks_stories") {
                AnkiDecksScreen(
                    viewModel = viewModel,
                    filterType = "passage",
                    onBack = { navController.popBackStack() },
                    onNavigateToStudy = { navController.navigate("anki_study") },
                    onNavigateToAddLesson = { navController.navigate("add_lesson") },
                    onNavigateToAddLessonWithCategory = { category -> 
                        navController.navigate("add_lesson?category=$category") 
                    },
                    onNavigateToCloudLibrary = { navController.navigate("cloud_library") },
                    onNavigateToMatching = { navController.navigate("matching_quiz") },
                    onNavigateToMCQ = { navController.navigate("mcq_quiz") },
                    onNavigateToFillBlank = { navController.navigate("fill_blank_quiz") },
                    onNavigateToReorder = { navController.navigate("reorder_quiz") },
                    onNavigateToPlanner = { deck, type ->
                        navController.navigate("deck_words_planner/$deck/$type")
                    },
                    onNavigateToStoryReader = { deck ->
                        navController.navigate("story_reader/$deck")
                    },
                    onNavigateToVerbs = { navController.navigate("verbs") }
                )
            }
            composable(
                route = "story_reader/{deckName}",
                arguments = listOf(
                    navArgument("deckName") { type = androidx.navigation.NavType.StringType }
                )
            ) { backStackEntry ->
                val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
                StoryReaderScreen(
                    viewModel = viewModel,
                    deckName = deckName,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("anki_study") {
                AnkiStudyScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "deck_words_planner/{deckName}/{contentType}",
                arguments = listOf(
                    navArgument("deckName") { type = androidx.navigation.NavType.StringType },
                    navArgument("contentType") { type = androidx.navigation.NavType.StringType }
                )
            ) { backStackEntry ->
                val deckName = backStackEntry.arguments?.getString("deckName") ?: ""
                val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
                
                DeckWordsPlannerScreen(
                    viewModel = viewModel,
                    deckName = deckName,
                    contentType = contentType,
                    onBack = { navController.popBackStack() },
                    onNavigateToAnkiStudy = { navController.navigate("anki_study") },
                    onNavigateToMatching = { navController.navigate("matching_quiz") },
                    onNavigateToMCQ = { navController.navigate("mcq_quiz") },
                    onNavigateToPronunciation = { 
                        viewModel.setQuizMode(true)
                        viewModel.selectContentType(contentType)
                        viewModel.selectCategory(deckName)
                        viewModel.setPracticeMemorizedMode(true)
                        navController.navigate("reverse_short_answer_quiz")
                    }
                )
            }
        }
    }
}
@Composable
fun LessonItem(lesson: Lesson, canDelete: Boolean, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(text = lesson.icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = lesson.rawLevel, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(text = stringResource(R.string.sentences_count, lesson.content.size), style = MaterialTheme.typography.bodySmall, color = DuoBlue)
                }
            }
            
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }
}
