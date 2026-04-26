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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var authService: AuthService
    // contentService is now managed within AppNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthService(this)
        // contentService will be initialized in AppNavigation to use the repository
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 100)
        }

        // Handle initial locale: Default to English ONLY if no locale is set at all.
        // This allows the user to switch to Arabic on the login screen and it will stay.
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
    
    val viewModel = remember { LessonViewModel(repository, ttsManager, translationManager, sttManager, audioRecorder, backupManager) }
    
    var remoteLessons by remember { mutableStateOf<List<Lesson>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        remoteLessons = contentService.getLessons()
        viewModel.loadGlobalLessons(context)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Force English for Auth Screens
    LaunchedEffect(currentRoute) {
        if (currentRoute in listOf("login", "register")) {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }
    
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(authService.currentUser) {
        if (authService.currentUser == null) {
            startDestination = "login"
        } else {
            val profile = authService.getUserProfile()
            if (profile != null) {
                startDestination = "home"
            } else {
                startDestination = "setup_profile"
            }
        }
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
            val bottomRoutes = listOf("home", "lessons_hub", "stats", "settings")
            val isAuthScreen = currentRoute in listOf("login", "register", "admin")
            if (currentRoute in bottomRoutes && !isAuthScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val labelStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.Home, contentDescription = stringResource(R.string.nav_home)) },
                        label = { Text(stringResource(R.string.nav_home), style = labelStyle) },
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { launchSingleTop = true; restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Rounded.MenuBook, contentDescription = stringResource(R.string.nav_lessons)) },
                        label = { Text(stringResource(R.string.nav_lessons), style = labelStyle) },
                        selected = currentRoute == "lessons_hub",
                        onClick = { navController.navigate("lessons_hub") { launchSingleTop = true; restoreState = true } }
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
            composable("login") {
                val scope = rememberCoroutineScope()
                LoginScreen(
                    authService = authService,
                    onLoginSuccess = { 
                        scope.launch {
                            val profile = authService.getUserProfile()
                            if (profile == null) {
                                navController.navigate("setup_profile") { popUpTo("login") { inclusive = true } }
                            } else {
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                            }
                        }
                    },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }
            composable("register") {
                RegisterScreen(
                    authService = authService,
                    onRegisterSuccess = { 
                        // New users always go to setup_profile
                        navController.navigate("setup_profile") { popUpTo("register") { inclusive = true } }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable("setup_profile") {
                SetupProfileScreen(
                    authService = authService,
                    onComplete = { navController.navigate("home") { popUpTo("setup_profile") { inclusive = true } } }
                )
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
                var hubLessons by remember { mutableStateOf<List<Lesson>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                
                LaunchedEffect(Unit) {
                    hubLessons = contentService.getLessons()
                    isLoading = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        if (isLoading && hubLessons.isEmpty()) {
                            item {
                               Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                   CircularProgressIndicator(color = DuoBlue)
                               }
                            }
                        } else if (hubLessons.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    Text("لا توجد دروس حالياً.\nاضغط على الزر لإضافة أول درس!", textAlign = TextAlign.Center, color = Color.Gray)
                                }
                            }
                        }
                        
                        items(hubLessons) { lesson ->
                            LessonItem(lesson) {
                               viewModel.setQuizMode(false)
                               viewModel.selectCategory(lesson.categoryId)
                               if (lesson.content.isNotEmpty()) {
                                   viewModel.selectContentType(lesson.content[0].contentType)
                               }
                               navController.navigate("learning")
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = { navController.navigate("add_lesson") },
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
                            "quiz_mcq_beginner" -> {
                                viewModel.setQuizMode(true); viewModel.selectContentType("word"); viewModel.selectCategory(null)
                                navController.navigate("mcq_quiz")
                            }
                            else -> { 
                                navController.navigate("learning") 
                            }
                        }
                    },
                    onAddLesson = { navController.navigate("add_lesson") }
                )
            }
            composable("true_false_quiz") {
                TrueFalseQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("fill_blank_quiz") {
                FillBlankQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("mcq_quiz") {
                MCQQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("hidden_audio_quiz") {
                HiddenAudioQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("matching_quiz") {
                MatchingQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("reorder_quiz") {
                ReorderQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("short_answer_quiz") {
                ShortAnswerQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable("reverse_short_answer_quiz") {
                ReverseShortAnswerQuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
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
            composable("add_lesson") {
                AddLessonScreen(
                    viewModel = viewModel, 
                    onBack = { navController.popBackStack() },
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
        }
    }
}
@Composable
fun LessonItem(lesson: Lesson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(text = lesson.icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = lesson.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = lesson.rawLevel, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.sentences_count, lesson.content.size), style = MaterialTheme.typography.bodySmall, color = DuoBlue)
        }
    }
}
