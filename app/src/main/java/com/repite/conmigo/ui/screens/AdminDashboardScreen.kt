package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.repite.conmigo.logic.ContentService
import com.repite.conmigo.models.Lesson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    contentService: ContentService,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var lessons by remember { mutableStateOf<List<Lesson>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        lessons = contentService.getLessons()
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لوحة التحكم (Admin)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(onClick = {
                        scope.launch {
                            isLoading = true
                            contentService.seedDatabase()
                            lessons = contentService.getLessons()
                            isLoading = false
                        }
                    }) {
                        Text("رفع البيانات المحلية")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Add new lesson */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(lessons) { lesson ->
                    ListItem(
                        headlineContent = { Text(lesson.title) },
                        supportingContent = { Text("${lesson.content.size} جمل") },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { /* TODO: Edit */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        contentService.deleteLesson(lesson.id)
                                        lessons = contentService.getLessons()
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}
