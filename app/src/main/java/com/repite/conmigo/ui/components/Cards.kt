package com.repite.conmigo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LessonCard(title: String, subtitle: String, icon: String, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = color,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun OutlinedActionCard(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
