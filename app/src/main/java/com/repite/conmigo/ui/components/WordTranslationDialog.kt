package com.repite.conmigo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen

@Composable
fun WordTranslationDialog(
    word: String,
    translation: String,
    onDismiss: () -> Unit,
    onPlayTts: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = word,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = DuoGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = translation,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = DuoBlue
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onPlayTts,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🔊 Escuchar")
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
