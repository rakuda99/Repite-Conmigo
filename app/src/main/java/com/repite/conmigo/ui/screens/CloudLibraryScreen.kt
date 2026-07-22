package com.repite.conmigo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudLibraryScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val catalog by viewModel.cloudCatalog.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refreshCloudCatalog()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المكتبة السحابية 🌍", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshCloudCatalog() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        var searchQuery by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("الكل") }
        val categories = listOf("الكل", "دروسي المرفوعة", "منهج ديولينجو", "قصص ومحادثات", "مفردات عامة")

        val filteredCatalog = remember(catalog, searchQuery, selectedCategory) {
            val baseList = when (selectedCategory) {
                "دروسي المرفوعة" -> catalog.filter { it.title.any { c -> c in '\u0600'..'\u06FF' } }
                "منهج ديولينجو" -> catalog.filter { it.title.contains("Duo", ignoreCase = true) || it.title.contains("Duolingo", ignoreCase = true) }
                "قصص ومحادثات" -> catalog.filter { it.title.trim().firstOrNull()?.isDigit() == true }
                "مفردات عامة" -> catalog.filter { 
                    val title = it.title.trim()
                    !title.any { c -> c in '\u0600'..'\u06FF' } && 
                    !title.contains("Duo", ignoreCase = true) && 
                    !title.contains("Duolingo", ignoreCase = true) && 
                    title.firstOrNull()?.isDigit() != true
                }
                else -> catalog
            }

            if (searchQuery.isBlank()) {
                baseList
            } else {
                baseList.filter { lesson ->
                    val arabicTitle = getArabicCategoryName(lesson.title)
                    lesson.title.contains(searchQuery, ignoreCase = true) ||
                            arabicTitle.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7FDF9))
        ) {
            if (uiState.feedback.isNotEmpty()) {
                Text(
                    text = uiState.feedback,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    color = DuoBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("بحث عن مجموعة... 🔍") },
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

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DuoGreen,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            if (catalog.isEmpty() && uiState.isSyncing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DuoGreen)
                }
            } else if (filteredCatalog.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (catalog.isEmpty()) "لا توجد دروس متاحة في المكتبة السحابية حالياً." else "لا توجد نتائج تطابق بحثك أو تصنيفك 🔍",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCatalog) { lesson ->
                        CloudLessonCard(
                            title = getArabicCategoryName(lesson.title),
                            difficulty = lesson.difficulty ?: "Beginner",
                            itemsCount = lesson.total_items,
                            onDownload = { viewModel.importRemoteLesson(lesson.url ?: "", context) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloudLessonCard(
    title: String,
    difficulty: String,
    itemsCount: Int,
    onDownload: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DuoBlue)
                Text("${getArabicDifficulty(difficulty)} • $itemsCount جملة", fontSize = 14.sp, color = Color.Gray)
            }
            
            Button(
                onClick = onDownload,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("تنزيل", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

private fun getArabicDifficulty(difficulty: String): String {
    return when (difficulty.trim().lowercase()) {
        "beginner" -> "مبتدئ"
        "intermediate" -> "متوسط"
        "advanced" -> "متقدم"
        else -> difficulty
    }
}
