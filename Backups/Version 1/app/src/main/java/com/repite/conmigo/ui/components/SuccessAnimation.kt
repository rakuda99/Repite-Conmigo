package com.repite.conmigo.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import com.repite.conmigo.ui.theme.DuoGreen

@Composable
fun SuccessAnimation(isVisible: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val bounce by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .offset(y = bounce.dp)
                .size(140.dp)
                .background(DuoGreen.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🐪 🎉",
                fontSize = 50.sp
            )
        }
    }
}
