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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable

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
            // App Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Repite Conmigo",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DuoBlue,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "أتقن النطق بقوة الذكاء الاصطناعي",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
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
                        Text("معدل الدقة", color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(
                            "${userProgress?.highestAccuracy?.toInt() ?: 0}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = DuoGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "الدروس المكتملة: ${userProgress?.sessionCount ?: 0}",
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
                text = "تحدي سريع ⚡",
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
                        Text("اختبار المعاني (MCQ)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("اختبر حصيلتك اللغوية الآن!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
                        Text("صحيح أم خطأ؟", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("تحدي سرعة البديهة والترجمة!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
                        Text("أكمل الفراغ", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("اختبر قدرتك على بناء الجمل!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
                        Text("ترتيب الجملة", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("رتب الكلمات المبعثرة بشكل صحيح!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
                        Text("توصيل الكلمات", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("وصّل الكلمة بمعناها الصحيح!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
                        Text("اختبار الكتابة", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("اكتب الترجمة الصحيحة بنفسك!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
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
                        Text("ترجم إلى الإسبانية", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("اكتب الترجمة بالإسبانية مباشرة!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level Section
            Text(
                text = "اختر مستواك",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Right
            )
        }

        item {
            LevelCard(
                title = "مبتدئ",
                subtitle = "الحروف والأصوات",
                icon = "📗",
                color = DuoGreen,
                onClick = onNavigateToBeginner
            )
        }
        item {
            LevelCard(
                title = "متوسط",
                subtitle = "الكلمات والعبارات",
                icon = "📘",
                color = DuoBlue,
                onClick = onNavigateToIntermediate
            )
        }
        item {
            LevelCard(
                title = "متقدم",
                subtitle = "الجمل، القواعد، والنصوص",
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
