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
import androidx.compose.ui.res.stringResource
import com.repite.conmigo.R

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
                title = { Text(stringResource(R.string.stats_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
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
                    Text(stringResource(R.string.total_xp), fontSize = 14.sp, color = DuoBlue.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Grid of Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBox(
                    label = stringResource(R.string.highest_accuracy),
                    value = "${progress?.highestAccuracy?.toInt() ?: 0}%",
                    icon = Icons.Rounded.EmojiEvents,
                    color = DuoOrange,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = stringResource(R.string.active_days),
                    value = "${progress?.streak ?: 0} 🔥",
                    icon = Icons.Rounded.LocalFireDepartment,
                    color = DuoRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatBox(
                    label = stringResource(R.string.sessions),
                    value = "${progress?.sessionCount ?: 0}",
                    icon = Icons.Rounded.History,
                    color = DuoGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = stringResource(R.string.level_label),
                    value = stringResource(R.string.level_elite),
                    icon = Icons.Rounded.WorkspacePremium,
                    color = DuoBlue,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                stringResource(R.string.stats_motivation),
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
        modifier = modifier.heightIn(min = 125.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.1f)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text(
                text = label, 
                fontSize = 11.sp, 
                color = Color.Gray, 
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}
