package com.repite.conmigo.logic

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.repite.conmigo.models.Lesson
import com.repite.conmigo.data.LessonRepository
import com.repite.conmigo.data.Sentence
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.io.InputStreamReader

class ContentService(private val context: Context, private val repository: LessonRepository? = null) {
    private val db = FirebaseFirestore.getInstance()
    private val lessonsCollection = db.collection("lessons")

    suspend fun getLessons(): List<Lesson> {
        val allLessons = mutableListOf<Lesson>()
        
        // Get user's preferred language (mother tongue)
        val authService = AuthService(context)
        val userProfile = authService.getUserProfile()
        val preferredLang = userProfile?.motherTongue ?: "ar" // Default to Arabic if not found

        // 1. Fetch Local Custom Lessons from Repository
        repository?.let { repo ->
            val sentences = repo.allSentences.first()
            val categories = sentences.groupBy { it.category }
            categories.forEach { (catName, catSentences) ->
                if (catSentences.isNotEmpty()) {
                    allLessons.add(
                        Lesson(
                            id = "local_$catName",
                            title = catName,
                            categoryId = catName,
                            content = catSentences,
                            type = "local",
                            icon = "📝"
                        )
                    )
                }
            }
        }

        // 2. Fetch Remote lessons from Firestore (new Magic Factory format)
        try {
            val snapshot = lessonsCollection.get().await()
            if (!snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    try {
                        val data = document.data ?: continue
                        val id = data["id"] as? String ?: document.id
                        val title = data["title"] as? String ?: "Untitled"
                        val categoryId = data["categoryId"] as? String ?: title
                        val icon = data["icon"] as? String ?: "📚"
                        val type = data["type"] as? String ?: "remote"
                        val authorId = data["authorId"] as? String ?: ""
                        val authorEmail = data["authorEmail"] as? String ?: ""
                        
                        val rawLevel = when (val rl = data["rawLevel"]) {
                            is Long -> rl.toString()
                            is Int -> rl.toString()
                            is String -> rl
                            else -> ""
                        }

                        val contentList = mutableListOf<Sentence>()
                        val rawContent = data["content"] as? List<*>
                        if (rawContent != null) {
                            for (item in rawContent) {
                                @Suppress("UNCHECKED_CAST")
                                val s = item as? Map<String, Any?> ?: continue
                                
                                // Dynamic Translation Selection
                                val translations = s["translations"] as? Map<String, String> ?: emptyMap()
                                val selectedTranslation = translations[preferredLang] 
                                    ?: translations["en"] 
                                    ?: s["translation"] as? String 
                                    ?: ""

                                contentList.add(
                                    Sentence(
                                        text = s["text"] as? String ?: "",
                                        translation = selectedTranslation,
                                        targetLang = s["targetLang"] as? String ?: "es",
                                        sourceLang = preferredLang,
                                        category = s["category"] as? String ?: categoryId,
                                        contentType = s["contentType"] as? String ?: "sentence",
                                        imageUrl = s["imageUrl"] as? String
                                    )
                                )
                            }
                        }

                        if (contentList.isNotEmpty()) {
                            allLessons.add(
                                Lesson(
                                    id = id,
                                    title = title,
                                    categoryId = categoryId,
                                    content = contentList,
                                    type = type,
                                    rawLevel = rawLevel,
                                    icon = icon,
                                    authorId = authorId,
                                    authorEmail = authorEmail
                                )
                            )
                        }
                    } catch (parseEx: Exception) {
                        android.util.Log.e("ContentService", "Failed to parse lesson: ${document.id}", parseEx)
                    }
                }
            } else {
                allLessons.addAll(loadLessonsFromAssets())
            }
        } catch (e: Exception) {
            android.util.Log.e("ContentService", "Firestore fetch failed, loading from assets", e)
            allLessons.addAll(loadLessonsFromAssets())
        }

        return allLessons.distinctBy { it.id }
    }

    private fun loadLessonsFromAssets(): List<Lesson> {
        return try {
            val inputStream = context.assets.open("lessons.json")
            val reader = InputStreamReader(inputStream)
            
            // New structure handler: The JSON is a Map<String, List<Map<String, Any>>>
            val type = object : com.google.gson.reflect.TypeToken<Map<String, List<Map<String, Any>>>>() {}.type
            val data: Map<String, List<Map<String, Any>>> = Gson().fromJson(reader, type)
            reader.close()

            val result = mutableListOf<Lesson>()
            val currentLang = context.resources.configuration.locales.get(0).language // "ar" or "en"
            val titleKey = if (currentLang == "ar") "ar-SA" else "en-US"

            data.forEach { (level, lessons) ->
                lessons.forEach { lessonMap ->
                    val id = lessonMap["id"] as? String ?: ""
                    val titles = lessonMap["title"] as? Map<String, String> ?: emptyMap()
                    
                    // Display title follows current language
                    val title = titles[titleKey] ?: titles["en-US"] ?: titles["ar-SA"] ?: "Untitled Lesson"
                    
                    // Internal category ID MUST BE STABLE (use English title or ID)
                    val categoryId = titles["en-US"] ?: title
                    
                    val rawLevel = lessonMap["rawLevel"] as? String ?: level
                    val icon = lessonMap["icon"] as? String ?: "📚"
                    
                    val sentenceList = mutableListOf<Sentence>()
                    val rawSentences = lessonMap["sentences"] as? List<Map<String, String>> ?: emptyList()
                    
                    rawSentences.forEach { sMap ->
                        sentenceList.add(
                            Sentence(
                                text = sMap["es"] ?: sMap["en"] ?: "",
                                translation = sMap["ar"] ?: sMap["en"] ?: "",
                                targetLang = if (sMap.containsKey("es")) "es" else "en",
                                sourceLang = currentLang,
                                category = categoryId, // Use the STABLE category ID
                                contentType = if ((sMap["es"]?.length ?: 0) < 15) "word" else "sentence",
                                imageUrl = sMap["imageUrl"] // Added support for AI images
                            )
                        )
                    }

                    result.add(
                        Lesson(
                            id = id,
                            title = title,
                            categoryId = categoryId,
                            content = sentenceList,
                            type = "asset",
                            rawLevel = rawLevel,
                            icon = icon
                        )
                    )
                }
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateLesson(lesson: Lesson) {
        lessonsCollection.document(lesson.id).set(lesson).await()
    }

    suspend fun clearAllLocalData() {
        repository?.clearAll()
    }

    suspend fun deleteLesson(lessonId: String) {
        lessonsCollection.document(lessonId).delete().await()
    }

    // Seed function for the admin to upload current local lessons to Firestore
    suspend fun seedDatabase() {
        val lessons = loadLessonsFromAssets()
        for (lesson in lessons) {
            updateLesson(lesson)
        }
    }
}
