package com.repite.conmigo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences")
data class Sentence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val language: String, // "es" or "en"
    val translation: String,
    val translationMapping: String? = null, // JSON or comma-separated index map: "0:0,1:2"
    val category: String = "General",
    val contentType: String = "sentence" // "word", "sentence", "passage"
)

@Entity(tableName = "learning_records")
data class LearningRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sentenceId: Int,
    val date: Long,
    val accuracy: Float,
    val transcription: String
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val streak: Int = 0,
    val lastActiveDate: Long = 0,
    val highestAccuracy: Float = 0f,
    val sessionCount: Int = 0,
    val totalWordsLearned: Int = 0
)

data class LessonState(
    val sentences: List<Sentence> = emptyList(),
    val currentIndex: Int = 0,
    val currentXp: Int = 0,
    val isRecording: Boolean = false,
    val lastAccuracy: Float = 0f,
    val feedback: String = "",
    val completed: Boolean = false,
    val wrongWords: Set<String> = emptySet(),
    val highlightedIndex: Int = -1,
    val lastTranscription: String = ""
)
