package com.repite.conmigo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences")
data class Sentence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val translation: String,
    val targetLang: String = "es",
    val sourceLang: String = "en",
    val audioUrl: String? = null,
    val slowAudioUrl: String? = null,
    val phoneticHint: String? = null,
    val validationTags: String? = null, // Stored as comma-separated string
    val category: String = "General",
    val contentType: String = "sentence", // "word", "sentence", "passage"
    val imageUrl: String? = null,
    val externalId: String? = null, // For tracking ID from remote server
    val localAudioPath: String? = null // Path to the downloaded audio file
)

data class LessonMetadata(
    val lesson_id: String,
    val title: String,
    val version: Int,
    val target_lang: String,
    val source_lang: String,
    val url: String? = null,
    val difficulty: String? = null,
    val thumbnail_url: String? = null,
    val total_items: Int = 0
)

data class LessonContent(
    val id: String,
    val original_text: String,
    val translation: String,
    val audio_url: String,
    val slow_audio_url: String? = null,
    val phonetic_hint: String? = null,
    val validation_tags: List<String> = emptyList(),
    val type: String = "sentence"
)

data class GlobalLesson(
    val metadata: LessonMetadata,
    val content: List<LessonContent>
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

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val gender: String = "",
    val age: String = "",
    val country: String = "",
    val motherTongue: String = "",
    val targetLanguage: String = ""
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
    val lastTranscription: String = "",
    val isSyncing: Boolean = false
)
