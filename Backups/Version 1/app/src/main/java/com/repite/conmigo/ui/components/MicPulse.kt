package com.repite.conmigo.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.repite.conmigo.ui.theme.DuoBlue

@Composable
fun MicPulse(rmsDb: Float, isListening: Boolean) {
    if (!isListening) return
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f + (rmsDb / 15f).coerceIn(0f, 0.4f),
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier.size(110.dp).scale(scale).background(DuoBlue.copy(alpha = 0.2f), CircleShape)
    )
}
