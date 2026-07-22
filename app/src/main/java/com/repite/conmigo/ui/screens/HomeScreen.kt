package com.repite.conmigo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.rounded.ExitToApp
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.repite.conmigo.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.runtime.remember
import com.repite.conmigo.data.Sentence

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LessonViewModel,
    onNavigateToBeginner: () -> Unit,
    onNavigateToIntermediate: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
    onNavigateToMCQ: () -> Unit,
    onNavigateToTrueFalse: () -> Unit,
    onNavigateToFillBlank: () -> Unit,
    onNavigateToReorder: () -> Unit,
    onNavigateToMatching: () -> Unit,
    onNavigateToShortAnswer: () -> Unit,
    onNavigateToReverseShortAnswer: () -> Unit,
    onNavigateToAnki: () -> Unit,
    onNavigateToDeck: (String, String) -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val syncReport by viewModel.syncReport.collectAsState()
    val isExitSyncing by viewModel.isExitSyncing.collectAsState()
    val context = LocalContext.current

    val favoriteDecks by viewModel.favoriteDecks.collectAsState()
    val allSentencesList by viewModel.allSentences.collectAsState(emptyList())

    val favoriteDecksInfo = remember(favoriteDecks, allSentencesList) {
        if (favoriteDecks.isEmpty() || allSentencesList.isEmpty()) emptyList()
        else {
            val grouped = allSentencesList.groupBy { it.category }
            favoriteDecks.mapNotNull { deckName ->
                val categorySentences = grouped[deckName] ?: return@mapNotNull null
                val totalCount = categorySentences.size
                val contentType = categorySentences.groupBy { it.contentType }
                    .maxByOrNull { it.value.size }?.key ?: "word"
                Triple(deckName, totalCount, contentType)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FDF9))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = { if (!uiState.isSyncing) viewModel.syncAll(context, isManualTrigger = true) },
                    modifier = Modifier.background(DuoBlue.copy(alpha = 0.1f), CircleShape),
                    enabled = !uiState.isSyncing
                ) {
                    if (uiState.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = DuoBlue,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = DuoBlue)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Repite Conmigo",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DuoBlue
                    )
                    Text(
                        text = "طريقك لإتقان الإسبانية",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                
                IconButton(
                    onClick = { viewModel.syncAndExit(context, context as? android.app.Activity) },
                    modifier = Modifier.background(Color.Red.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ExitToApp,
                        contentDescription = "Exit",
                        tint = Color.Red
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(DuoGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐪", fontSize = 40.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(stringResource(R.string.accuracy_rate), color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(
                            "${userProgress?.highestAccuracy?.toInt() ?: 0}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = DuoGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.completed_lessons, userProgress?.sessionCount ?: 0),
                            fontSize = 14.sp,
                            color = DuoBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "المجموعات المفضلة ⭐",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = DuoOrange,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textAlign = TextAlign.Right
            )
        }

        if (favoriteDecksInfo.isNotEmpty()) {
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    items(favoriteDecksInfo) { (deckName, totalCount, contentType) ->
                        FavoriteDeckCard(
                            name = deckName,
                            count = totalCount,
                            contentType = contentType,
                            onClick = {
                                onNavigateToDeck(deckName, contentType)
                            }
                        )
                    }
                }
            }
        } else {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = DuoOrange.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DuoOrange.copy(alpha = 0.2f))
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⭐ لا توجد مجموعات مفضلة بعد. انقر على النجمة بجانب أي مجموعة في شاشة بطاقات Anki لإضافتها هنا للوصول السريع.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DuoOrange,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ماذا تريد أن تتعلم اليوم؟",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textAlign = TextAlign.Right
            )
        }

        item {
            LevelCard(
                title = "حفظ الكلمات والجمل",
                subtitle = "تعلم كلمات جديدة وتدرب على قراءتها",
                icon = "📖",
                color = DuoBlue,
                onClick = onNavigateToBeginner
            )
        }
        item {
            LevelCard(
                title = "بطاقات Anki المتباعدة",
                subtitle = "تدرب على الصعب وقيم نطقك وحفظك",
                icon = "🎴",
                color = Color(0xFF8E24AA),
                onClick = onNavigateToAnki
            )
        }
        item {
            LevelCard(
                title = "تدريب النطق",
                subtitle = "استمع وكرر لتحسين نطقك بالإسبانية",
                icon = "🗣️",
                color = DuoOrange,
                onClick = onNavigateToReverseShortAnswer
            )
        }
        item {
            LevelCard(
                title = "قصص قصيرة",
                subtitle = "اقرأ واستمتع بقصص قصيرة ممتعة",
                icon = "📚",
                color = DuoGreen,
                onClick = onNavigateToAdvanced
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (syncReport != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearSyncReport() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🔄", fontSize = 24.sp)
                    Text(
                        text = "تقرير مزامنة السحابة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DuoBlue
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = syncReport ?: "",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearSyncReport() },
                    colors = ButtonDefaults.buttonColors(containerColor = DuoBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("حسناً", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (isExitSyncing) {
        AlertDialog(
            onDismissRequest = {}, // Non-dismissible
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    CircularProgressIndicator(color = DuoBlue, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "جاري حفظ ومزامنة كلماتك سحابياً قبل الخروج...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الرجاء عدم إغلاق التطبيق ⏳",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {},
            dismissButton = null,
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun LevelCard(title: String, subtitle: String, icon: String, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 32.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
                Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun FavoriteDeckCard(
    name: String,
    count: Int,
    contentType: String,
    onClick: () -> Unit
) {
    val icon = when (contentType) {
        "word" -> "🎴"
        "sentence" -> "🗣️"
        "passage" -> "📚"
        else -> "📝"
    }
    
    val color = DuoOrange
    
    Surface(
        modifier = Modifier
            .width(180.dp)
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 20.sp)
                }
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Favorite",
                    tint = DuoOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Column {
                Text(
                    text = getArabicCategoryName(name),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "$count بطاقة",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
