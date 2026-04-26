package com.repite.conmigo.models

import com.repite.conmigo.data.Sentence

data class Lesson(
    val id: String = "",
    val title: String = "",
    val categoryId: String = "",
    val content: List<Sentence> = emptyList(),
    val type: String = "local", // "local", "asset", "remote"
    val rawLevel: String = "",
    val icon: String = "📚"
)
