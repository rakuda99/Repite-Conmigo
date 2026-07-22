package com.repite.conmigo.logic

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.repite.conmigo.models.Lesson
import com.repite.conmigo.data.LessonRepository
import com.repite.conmigo.data.Sentence
import com.repite.conmigo.data.determineContentType
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
        val preferredLang = userProfile?.motherTongue ?: "en" 

        // 1. Fetch Local Lessons from Repository (ONLY)
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

        // If absolutely empty, we can show assets or just leave it empty for the user to pick from cloud
        if (allLessons.isEmpty()) {
            // Load from assets if DB is empty (fresh install or after wipe)
            allLessons.addAll(loadLessonsFromAssets())
        }

        return allLessons.distinctBy { it.id }
    }

    suspend fun loadLessonsFromAssets(): List<Lesson> {
        return try {
            val inputStream = context.assets.open("lessons.json")
            val reader = InputStreamReader(inputStream)
            
            // New structure handler: The JSON is a Map<String, List<Map<String, Any>>>
            val type = object : com.google.gson.reflect.TypeToken<Map<String, List<Map<String, Any>>>>() {}.type
            val data: Map<String, List<Map<String, Any>>> = Gson().fromJson(reader, type)
            reader.close()

            val authService = AuthService(context)
            val userProfile = authService.getUserProfile()
            val currentLang = userProfile?.motherTongue ?: "ar" // Default to Arabic for this specific user since they are from an Arab region, or 'en'
            
            val result = mutableListOf<Lesson>()
            val titleKey = if (currentLang == "ar") "ar-SA" else "en-US"

            data.forEach { (level, lessons) ->
                lessons.forEach { lessonMap ->
                    val id = lessonMap["id"] as? String ?: ""
                    val titles = lessonMap["title"] as? Map<String, String> ?: emptyMap()
                    
                    // Display title follows current language
                    val title = titles[titleKey] ?: titles["en-US"] ?: titles["ar-SA"] ?: "Untitled Lesson"
                    
                    // Internal category ID MUST BE STABLE (use English title or ID)
                    val categoryId = titles["en-US"] ?: titles["es-ES"] ?: titles["ar-SA"] ?: title
                    
                    val rawLevel = lessonMap["rawLevel"] as? String ?: level
                    val icon = lessonMap["icon"] as? String ?: "📚"
                    val sentenceList = mutableListOf<Sentence>()
                    val rawSentences = lessonMap["sentences"] as? List<Map<String, String>> ?: emptyList()
                    
                    rawSentences.forEach { sMap ->
                        // Smarter fallback: Try user's language -> English -> Arabic -> Spanish
                        val translation = sMap[currentLang] ?: sMap["ar"] ?: sMap["en"] ?: sMap["es"] ?: ""

                        sentenceList.add(
                            Sentence(
                                text = sMap["es"] ?: sMap["en"] ?: "",
                                translation = translation,
                                targetLang = if (sMap.containsKey("es")) "es" else "en",
                                sourceLang = if (currentLang == "en") "en" else "ar",
                                category = categoryId, 
                                contentType = sMap["contentType"] ?: determineContentType(sMap["es"] ?: sMap["en"] ?: "", categoryId),
                                imageUrl = sMap["imageUrl"] 
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
        if (lessonId.startsWith("local_")) {
            val category = lessonId.removePrefix("local_")
            repository?.deleteByCategory(category)
        } else {
            lessonsCollection.document(lessonId).delete().await()
        }
    }

    // Seed function for the admin to upload current local lessons to Firestore
    suspend fun seedDatabase() {
        val lessons = loadLessonsFromAssets()
        for (lesson in lessons) {
            updateLesson(lesson)
        }
    }
}
