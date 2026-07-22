package com.repite.conmigo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences")
data class Sentence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String = "",
    val translation: String = "",
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
    val localAudioPath: String? = null, // Path to the downloaded audio file
    val pronunciationScore: Float = 0f,
    val memorizationDifficulty: Int = 0 // 0: unrated, 1: Easy, 2: Medium, 3: Hard
)

data class LessonMetadata(
    val lesson_id: String = "",
    val title: String = "",
    val version: Int = 0,
    val target_lang: String = "",
    val source_lang: String = "",
    val url: String? = null,
    val difficulty: String? = null,
    val thumbnail_url: String? = null,
    val total_items: Int = 0
)

data class LessonContent(
    val id: String = "",
    val original_text: String = "",
    val translation: String = "",
    val audio_url: String = "",
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
    val motherTongue: String = "en",
    val targetLanguage: String = "es"
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

data class AnkiState(
    val currentDeck: String? = null,
    val cards: List<Sentence> = emptyList(),
    val currentIndex: Int = 0,
    val showAnswer: Boolean = false,
    val pronunciationScore: Float = 0f,
    val isRecording: Boolean = false,
    val feedback: String = "",
    val wrongWords: Set<String> = emptySet(),
    val lastTranscription: String = "",
    val testMode: String = "all", // "all", "difficult", "random"
    val swapLanguages: Boolean = false
)

fun determineContentType(text: String, category: String): String {
    if (category.startsWith("The ") || category.lowercase().contains("story") || category == "The cat who barked") {
        return "passage"
    }

    val wordCategories = setOf(
        "The Alphabet", "Numbers 1-100", "Colors", "Days & Months", "Family Members", 
        "Body Parts", "Clothes", "Animals", "House", "Food & Drink", "Adjectives", 
        "Verbs 1", "Verbs 2", "Architecture", "Art", "Technology", "Science", 
        "Environment", "Museum", "Cinema", "Music Theory", "Cleaning", "Gardening", 
        "Fishing", "Fashion", "Nature", "Time", "Weather", "Verbs",
        "School", "Transport", "Jobs", "Cooking", "Sports Events", "Olympic Games",
        "Library", "Mountain", "Park", "Pharmacy", "Philosophy", "Photography",
        "Politics", "Psychology", "Religion", "Traditions", "World Cup", "Beach",
        "Culture", "Emotions", "Fixing Things", "Holidays", "Space"
    )
    val sentenceCategories = setOf(
        "Greetings & Basics", "At the Airport", "At the Doctor", 
        "Bank Transaction", "Business Meeting", "Camping", "Car Rental", "Hotel Check-in", 
        "Job Interview", "Police Station", "Post Office", "Public Transport", 
        "Restaurant Reservation", "School Conversation",
        "Shopping for Clothes", "Social Media", "Supermarket"
    )

    // Sentence categories always produce sentences
    if (sentenceCategories.contains(category)) return "sentence"

    // Now check actual text content - text-based checks take priority over category
    val trimmedText = text.trim()
    val words = trimmedText.split(Regex("\\s+")).filter { it.isNotBlank() }
    
    if (trimmedText.contains("?") || trimmedText.contains("!") || trimmedText.contains("¿") || trimmedText.contains("¡")) {
        return "sentence"
    }

    // If text ends with a period and has multiple words, it's a sentence
    if (trimmedText.endsWith(".") && words.size >= 3) {
        return "sentence"
    }

    val spanishVerbs = setOf(
        // Ser
        "soy", "eres", "es", "somos", "son",
        // Estar
        "estoy", "estás", "está", "estamos", "están",
        // Tener
        "tengo", "tienes", "tiene", "tenemos", "tienen",
        // Ir
        "voy", "vas", "va", "vamos", "van",
        // Querer/Gustar/Amar
        "quiero", "quiere", "gusta", "gustan", "amo", "ama",
        // Comprar
        "compro", "compras", "compra", "compré", "compró", "compramos", "compré",
        // Leer
        "leo", "lees", "lee", "leemos", "leen", "leí",
        // Necesitar
        "necesito", "necesitas", "necesita", "necesitamos", "necesitan",
        // Llegar
        "llego", "llegas", "llega", "llegamos", "llegan", "llegará", "llegaré",
        // Vivir/Residir
        "vivo", "vives", "vive", "vivimos", "viven", "viví", "vivió", "vivir",
        // Dormir
        "duermo", "duermes", "duerme", "dormimos", "duermen",
        // Costar/Valer
        "cuesta", "cuestan", "costó", "costaron",
        // Hay
        "hay",
        // Hacer/Medir/Pesar
        "hace", "hago", "hacen", "mide", "miden", "pesa", "pesan",
        // Correr/Viajar
        "corre", "corrí", "corro", "viaja", "viajan", "viajé",
        // Recibir
        "recibo", "recibí", "recibió", "reciben",
        // Estaremos (futuro)
        "estaremos", "estaré", "estará", "estarán",
        // Gastré/Gasté
        "gasté", "gastó", "gastamos",
        // Dura/Duro
        "dura", "duran", "duro",
        // Preparó/Preparé
        "preparó", "preparé", "prepara",
        // Other common verbs
        "plantó", "construyó", "resolvió", "resolvé", "vendió",
        "cumple", "cumplí", "cumplió"
    )
    if (words.any { spanishVerbs.contains(it.lowercase().trimEnd('.', ',', ';', ':')) }) {
        return "sentence"
    }

    // Only now use category to default to "word" for word-category lessons
    if (wordCategories.contains(category)) return "word"
    
    if (words.size >= 3) {
        val firstWord = words.first().lowercase()
        val nounMarkers = setOf("el", "la", "los", "las", "un", "una", "unos", "unas", "del", "al")
        if (words.size == 3 && nounMarkers.contains(firstWord)) {
            return "word"
        }
        return "sentence"
    }
    
    return "word"
}
