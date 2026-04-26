package com.repite.conmigo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import com.repite.conmigo.ui.theme.DuoOrange
import com.repite.conmigo.ui.theme.DuoRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: LessonViewModel,
    onBack: () -> Unit
) {
    val progress by viewModel.userProgress.collectAsState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("إحصائياتك 📊", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main XP Circle
            Surface(
                modifier = Modifier.size(160.dp),
                shape = RoundedCornerShape(80.dp),
                color = DuoBlue.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(4.dp, DuoBlue)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("${progress?.xp ?: 0}", fontSize = 48.sp, fontWeight = FontWeight.Black, color = DuoBlue)
                    Text("XP Total", fontSize = 14.sp, color = DuoBlue.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Grid of Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBox(
                    label = "Acierto Máximo",
                    value = "${progress?.highestAccuracy?.toInt() ?: 0}%",
                    icon = Icons.Rounded.EmojiEvents,
                    color = DuoOrange,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Días Activos",
                    value = "${progress?.streak ?: 0} 🔥",
                    icon = Icons.Rounded.LocalFireDepartment,
                    color = DuoRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBox(
                    label = "Sesiones",
                    value = "${progress?.sessionCount ?: 0}",
                    icon = Icons.Rounded.History,
                    color = DuoGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = "Nivel",
                    value = "Elite",
                    icon = Icons.Rounded.WorkspacePremium,
                    color = DuoBlue,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "¡Sigue así! Estás mejorando cada día.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun StatBox(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.1f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
