package com.repite.conmigo.logic

import android.content.Context
import android.util.Log
import com.repite.conmigo.data.Sentence
import com.repite.conmigo.data.LessonRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object RemoteContentService {

    private val client = OkHttpClient()

    // رابط تجريبي لجدول البيانات (يجب استبداله برابط العميل لاحقاً)
    private const val GOOGLE_SHEETS_CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRE-T-KUT80V8p7GqQW6b8z7S9-X-G/pub?output=csv"

    suspend fun syncContent(repository: LessonRepository): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(GOOGLE_SHEETS_CSV_URL)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Failed to fetch data"))

            val body = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val reader = BufferedReader(InputStreamReader(body.byteStream()))
            
            val sentences = mutableListOf<Sentence>()
            var line: String? = reader.readLine() // Skip header
            
            while (reader.readLine().also { line = it } != null) {
                // CSV parsing (Improved to handle quoted fields with commas)
                val parts = line?.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())?.map { 
                    it.trim().removeSurrounding("\"").replace("\"\"", "\"") 
                } ?: continue
                
                if (parts.size >= 4) {
                    val level = parts[0]
                    val lessonTitle = parts[1]
                    val spanishText = parts[2]
                    val arabicText = parts[3]

                    sentences.add(
                        Sentence(
                            text = spanishText,
                            targetLang = "es",
                            translation = arabicText,
                            category = "$level - $lessonTitle",
                            contentType = if (spanishText.split(" ").size > 6) "passage" else "sentence"
                        )
                    )
                }
            }
            
            if (sentences.isNotEmpty()) {
                repository.clearAll() // Use correct method name
                repository.insertSentences(sentences)
                return@withContext Result.success(sentences.size)
            }

            Result.success(0)
        } catch (e: Exception) {
            Log.e("RemoteContentService", "Sync failed", e)
            Result.failure(e)
        }
    }
}
