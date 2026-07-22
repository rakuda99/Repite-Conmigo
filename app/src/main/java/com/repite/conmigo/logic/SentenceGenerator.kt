package com.repite.conmigo.logic

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

data class GeneratedSentence(
    val sentence: String,
    val translation: String
)

data class GeneratedVerbDetails(
    val infinitive_meaning: String,
    val past: String,
    val past_meaning: String,
    val present: String,
    val present_meaning: String,
    val future: String,
    val future_meaning: String,
    val sentence_infinitive: String,
    val trans_infinitive: String,
    val sentence_past: String,
    val trans_past: String,
    val sentence_present: String,
    val trans_present: String,
    val sentence_future: String,
    val trans_future: String
)

class SentenceGenerator {
    
    suspend fun generateSentences(
        apiKey: String,
        word: String,
        learningLanguage: String,
        nativeLanguage: String
    ): Result<List<GeneratedSentence>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("API Key is empty"))
        }

        try {
            val prompt = """
                Generate exactly 5 simple, everyday example sentences in $learningLanguage containing the word '$word'.
                Also provide their translations in $nativeLanguage.
                
                CRITICAL INSTRUCTIONS:
                - Output ONLY the 5 sentences and their translations.
                - Use EXACTLY this format for each sentence on a new line:
                  Sentence in $learningLanguage|Translation in $nativeLanguage
                - Do NOT include numbers (1., 2.), markdown tags, bullet points, or any other text.
                - Just the 5 lines of text separated by the pipe '|' character.
            """.trimIndent()

            val url = java.net.URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\n", "\\n").replace("\"", "\\\"") + "\"}]}]}"
            
            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            val responseString = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorString = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                var exactError = "Unknown error"
                try {
                    val messageRegex = "\"message\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                    val match = messageRegex.find(errorString)
                    if (match != null) {
                        exactError = match.groups[1]?.value ?: exactError
                    }
                } catch (e: Exception) {
                    exactError = errorString
                }
                return@withContext Result.failure(Exception("السبب: $exactError (HTTP $responseCode)"))
            }

            // Simple parsing to extract the text from JSON
            val textRegex = "\"text\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = textRegex.find(responseString)
            val extractedText = match?.groups?.get(1)?.value?.replace("\\n", "\n")?.replace("\\\"", "\"")
                ?: return@withContext Result.failure(Exception("Failed to extract text from response"))

            val results = mutableListOf<GeneratedSentence>()
            extractedText.lines().forEach { line ->
                val cleanLine = line.replace("*", "").replace("`", "")
                val parts = cleanLine.split("|")
                if (parts.size >= 2) {
                    val sentence = parts[0].trim()
                    val translation = parts[1].trim()
                    val cleanSentence = sentence.replace(Regex("^[0-9]+\\.\\s*"), "").trim()
                    if (cleanSentence.isNotBlank() && translation.isNotBlank()) {
                        results.add(GeneratedSentence(cleanSentence, translation))
                    }
                }
            }
            
            if (results.isEmpty()) {
                return@withContext Result.failure(Exception("Could not parse: $extractedText"))
            }
            
            return@withContext Result.success(results)
        } catch (e: Exception) {
            Log.e("SentenceGenerator", "Error generating sentences", e)
            return@withContext Result.failure(e)
        }
    }

    suspend fun generateVerbDetails(
        apiKey: String,
        verb: String,
        learningLanguage: String,
        nativeLanguage: String
    ): Result<GeneratedVerbDetails> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("API Key is empty"))
        }

        try {
            val prompt = """
                Provide the standard conjugations (using the 1st person singular 'Yo' pronoun) and meanings in $nativeLanguage for the $learningLanguage verb: '$verb'.
                Also generate 1 simple everyday example sentence for each form (infinitive, past, present, future) and its translation in $nativeLanguage.
                You MUST return ONLY a valid JSON object matching this exact structure, without any markdown formatting, backticks, or additional text:
                {
                  "infinitive_meaning": "",
                  "past": "",
                  "past_meaning": "",
                  "present": "",
                  "present_meaning": "",
                  "future": "",
                  "future_meaning": "",
                  "sentence_infinitive": "",
                  "trans_infinitive": "",
                  "sentence_past": "",
                  "trans_past": "",
                  "sentence_present": "",
                  "trans_present": "",
                  "sentence_future": "",
                  "trans_future": ""
                }
            """.trimIndent()

            val url = java.net.URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\n", "\\n").replace("\"", "\\\"") + "\"}]}]}"
            
            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            val responseString = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorString = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                var exactError = "Unknown error"
                try {
                    val messageRegex = "\"message\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                    val match = messageRegex.find(errorString)
                    if (match != null) {
                        exactError = match.groups[1]?.value ?: exactError
                    }
                } catch (e: Exception) {
                    exactError = errorString
                }
                return@withContext Result.failure(Exception("السبب: $exactError (HTTP $responseCode)"))
            }

            val textRegex = "\"text\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = textRegex.find(responseString)
            val extractedText = match?.groups?.get(1)?.value?.replace("\\n", "\n")?.replace("\\\"", "\"")
                ?.replace("```json", "")?.replace("```", "")?.trim()
                ?: return@withContext Result.failure(Exception("Failed to extract text from response"))

            val details = com.google.gson.Gson().fromJson(extractedText, GeneratedVerbDetails::class.java)
                ?: return@withContext Result.failure(Exception("Could not parse JSON"))
            
            return@withContext Result.success(details)
        } catch (e: Exception) {
            Log.e("SentenceGenerator", "Error generating verb details", e)
            return@withContext Result.failure(e)
        }
    }

    suspend fun generateStoryFromWords(
        apiKey: String,
        words: List<String>,
        learningLanguage: String,
        nativeLanguage: String
    ): Result<GeneratedSentence> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("API Key is empty"))
        }

        try {
            val wordsListStr = words.joinToString(", ")
            val prompt = """
                Write a short, engaging story in $learningLanguage that includes ALL of the following words: $wordsListStr.
                The story should be a single paragraph, no longer than 150 words.
                Also provide its full translation in $nativeLanguage.
                
                CRITICAL INSTRUCTIONS:
                - Output ONLY the story and its translation.
                - Use EXACTLY this format:
                  Story in $learningLanguage|Translation in $nativeLanguage
                - Do NOT include any other text, titles, markdown, or line breaks before the pipe character.
                - Just the two texts separated by the pipe '|' character on a single line (or single output block).
            """.trimIndent()

            val url = java.net.URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=$apiKey")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonInputString = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\n", "\\n").replace("\"", "\\\"") + "\"}]}]}"
            
            connection.outputStream.use { os ->
                val input = jsonInputString.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = connection.responseCode
            val responseString = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorString = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                var exactError = "Unknown error"
                try {
                    val messageRegex = "\"message\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                    val match = messageRegex.find(errorString)
                    if (match != null) {
                        exactError = match.groups[1]?.value ?: exactError
                    }
                } catch (e: Exception) {
                    exactError = errorString
                }
                return@withContext Result.failure(Exception("السبب: $exactError (HTTP $responseCode)"))
            }

            val textRegex = "\"text\"\\s*:\\s*\"(.*?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
            val match = textRegex.find(responseString)
            val extractedText = match?.groups?.get(1)?.value?.replace("\\n", " ")?.replace("\\\"", "\"")
                ?: return@withContext Result.failure(Exception("Failed to extract text from response"))

            val cleanLine = extractedText.replace("*", "").replace("`", "").trim()
            val parts = cleanLine.split("|")
            if (parts.size >= 2) {
                val story = parts[0].trim()
                val translation = parts[1].trim()
                if (story.isNotBlank() && translation.isNotBlank()) {
                    return@withContext Result.success(GeneratedSentence(story, translation))
                }
            }
            
            return@withContext Result.failure(Exception("Could not parse: $extractedText"))
        } catch (e: Exception) {
            Log.e("SentenceGenerator", "Error generating story", e)
            return@withContext Result.failure(e)
        }
    }
}
