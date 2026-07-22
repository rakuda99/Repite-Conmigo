package com.repite.conmigo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.repite.conmigo.ui.theme.DuoRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckWordsPlannerScreen(
    viewModel: LessonViewModel,
    deckName: String,
    contentType: String,
    onBack: () -> Unit,
    onNavigateToAnkiStudy: () -> Unit,
    onNavigateToMatching: () -> Unit,
    onNavigateToMCQ: () -> Unit,
    onNavigateToPronunciation: () -> Unit
) {
    val plannerCards by viewModel.plannerCards.collectAsState()
    val remainingCards by viewModel.remainingDeckCards.collectAsState()
    val sessionSize by viewModel.sessionSize.collectAsState()
    val useOriginalOrder by viewModel.useOriginalOrder.collectAsState()
    val rangeStart by viewModel.rangeStart.collectAsState()
    val rangeEnd by viewModel.rangeEnd.collectAsState()
    val totalDeckCardsCount by viewModel.totalDeckCardsCount.collectAsState()

    var showStartSessionDialog by remember { mutableStateOf(false) }

    var showAddCardDialog by remember { mutableStateOf(false) }
    var addCardText by remember { mutableStateOf("") }
    var addCardTranslation by remember { mutableStateOf("") }
    var isBulkMode by remember { mutableStateOf(false) }

    var cardToEdit by remember { mutableStateOf<Sentence?>(null) }
    var editCardText by remember { mutableStateOf("") }
    var editCardTranslation by remember { mutableStateOf("") }

    var cardToDelete by remember { mutableStateOf<Sentence?>(null) }

    // Initialize planner when screen opens
    LaunchedEffect(deckName, contentType) {
        viewModel.initPlanner(deckName, contentType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "مخطط الجلسة اليومي 📋",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        addCardText = ""
                        addCardTranslation = ""
                        showAddCardDialog = true 
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add Card",
                            tint = DuoBlue
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
                    .padding(bottom = 80.dp)
            ) {
                // Header Card for session size adjustments
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DuoBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getArabicCategoryName(deckName),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = if (contentType == "word") "مجموعات الكلمات" else "مجموعات الجمل",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Selector for Order Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilterChip(
                                selected = !useOriginalOrder,
                                onClick = { viewModel.setUseOriginalOrder(false) },
                                label = { Text("الترتيب الذكي 🧠", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White.copy(alpha = 0.3f),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.Transparent,
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White.copy(alpha = 0.3f),
                                    selectedBorderColor = Color.White.copy(alpha = 0.5f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = useOriginalOrder,
                                onClick = { viewModel.setUseOriginalOrder(true) },
                                label = { Text("نطاق مخصص 🎯", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White.copy(alpha = 0.3f),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.Transparent,
                                    labelColor = Color.White.copy(alpha = 0.7f)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White.copy(alpha = 0.3f),
                                    selectedBorderColor = Color.White.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!useOriginalOrder) {
                            Text(
                                text = "عدد الكلمات في جلسة الحفظ اليومية:",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { 
                                        if (sessionSize > 3) {
                                            viewModel.adjustPlannerSize(sessionSize - 1)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease", tint = Color.White)
                                }

                                Text(
                                    text = "$sessionSize",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                IconButton(
                                    onClick = { 
                                        viewModel.adjustPlannerSize(sessionSize + 1)
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = "Increase", tint = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "اضغط على + أو - للتحكم بالزيادة والنقصان في عدد كلمات الجلسة.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            if (remainingCards.isNotEmpty()) {
                                Text(
                                    text = "متاح ${remainingCards.size} كلمة أخرى للتبديل في هذه المجموعة.",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "اختر نطاق الكلمات المراد دراسته:",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            var sliderPosition by remember(rangeStart, rangeEnd) {
                                mutableStateOf(rangeStart.toFloat()..rangeEnd.toFloat())
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "من الكلمة: ${sliderPosition.start.toInt()}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "إلى الكلمة: ${sliderPosition.endInclusive.toInt()}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            RangeSlider(
                                value = sliderPosition,
                                onValueChange = { range ->
                                    val startVal = range.start.toInt()
                                    val endVal = range.endInclusive.toInt()
                                    if (endVal - startVal >= 2) {
                                        sliderPosition = range
                                    } else {
                                        if (range.start.toInt() != sliderPosition.start.toInt()) {
                                            sliderPosition = range.start..kotlin.math.min(range.start + 2, totalDeckCardsCount.toFloat())
                                        } else {
                                            sliderPosition = kotlin.math.max(1f, range.endInclusive - 2)..range.endInclusive
                                        }
                                    }
                                },
                                onValueChangeFinished = {
                                    viewModel.setCustomRange(
                                        sliderPosition.start.toInt(),
                                        sliderPosition.endInclusive.toInt()
                                    )
                                },
                                valueRange = 1f..totalDeckCardsCount.toFloat().coerceAtLeast(3f),
                                steps = (totalDeckCardsCount - 2).coerceAtLeast(0),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                                    activeTickColor = Color.Transparent,
                                    inactiveTickColor = Color.Transparent
                                ),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "دراسة عدد ${rangeEnd - rangeStart + 1} كلمة في الترتيب الأصلي للبطاقات.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Subtitle
                Text(
                    text = "الكلمات المقترحة لدراستها اليوم (${plannerCards.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    textAlign = TextAlign.Right
                )

                // Words List
                if (plannerCards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DuoBlue)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        itemsIndexed(plannerCards) { index, card ->
                            PlannerWordCard(
                                index = index + 1,
                                card = card,
                                canSwap = remainingCards.isNotEmpty(),
                                onSpeak = { viewModel.speakText(card.text, card.targetLang) },
                                onSwap = { viewModel.swapPlannerCard(card.id) },
                                onMarkAsKnown = { viewModel.markPlannerCardAsKnown(card.id) },
                                onEdit = {
                                    editCardText = card.text
                                    editCardTranslation = card.translation
                                    cardToEdit = card
                                },
                                onDelete = {
                                    cardToDelete = card
                                }
                            )
                        }
                    }
                }
            }

            // Bottom Action Button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { showStartSessionDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ابدأ دراسة هذه القائمة الآن 🚀",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Start Session dialog containing choices
    if (showStartSessionDialog) {
        AlertDialog(
            onDismissRequest = { showStartSessionDialog = false },
            title = {
                Text(
                    text = "اختر طريقة الدراسة المفضلة",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option 1: Anki cards
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showStartSessionDialog = false
                                viewModel.startPlannedSession("all")
                                onNavigateToAnkiStudy()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DuoBlue.copy(alpha = 0.08f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Style, contentDescription = null, tint = DuoBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("بطاقات أنكي (Anki) 🎴", fontWeight = FontWeight.Bold)
                                Text("دراسة الكلمات بالتكرار المتباعد وتقييم نطقك.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Option 2: Matching Quiz (only for words)
                    if (contentType == "word") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showStartSessionDialog = false
                                    viewModel.startPlannedSession("all")
                                    onNavigateToMatching()
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DuoGreen.copy(alpha = 0.08f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Extension, contentDescription = null, tint = DuoGreen)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("لعبة مواءمة الكلمات 🧩", fontWeight = FontWeight.Bold)
                                    Text("طابق الكلمة الإسبانية بترجمتها العربية بسرعة.", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    // Option 3: MCQ Quiz
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showStartSessionDialog = false
                                viewModel.startPlannedSession("all")
                                onNavigateToMCQ()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DuoOrange.copy(alpha = 0.08f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = DuoOrange)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("اختبار خيارات (MCQ) 🎯", fontWeight = FontWeight.Bold)
                                Text("اختر الإجابة الصحيحة من الخيارات المتعددة.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    // Option 4: Pronunciation Test (Speaking Practice)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showStartSessionDialog = false
                                viewModel.startPlannedSession("all")
                                onNavigateToPronunciation()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6).copy(alpha = 0.08f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, tint = Color(0xFF8B5CF6))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("تدريب نطق الجمل والكلمات 🗣️", fontWeight = FontWeight.Bold)
                                Text("استمع للبرنامج وكرر بصوتك لتقييم النطق بدقة.", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStartSessionDialog = false }, modifier = Modifier.fillMaxWidth()) {
                    Text("إلغاء", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Dialog 1: Add Card Dialog
    if (showAddCardDialog) {
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isBulkMode) "إضافة جمل متعددة 📝" else "إضافة كارت جديد ➕",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Tab/Row selector
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F3F9), RoundedCornerShape(10.dp)).padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { isBulkMode = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isBulkMode) DuoBlue else Color.Transparent,
                                contentColor = if (!isBulkMode) Color.White else Color.Gray
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("كارت مفرد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { isBulkMode = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBulkMode) DuoBlue else Color.Transparent,
                                contentColor = if (isBulkMode) Color.White else Color.Gray
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("لصق متعدد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isBulkMode) {
                        Text(
                            text = "الصق الجمل أو الكلمات هنا (كل جملة في سطر). سيقوم التطبيق بترجمتها تلقائياً للعربية وحفظها في المجموعة.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = addCardText,
                            onValueChange = { addCardText = it },
                            placeholder = { Text("مثال:\nHola\n¿Cómo estás?\nBuenos días") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = addCardText,
                            onValueChange = { addCardText = it },
                            label = { Text("الكلمة / الجملة باللغة الأجنبية") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = addCardTranslation,
                            onValueChange = { addCardTranslation = it },
                            label = { Text("الترجمة بالعربية") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isBulkMode) {
                            if (addCardText.isNotBlank()) {
                                val currentSentence = plannerCards.firstOrNull()
                                val lang = currentSentence?.targetLang ?: viewModel.learningLanguage.value
                                viewModel.addCustomSentences(addCardText, lang, deckName)
                                showAddCardDialog = false
                            }
                        } else {
                            if (addCardText.isNotBlank() && addCardTranslation.isNotBlank()) {
                                viewModel.addCardToDeck(addCardText, addCardTranslation, deckName, contentType)
                                showAddCardDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                    enabled = if (isBulkMode) addCardText.isNotBlank() else (addCardText.isNotBlank() && addCardTranslation.isNotBlank())
                ) {
                    Text("حفظ وإضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog 2: Edit Card Dialog
    if (cardToEdit != null) {
        AlertDialog(
            onDismissRequest = { cardToEdit = null },
            title = { Text("تعديل الكارت ✏️", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editCardText,
                        onValueChange = { editCardText = it },
                        label = { Text("الكلمة / الجملة باللغة الأجنبية") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editCardTranslation,
                        onValueChange = { editCardTranslation = it },
                        label = { Text("الترجمة بالعربية") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editCardText.isNotBlank() && editCardTranslation.isNotBlank()) {
                            viewModel.editCard(cardToEdit!!, editCardText, editCardTranslation)
                            cardToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue)
                ) {
                    Text("حفظ التعديلات")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToEdit = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Dialog 3: Delete Card Dialog
    if (cardToDelete != null) {
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = { Text("حذف الكارت 🗑️", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف الكارت \"${cardToDelete!!.text}\"؟",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCard(cardToDelete!!)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun PlannerWordCard(
    index: Int,
    card: Sentence,
    canSwap: Boolean,
    onSpeak: () -> Unit,
    onSwap: () -> Unit,
    onMarkAsKnown: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isKnown = card.memorizationDifficulty == 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isKnown) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isKnown) DuoGreen.copy(alpha = 0.4f) else Color.LightGray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Index badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (isKnown) DuoGreen.copy(alpha = 0.2f) else DuoBlue.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isKnown) DuoGreen else DuoBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Words content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSpeak() }
                    ) {
                        Text(
                            text = card.text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isKnown) DuoGreen else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Rounded.VolumeUp,
                            contentDescription = "Speak",
                            tint = if (isKnown) DuoGreen else DuoBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = card.translation,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // If memorized already
                if (isKnown) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = "Known",
                        tint = DuoGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Edit and Delete buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = DuoBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
                
                // Right side: Known / Swap buttons (only if not known)
                if (!isKnown) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // "I Know it" Button
                        TextButton(
                            onClick = onMarkAsKnown,
                            colors = ButtonDefaults.textButtonColors(contentColor = DuoGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("أعرفها", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (canSwap) {
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            // "Swap" Button
                            TextButton(
                                onClick = onSwap,
                                colors = ButtonDefaults.textButtonColors(contentColor = DuoOrange),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Rounded.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تبديل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
            }
        }
    }
}
