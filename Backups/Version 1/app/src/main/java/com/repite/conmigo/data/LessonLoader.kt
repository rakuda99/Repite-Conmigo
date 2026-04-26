package com.repite.conmigo.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

data class PingoTitle(
    val `ar-SA`: String? = null,
    val `en-US`: String? = null
)

data class PingoSentence(
    val es: String,
    val ar: String,
    val imagePrompt: String? = null
)

data class PingoLesson(
    val id: String,
    val title: PingoTitle,
    val rawLevel: String,
    val icon: String? = null,
    val color: String? = null,
    val sentences: List<PingoSentence>
)

object LessonLoader {

    fun loadPingoData(context: Context, repository: LessonRepository) {
        try {
            val inputStream = context.assets.open("lessons.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()

            // Map<String, List<PingoLesson>> (Level -> List of lessons)
            val type = object : TypeToken<Map<String, List<PingoLesson>>>() {}.type
            val data: Map<String, List<PingoLesson>> = gson.fromJson(reader, type)
            reader.close()

            val sentencesToInsert = mutableListOf<Sentence>()

            for ((levelName, lessons) in data) {
                val cleanedCategory = levelName.replace(Regex("[^A-Za-z0-9 ]"), "").trim()
                
                for (lesson in lessons) {
                    val subCategory = lesson.title.`ar-SA` ?: lesson.title.`en-US` ?: "Unknown Lesson"
                    val finalCategory = "$cleanedCategory - $subCategory"

                    lesson.sentences.forEach { s ->
                        sentencesToInsert.add(
                            Sentence(
                                text = s.es ?: "",
                                language = "es",
                                translation = s.ar ?: "",
                                category = finalCategory,
                                contentType = if (s.es.split(" ").size > 6) "passage" else "sentence"
                            )
                        )
                    }
                }
            }

            if (sentencesToInsert.isNotEmpty()) {
                // Perform insert using coroutine
                kotlinx.coroutines.GlobalScope.launch {
                    repository.insertSentences(sentencesToInsert)
                    Log.d("LessonLoader", "Inserted ${sentencesToInsert.size} sentences from Pingo AI")
                }
            }

        } catch (e: Exception) {
            Log.e("LessonLoader", "Failed to load lessons.json", e)
        }
    }
}
