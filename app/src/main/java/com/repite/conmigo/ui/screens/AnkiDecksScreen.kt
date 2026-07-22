package com.repite.conmigo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.data.Sentence
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange

val educationalOrder = listOf(
    "The Alphabet", "الحروف", "الأبجدية",
    "Numbers 1-100", "الأرقام",
    "Greetings & Basics", "الترحيب والأساسيات",
    "days_spanish_arabic", "months_spanish_arabic", "seasons_spanish_arabic", "Days & Months", "أيام الأسبوع والأشهر", "الوقتLos Tiempos",
    "Colors", "الألوان", "الاوانLos Colores",
    "Family Members", "العائلة والأقارب", "العائلة والأقارب La Familia y Los",
    "Body Parts", "أجزاء جسم الإنسان", "أعضاء الجسم",
    "House", "في المنزل En la casa", "المفردات اليومية والمنزلية",
    "Food & Drink", "في المطبخ En la cocina", "على المائدةSobre la mesa", "في النطعم",
    "Animals", "الحيوانات", "الحيوانات (animal)",
    "Clothes", "الملابس",
    "School", "المدرسة",
    "Nature", "الطبيعة",
    "Transport", "المواصلات", "7. المواصلات",
    "Jobs", "المهن",
    "Adjectives", "الصفات", "الصفاتCualidades",
    "Time", "الوقت",
    "Weather", "الطقس", "El clima",
    "Emotions", "المشاعر والصفات", "المشاعر",
    "Ser vs Estar",
    "أهم الأفعال الإسبانية", "الأفعال الإسبانية", "أفعال أساسية للتعلم", "40 Spanish verbs", "الافعال الاسبانية الاهم",
    "أدوات الاستفهام", "Spanish question words", "كلمات الاستفهام",
    "منهج تعلم الإسبانية في 30 يوماً",
    "منهج الأستاذة نورة",
    "أهم 100 كلمة إسبانية", "100 common Spanish vocabulary words",
    "أهم 200 كلمة إسبانية", "اهم 200كلمه",
    "أسئلة إسبانية شائعة", "سوال",
    "Spanish The 25 most important questions",
    "Hotel Check-in", "6. الفنادق والإقامة", "الفنادق والإقامة",
    "Asking Directions", "السؤال عن الاتجاهات",
    "At the Airport", "في المطار",
    "Shopping for Clothes", "التسوق لشراء الملابس",
    "At the Doctor", "عند الطبيب", "الصحة", "9. الصحة",
    "Job Interview", "مقابلة عمل",
    "Business Meeting", "اجتماع عمل",
    "Restaurant Reservation", "حجز مطعم",
    "Post Office", "مكتب البريد",
    "Bank Transaction", "معاملة مصرفية", "الخدمات المصرفية والعملات", "8. الخدمات المصرفية والعملات",
    "Police Station", "مركز الشرطة",
    "Public Transport", "وسائل النقل العام",
    "Car Rental", "تأجير السيارات",
    "Pharmacy", "الصيدلية",
    "Supermarket", "السوبرماركت", "Supermarket",
    "Cinema", "السينما",
    "Library", "المكتبة",
    "Park", "الحديقة",
    "Museum", "المتحف",
    "Beach", "الشاطئ",
    "Camping", "التخييم",
    "Fishing", "الصيد",
    "Mountain", "الجبل",
    "Cleaning", "التنظيف",
    "Gardening", "البستنة",
    "Cooking", "الطبخ",
    "Fixing Things", "إصلاح الأشياء",
    "Technology", "التكنولوجيا",
    "Social Media", "وسائل التواصل الاجتماعي",
    "Politics", "السياسة",
    "News", "الأخبار",
    "History", "التاريخ",
    "Science", "العلوم",
    "Space", "الفضاء",
    "Environment", "البيئة",
    "Music Theory", "نظرية الموسيقى",
    "Art", "الفن", "العمارة", "Architecture",
    "Psychology", "علم النفس",
    "Fashion", "الموضة",
    "Photography", "التصوير الفوتوغرافي",
    "Traditions", "التقاليد",
    "Holidays", "العطلات",
    "Philosophy", "الفلسفة",
    "Religion", "الدين",
    "Sports Events", "الأحداث الرياضية",
    "Culture", "الثقافة",
    "World Cup", "كأس العالم",
    "Olympic Games", "الألعاب الأولمبية",
    "Championships", "البطولات",
    "The Midnight Bakery", "The Detective Dog", "The Flying Bicycle", "The Lost Penguin", 
    "The Boy Who Stopped Time", "The Robot Gardener", "The Library of Whispers", 
    "The Village of Clouds", "The Singing Shoes", "The Island of Lost Socks", 
    "The Clockmaker's Secret", "The Tree with Golden Leaves", "The Dragon Who Loved Tea", 
    "The Astronaut Ant", "The Invisible Painter", "The Boy Who Talked to Clouds", 
    "The Underwater City", "The Library of Dreams", "The Magic Chef", "The cat who barked",
    "كلمات إسبانية ذات أصل عربي", "كلمات أصلها عربي",
    "الأمثال والحكم الإسبانية", "Arabic Spanish proverbs",
    "منهج Duolingo - جمل ومحادثات", "Duolingo", "Duolingo 1"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnkiDecksScreen(
    viewModel: LessonViewModel,
    filterType: String = "word",
    onBack: () -> Unit,
    onNavigateToStudy: () -> Unit,
    onNavigateToAddLesson: () -> Unit,
    onNavigateToAddLessonWithCategory: (String) -> Unit,
    onNavigateToCloudLibrary: () -> Unit,
    onNavigateToMatching: () -> Unit,
    onNavigateToMCQ: () -> Unit,
    onNavigateToFillBlank: () -> Unit,
    onNavigateToReorder: () -> Unit,
    onNavigateToPlanner: (String, String) -> Unit,
    onNavigateToStoryReader: ((String) -> Unit)? = null,
    onNavigateToVerbs: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("repite_prefs", android.content.Context.MODE_PRIVATE) }
    val orderList = remember {
        val saved = sharedPreferences.getString("anki_decks_order", "") ?: ""
        val list = saved.split(",").filter { it.isNotBlank() }
        val stateList = androidx.compose.runtime.mutableStateListOf<String>()
        stateList.addAll(list)
        stateList
    }
    var isReorderMode by remember { mutableStateOf(false) }

    val favoriteDecks by viewModel.favoriteDecks.collectAsState()

    val sentences by viewModel.allSentences.collectAsState()
    val records by viewModel.learningRecords.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val syncReport by viewModel.syncReport.collectAsState()
    val isGeneratingStory by viewModel.isGeneratingStory.collectAsState()
    val generatingExamplesProgress by viewModel.generatingExamplesProgress.collectAsState()
    
    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var newDeckName by remember { mutableStateOf("") }
    
    var selectedDeckForOptions by remember { mutableStateOf<String?>(null) }
    var showDeckOptionsSheet by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    // Group sentences by category (deck) and filter by contentType
    val decks = remember(sentences, records, orderList.toList()) {
        val recordIds = records.map { it.sentenceId }.toSet()
        val filteredSentences = sentences.filter { it.contentType == filterType }
        filteredSentences.groupBy { it.category }.map { (category, categorySentences) ->
            var newCount = 0
            var learnCount = 0
            var dueCount = 0
            
            categorySentences.forEach { sentence ->
                val hasPracticed = recordIds.contains(sentence.id)
                val isLowPron = sentence.pronunciationScore in 0.1f..69.9f
                val isDifficultMarked = sentence.memorizationDifficulty >= 2 // Medium or Hard
                
                if (!hasPracticed) {
                    newCount++
                } else if (isDifficultMarked || isLowPron) {
                    learnCount++
                } else {
                    dueCount++
                }
            }
            
            AnkiDeckInfo(
                name = category,
                newCount = newCount,
                learnCount = learnCount,
                dueCount = dueCount,
                totalCount = categorySentences.size
            )
        }.sortedWith { d1, d2 ->
            val index1 = orderList.indexOf(d1.name)
            val index2 = orderList.indexOf(d2.name)
            
            when {
                index1 != -1 && index2 != -1 -> index1.compareTo(index2)
                index1 != -1 -> -1
                index2 != -1 -> 1
                else -> {
                    val eduIndex1 = educationalOrder.indexOfFirst { it.equals(d1.name, ignoreCase = true) }
                    val eduIndex2 = educationalOrder.indexOfFirst { it.equals(d2.name, ignoreCase = true) }
                    
                    when {
                        eduIndex1 != -1 && eduIndex2 != -1 -> eduIndex1.compareTo(eduIndex2)
                        eduIndex1 != -1 -> -1
                        eduIndex2 != -1 -> 1
                        else -> d1.name.compareTo(d2.name)
                    }
                }
            }
        }
    }

    val filteredDecks = remember(decks, searchQuery) {
        if (searchQuery.isBlank()) {
            decks
        } else {
            val queryWords = searchQuery.trim().split(" ").filter { it.isNotBlank() }.map { cleanArabicWord(it) }
            decks.filter { deck ->
                val arabicName = getArabicCategoryName(deck.name)
                val normEng = deck.name.lowercase()
                val deckWords = arabicName.trim().split(" ").filter { it.isNotBlank() }.map { cleanArabicWord(it) }
                
                queryWords.all { qWord ->
                    normEng.contains(qWord) || deckWords.any { dWord -> dWord.contains(qWord) || qWord.contains(dWord) }
                }
            }
        }
    }

    val favoriteDecksList = remember(filteredDecks, favoriteDecks) {
        filteredDecks.filter { favoriteDecks.contains(it.name) }
    }
    val otherDecksList = remember(filteredDecks, favoriteDecks) {
        filteredDecks.filter { !favoriteDecks.contains(it.name) }
    }

    // Count global difficult cards
    val globalDifficultCount = remember(sentences, records) {
        val recordIds = records.map { it.sentenceId }.toSet()
        sentences.count { sentence ->
            val hasPracticed = recordIds.contains(sentence.id)
            val isLowPron = sentence.pronunciationScore in 0.1f..69.9f
            val isDifficultMarked = sentence.memorizationDifficulty >= 2
            hasPracticed && (isDifficultMarked || isLowPron)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val titleText = when (filterType) {
                        "word" -> "مجموعات الكلمات 📖"
                        "sentence" -> "مجموعات الجمل 💬"
                        "passage" -> "القصص القصيرة 📚"
                        else -> "بطاقات التكرار المتباعد"
                    }
                    Text(titleText, fontSize = 18.sp, fontWeight = FontWeight.Bold) 
                },
                navigationIcon = {
                    // Hide back button if it's one of the main bottom tabs, as they are root screens
                    val isBottomTab = filterType in listOf("word", "sentence", "passage")
                    if (!isBottomTab) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { if (!uiState.isSyncing) viewModel.syncAll(context, isManualTrigger = true) },
                        enabled = !uiState.isSyncing
                    ) {
                        if (uiState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = DuoBlue,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Sync,
                                contentDescription = "Sync",
                                tint = DuoBlue
                            )
                        }
                    }
                    IconButton(onClick = { isReorderMode = !isReorderMode }) {
                        Icon(
                            imageVector = if (isReorderMode) Icons.Rounded.Check else Icons.Rounded.Sort,
                            contentDescription = if (isReorderMode) "Done" else "Reorder",
                            tint = if (isReorderMode) DuoGreen else MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // Leave space for bottom buttons
            ) {
                // Header Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoBlue)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "مراجعة ذكية وتكرار متباعد",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        if (globalDifficultCount > 0) {
                            Button(
                                onClick = {
                                    viewModel.startAnkiSession(null, "difficult", filterType)
                                    onNavigateToStudy()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Rounded.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مراجعة الصعب ($globalDifficultCount)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("بحث عن مجموعة... 🔍", fontSize = 14.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E293B),
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = DuoGreen,
                            unfocusedBorderColor = Color.LightGray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        )
                    )

                    val selectedWordsDeck = decks.find { it.name == "الكلمات المختارة" }
                    val selectedWordsCount = selectedWordsDeck?.totalCount ?: 0

                    Button(
                        onClick = {
                            selectedDeckForOptions = "الكلمات المختارة"
                            showDeckOptionsSheet = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("الكلمات المختارة ($selectedWordsCount)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Decks List
                if (filteredDecks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎴", fontSize = 64.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                if (decks.isEmpty()) "لا توجد مجموعات بطاقات حالياً.\nقم بإنشاء مجموعة أو استيراد ملف للبدء!"
                                else "لا توجد نتائج تطابق بحثك 🔍",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (favoriteDecksList.isNotEmpty()) {
                            item {
                                Text(
                                    text = "المجموعات المفضلة ⭐",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DuoOrange,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                            items(favoriteDecksList) { deck ->
                                AnkiDeckRow(
                                    deck = deck,
                                    isFavorite = true,
                                    onToggleFavorite = {
                                        viewModel.toggleFavoriteDeck(deck.name)
                                    },
                                    isReorderMode = isReorderMode,
                                    onMoveUp = {
                                        val currentDecksNames = decks.map { it.name }
                                        if (orderList.isEmpty()) {
                                            orderList.addAll(currentDecksNames)
                                        }
                                        val index = orderList.indexOf(deck.name)
                                        if (index > 0) {
                                            val temp = orderList[index]
                                            orderList[index] = orderList[index - 1]
                                            orderList[index - 1] = temp
                                            sharedPreferences.edit().putString("anki_decks_order", orderList.joinToString(",")).apply()
                                        }
                                    },
                                    onMoveDown = {
                                        val currentDecksNames = decks.map { it.name }
                                        if (orderList.isEmpty()) {
                                            orderList.addAll(currentDecksNames)
                                        }
                                        val index = orderList.indexOf(deck.name)
                                        if (index != -1 && index < orderList.size - 1) {
                                            val temp = orderList[index]
                                            orderList[index] = orderList[index + 1]
                                            orderList[index + 1] = temp
                                            sharedPreferences.edit().putString("anki_decks_order", orderList.joinToString(",")).apply()
                                        }
                                    },
                                    onClick = {
                                        selectedDeckForOptions = deck.name
                                        showDeckOptionsSheet = true
                                    }
                                )
                            }
                            if (otherDecksList.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "المجموعات الأخرى 🎴",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        items(otherDecksList) { deck ->
                            AnkiDeckRow(
                                deck = deck,
                                isFavorite = false,
                                onToggleFavorite = {
                                    viewModel.toggleFavoriteDeck(deck.name)
                                },
                                isReorderMode = isReorderMode,
                                onMoveUp = {
                                    val currentDecksNames = decks.map { it.name }
                                    if (orderList.isEmpty()) {
                                        orderList.addAll(currentDecksNames)
                                    }
                                    val index = orderList.indexOf(deck.name)
                                    if (index > 0) {
                                        val temp = orderList[index]
                                        orderList[index] = orderList[index - 1]
                                        orderList[index - 1] = temp
                                        sharedPreferences.edit().putString("anki_decks_order", orderList.joinToString(",")).apply()
                                    }
                                },
                                onMoveDown = {
                                    val currentDecksNames = decks.map { it.name }
                                    if (orderList.isEmpty()) {
                                        orderList.addAll(currentDecksNames)
                                    }
                                    val index = orderList.indexOf(deck.name)
                                    if (index != -1 && index < orderList.size - 1) {
                                        val temp = orderList[index]
                                        orderList[index] = orderList[index + 1]
                                        orderList[index + 1] = temp
                                        sharedPreferences.edit().putString("anki_decks_order", orderList.joinToString(",")).apply()
                                    }
                                },
                                onClick = {
                                    selectedDeckForOptions = deck.name
                                    showDeckOptionsSheet = true
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Buttons (Anki Style)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToCloudLibrary() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("تصفح المشترك", color = DuoBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        Button(
                            onClick = { showCreateDeckDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("إنشاء مجموعة", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToAddLesson() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("استيراد ملف", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        Button(
                            onClick = { onNavigateToVerbs() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DuoOrange),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("الأفعال وتصريفاتها", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }
    }

    // Create Deck Dialog
    if (showCreateDeckDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDeckDialog = false },
            title = { Text("إنشاء مجموعة بطاقات جديدة", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("أدخل اسم المجموعة (مثال: أجزاء جسم الإنسان، كلمات السفر...):", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDeckName,
                        onValueChange = { newDeckName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("مثال: كلمات السفر") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDeckName.isNotBlank()) {
                            val name = newDeckName.trim()
                            showCreateDeckDialog = false
                            newDeckName = ""
                            onNavigateToAddLessonWithCategory(name)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
                ) {
                    Text("إنشاء وإضافة كلمات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDeckDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (syncReport != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearSyncReport() },
            title = { Text("تقرير المزامنة السحابية 🔄") },
            text = { Text(syncReport ?: "") },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearSyncReport() },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
                ) {
                    Text("حسناً")
                }
            }
        )
    }

    // Deck Options Bottom Sheet / Dialog
    if (showDeckOptionsSheet && selectedDeckForOptions != null) {
        AlertDialog(
            onDismissRequest = { showDeckOptionsSheet = false },
            title = { Text(getArabicCategoryName(selectedDeckForOptions!!), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isFavorite = selectedDeckForOptions != null && favoriteDecks.contains(selectedDeckForOptions!!)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleFavoriteDeck(selectedDeckForOptions!!)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isFavorite) DuoOrange.copy(alpha = 0.1f) else DuoBlue.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                contentDescription = null,
                                tint = if (isFavorite) DuoOrange else DuoBlue
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFavorite) DuoOrange else DuoBlue
                                )
                                Text(
                                    text = if (isFavorite) "إزالة هذه المجموعة من قائمة المفضلة." else "حفظ هذه المجموعة في قائمة المفضلة للوصول السريع.",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    if (selectedDeckForOptions != "الكلمات المختارة") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    onNavigateToPlanner(selectedDeckForOptions!!, filterType)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoBlue.copy(alpha = 0.15f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Assignment, contentDescription = null, tint = DuoBlue)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("تخطيط وتعديل كلمات الجلسة 📋", fontWeight = FontWeight.Bold, color = DuoBlue)
                                    Text("عرض القائمة، زيادة/نقصان عددها، وتبديل الكلمات.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    if (filterType == "passage") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    onNavigateToStoryReader?.invoke(selectedDeckForOptions!!)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoBlue.copy(alpha = 0.15f)),
                            border = BorderStroke(1.5.dp, DuoBlue)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Book, contentDescription = null, tint = DuoBlue, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("قارئ القصة المتكامل 📖", fontWeight = FontWeight.Bold, color = DuoBlue, fontSize = 16.sp)
                                    Text("قراءة القصة كاملة، التحكم بالسرعة، الاستماع وتكرار النطق خلفي.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDeckOptionsSheet = false
                                viewModel.startAnkiSession(selectedDeckForOptions, "all", filterType)
                                onNavigateToStudy()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DuoBlue.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = DuoBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("دراسة كامل المجموعة", fontWeight = FontWeight.Bold)
                                Text("دراسة كل بطاقات المجموعة بالترتيب الأساسي.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    if (selectedDeckForOptions != "الكلمات المختارة") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    viewModel.startAnkiSession(selectedDeckForOptions, "difficult", filterType)
                                    onNavigateToStudy()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoOrange.copy(alpha = 0.05f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Warning, contentDescription = null, tint = DuoOrange)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("الأشياء الصعبة فقط", fontWeight = FontWeight.Bold, color = DuoOrange)
                                    Text("دراسة عشوائية فقط للكلمات الصعبة أو ذات النطق السيء.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDeckOptionsSheet = false
                                viewModel.startAnkiSession(selectedDeckForOptions, "random", filterType)
                                onNavigateToStudy()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DuoGreen.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Casino, contentDescription = null, tint = DuoGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("اختبار عشوائي (Random)", fontWeight = FontWeight.Bold, color = DuoGreen)
                                Text("خلط بطاقات المجموعة كاملة ودراستها عشوائياً.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Interactive Games and Quizzes Divider & Title
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    Text(
                        text = "الألعاب والاختبارات التفاعلية 🎮",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    if (filterType == "word") {
                        // Word Matching Quiz
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    viewModel.selectCategory(selectedDeckForOptions)
                                    viewModel.selectContentType("word")
                                    viewModel.setQuizMode(true)
                                    viewModel.setPracticeMemorizedMode(false)
                                    onNavigateToMatching()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF6366F1).copy(alpha = 0.08f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Extension, contentDescription = null, tint = Color(0xFF6366F1))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("لعبة مواءمة الكلمات 🧩", fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                                    Text("طابق الكلمات الإسبانية مع ترجمتها العربية الصحيحة.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }

                        // MCQ Quiz
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    viewModel.selectCategory(selectedDeckForOptions)
                                    viewModel.selectContentType("word")
                                    viewModel.setQuizMode(true)
                                    viewModel.setPracticeMemorizedMode(false)
                                    onNavigateToMCQ()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0EA5E9).copy(alpha = 0.08f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF0EA5E9))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("اختبار اختيار من متعدد (MCQ) 🎯", fontWeight = FontWeight.Bold, color = Color(0xFF0EA5E9))
                                    Text("اختر الترجمة الصحيحة للكلمة المعروضة.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    } else {
                        // Fill-in-the-Blank Quiz
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    viewModel.selectCategory(selectedDeckForOptions)
                                    viewModel.selectContentType(filterType)
                                    viewModel.setQuizMode(true)
                                    viewModel.setPracticeMemorizedMode(false)
                                    onNavigateToFillBlank()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.08f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Edit, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("اختبار ملء الفراغ 📝", fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                                    Text("اكتب الكلمة الناقصة في الجملة لتكمل المعنى.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }

                        // Reorder Words Quiz
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeckOptionsSheet = false
                                    viewModel.selectCategory(selectedDeckForOptions)
                                    viewModel.selectContentType(filterType)
                                    viewModel.setQuizMode(true)
                                    viewModel.setPracticeMemorizedMode(false)
                                    onNavigateToReorder()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6).copy(alpha = 0.08f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Shuffle, contentDescription = null, tint = Color(0xFF8B5CF6))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("ترتيب الكلمات لتكوين جملة 🔀", fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                                    Text("رتب الكلمات المبعثرة للحصول على الجملة الصحيحة.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    // AI Generation Divider & Title
                    if (filterType == "word") {
                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                        Text(
                            text = "الذكاء الاصطناعي ✨",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isGeneratingStory && generatingExamplesProgress == null) {
                                    viewModel.generateExamplesForDeck(selectedDeckForOptions!!)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoBlue.copy(alpha = 0.05f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (generatingExamplesProgress != null) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DuoBlue, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = DuoBlue)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(generatingExamplesProgress ?: "توليد جمل لجميع الكلمات 🤖", fontWeight = FontWeight.Bold, color = DuoBlue)
                                    if (generatingExamplesProgress == null) {
                                        Text("توليد 5 أمثلة لكل كلمة وحفظها في قسم الجمل.", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isGeneratingStory && generatingExamplesProgress == null) {
                                    viewModel.generateStoryForDeck(selectedDeckForOptions!!)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoOrange.copy(alpha = 0.05f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (isGeneratingStory) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = DuoOrange, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Rounded.MenuBook, contentDescription = null, tint = DuoOrange)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(if (isGeneratingStory) "جاري تأليف القصة... ⏳" else "تأليف قصة من كلمات المجموعة 📖", fontWeight = FontWeight.Bold, color = DuoOrange)
                                    if (!isGeneratingStory) {
                                        Text("كتابة قصة قصيرة مفيدة تستخدم كل هذه الكلمات معاً.", fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    var isPublishing by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isPublishing) {
                                isPublishing = true
                                viewModel.publishDeckToCloud(selectedDeckForOptions!!) { resultMsg ->
                                    isPublishing = false
                                    showDeckOptionsSheet = false
                                    if (resultMsg == "نجاح") {
                                        android.widget.Toast.makeText(context, "تم نشر مجموعتك بنجاح في المتجر السحابي! 🌍", android.widget.Toast.LENGTH_LONG).show()
                                    } else {
                                        android.widget.Toast.makeText(context, resultMsg, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107).copy(alpha = 0.15f)),
                        border = BorderStroke(2.dp, Color(0xFFFFC107).copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (isPublishing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFFF57F17), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFF57F17))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("نشر في متجر المجموعات 🌍", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                                Text("شارك مجموعتك مع جميع مستخدمي التطبيق في المتجر السحابي!", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDeckOptionsSheet = false
                                viewModel.deleteLocalDeck(selectedDeckForOptions!!)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.05f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("حذف المجموعة من الجهاز", fontWeight = FontWeight.Bold, color = Color.Red)
                                Text("حذف الكلمات محلياً مع إبقائها آمنة في المكتبة السحابية.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDeckOptionsSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun AnkiDeckRow(
    deck: AnkiDeckInfo,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    isReorderMode: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(enabled = !isReorderMode) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getArabicCategoryName(deck.name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${deck.totalCount} بطاقة",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (isReorderMode) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onMoveUp,
                            modifier = Modifier
                                .size(36.dp)
                                .background(DuoBlue.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = "Move Up",
                                tint = DuoBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = onMoveDown,
                            modifier = Modifier
                                .size(36.dp)
                                .background(DuoBlue.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDownward,
                                contentDescription = "Move Down",
                                tint = DuoBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (isFavorite) DuoOrange else Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (!isReorderMode) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DuoBlue.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "جديد: ${deck.newCount}",
                            color = DuoBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Difficult / Learn
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DuoOrange.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "صعب: ${deck.learnCount}",
                            color = DuoOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Due / Review
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DuoGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "مراجعة: ${deck.dueCount}",
                            color = DuoGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

data class AnkiDeckInfo(
    val name: String,
    val newCount: Int,
    val learnCount: Int,
    val dueCount: Int,
    val totalCount: Int
)

fun getArabicCategoryName(category: String): String {
    return when (category.trim()) {
        "Adjectives" -> "الصفات"
        "Animals" -> "الحيوانات"
        "Architecture" -> "العمارة"
        "Art" -> "الفن"
        "Asking Directions" -> "السؤال عن الاتجاهات"
        "At the Airport" -> "في المطار"
        "At the Doctor" -> "عند الطبيب"
        "Bank Transaction" -> "معاملة بنكية"
        "Beach" -> "الشاطئ"
        "Body Parts" -> "أعضاء الجسم"
        "Business Meeting" -> "اجتماع عمل"
        "Camping" -> "التخييم"
        "Car Rental" -> "تأجير السيارات"
        "Championships" -> "البطولات"
        "Cinema" -> "السينما"
        "Cleaning" -> "التنظيف"
        "Clothes" -> "الملابس"
        "Colors" -> "الألوان"
        "Cooking" -> "الطبخ"
        "Culture" -> "الثقافة"
        "Days & Months" -> "الأيام والشهور"
        "Emotions" -> "المشاعر"
        "Environment" -> "البيئة"
        "Family Members" -> "أفراد العائلة"
        "Fashion" -> "الموضة"
        "Fishing" -> "صيد السمك"
        "Fixing Things" -> "إصلاح الأشياء"
        "Food & Drink" -> "الطعام والشراب"
        "Gardening" -> "البستنة"
        "Greetings & Basics" -> "التحية والأساسيات"
        "History" -> "التاريخ"
        "Holidays" -> "الأعياد"
        "Hotel Check-in" -> "تسجيل الدخول في الفندق"
        "House" -> "المنزل"
        "Job Interview" -> "مقابلة عمل"
        "Jobs" -> "الوظائف"
        "Library" -> "المكتبة"
        "Mountain" -> "الجبل"
        "Museum" -> "المتحف"
        "Music Theory" -> "نظرية الموسيقى"
        "Nature" -> "الطبيعة"
        "News" -> "الأخبار"
        "Numbers 1-100" -> "الأرقام 1-100"
        "Olympic Games" -> "الألعاب الأولمبية"
        "Park" -> "الحديقة"
        "Pharmacy" -> "الصيدلية"
        "Philosophy" -> "الفلسفة"
        "Photography" -> "التصوير"
        "Police Station" -> "مركز الشرطة"
        "Politics" -> "السياسة"
        "Post Office" -> "مكتب البريد"
        "Psychology" -> "علم النفس"
        "Public Transport" -> "النقل العام"
        "Religion" -> "الأديان"
        "Restaurant Reservation" -> "حجز مطعم"
        "School" -> "المدرسة"
        "Science" -> "العلوم"
        "Ser vs Estar" -> "فعل \"Ser\" و \"Estar\""
        "Shopping for Clothes" -> "شراء الملابس"
        "Social Media" -> "وسائل التواصل"
        "Space" -> "الفضاء"
        "Sports Events" -> "أحداث رياضية"
        "Supermarket" -> "السوبر ماركت"
        "Technology" -> "التكنولوجيا"
        "The Alphabet" -> "الحروف والنطق"
        "The Astronaut Ant" -> "النملة رائدة الفضاء"
        "The Boy Who Stopped Time" -> "الفتي الذي أوقف الزمن"
        "The Boy Who Talked to Clouds" -> "الفتي الذي تحدث مع الغيوم"
        "The Clockmaker's Secret" -> "سر صانع الساعات"
        "The Detective Dog" -> "الكلب المحقق"
        "The Dragon Who Loved Tea" -> "التنين الذي أحب الشاي"
        "The Flying Bicycle" -> "الدراجة الطائرة"
        "The Invisible Painter" -> "الرسام غير المرئي"
        "The Island of Lost Socks" -> "جزيرة الجوارب المفقودة"
        "The Library of Dreams" -> "مكتبة الأحلام"
        "The Library of Whispers" -> "مكتبة الهمسات"
        "The Lost Penguin" -> "البطريق التائه"
        "The Magic Chef" -> "الطباخ السحري"
        "The Midnight Bakery" -> "مخبز منتصف الليل"
        "The Robot Gardener" -> "البستاني الآلي"
        "The Singing Shoes" -> "الأحذية المغنية"
        "The Tree with Golden Leaves" -> "الشجرة ذات الأوراق الذهبية"
        "The Underwater City" -> "المدينة تحت الماء"
        "The Village of Clouds" -> "قرية الغيوم"
        "The cat who barked" -> "القط الذي ينبح"
        "Time" -> "الوقت"
        "Traditions" -> "التقاليد"
        "Transport" -> "المواصلات"
        "Verbs 1" -> "أفعال 1"
        "Verbs 2" -> "أفعال 2"
        "Weather" -> "الطقس"
        "World Cup" -> "كأس العالم"
        else -> category
    }
}

fun normalizeArabic(text: String): String {
    var str = text.lowercase().trim()
    str = str.replace("[أإآ]".toRegex(), "ا")
    str = str.replace("ة".toRegex(), "ه")
    str = str.replace("ى".toRegex(), "ي")
    str = str.replace("[\u064B-\u065F]".toRegex(), "")
    return str
}

fun cleanArabicWord(word: String): String {
    var w = normalizeArabic(word)
    if (w.length >= 4) {
        if (w.startsWith("ال")) {
            w = w.substring(2)
        } else if (w.startsWith("لل")) {
            w = w.substring(2)
        } else if (w.startsWith("ل") || w.startsWith("ب") || w.startsWith("و")) {
            w = w.substring(1)
        }
    }
    if (w.length >= 3 && (w.endsWith("ه") || w.endsWith("ة"))) {
        w = w.substring(0, w.length - 1)
    }
    return w
}

