package com.repite.conmigo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Sync
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.repite.conmigo.R
import androidx.compose.foundation.clickable

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
    onNavigateToReverseShortAnswer: () -> Unit
) {
    val userProgress by viewModel.userProgress.collectAsState()

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
            // App Title & Sync Action
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Sync Button (Admin/Owner Tool)
                IconButton(
                    onClick = { viewModel.syncRemoteContent() },
                    modifier = Modifier.background(DuoBlue.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Sync", tint = DuoBlue)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DuoBlue
                    )
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                
                // Placeholder for balance/alignment
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        item {
            // Progress Card
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
                    // Camel Mascot Placeholder
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
            Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.quick_challenge),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                textAlign = TextAlign.Right
            )
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToMCQ() },
                shape = RoundedCornerShape(24.dp),
                color = DuoBlue,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.mcq_quiz_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.mcq_quiz_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToTrueFalse() },
                shape = RoundedCornerShape(24.dp),
                color = DuoOrange,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚖️", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.tf_quiz_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.tf_quiz_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToFillBlank() },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFA100FF), // DuoPurple
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✍️", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.fill_blank_quiz_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.fill_blank_quiz_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToReorder() },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1CB0F6), // Light Blue
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧩", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.reorder_quiz_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.reorder_quiz_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToMatching() },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFCE82FF), // Pink/Purple
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔗", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.matching_quiz_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.matching_quiz_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToShortAnswer() },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFFF4B4B), // DuoRed / Vibrant Red
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📝", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.writing_quiz_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.writing_quiz_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onNavigateToReverseShortAnswer() },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1B365D), // Dark Premium Blue
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🗣️", fontSize = 34.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(stringResource(R.string.translate_to_spanish_title), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text(stringResource(R.string.translate_to_spanish_desc), fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level Section
            Text(
                text = stringResource(R.string.choose_level),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Right
            )
        }

        item {
            LevelCard(
                title = stringResource(R.string.beginner_level_title),
                subtitle = stringResource(R.string.beginner_level_desc),
                icon = "📗",
                color = DuoGreen,
                onClick = onNavigateToBeginner
            )
        }
        item {
            LevelCard(
                title = stringResource(R.string.intermediate_level_title),
                subtitle = stringResource(R.string.intermediate_level_desc),
                icon = "📘",
                color = DuoBlue,
                onClick = onNavigateToIntermediate
            )
        }
        item {
            LevelCard(
                title = stringResource(R.string.advanced_title),
                subtitle = stringResource(R.string.advanced_level_desc),
                icon = "📙",
                color = DuoOrange,
                onClick = onNavigateToAdvanced
            )
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LevelCard(title: String, subtitle: String, icon: String, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
                Text(subtitle, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}
