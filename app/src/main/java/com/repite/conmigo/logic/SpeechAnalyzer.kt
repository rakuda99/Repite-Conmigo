package com.repite.conmigo.logic

import kotlin.math.max

object SpeechAnalyzer {
    
    /**
     * Calculates the similarity between two strings using Levenshtein distance.
     * Returns a score between 0 and 100.
     */
    fun calculateAccuracy(original: String, spoken: String): Float {
        val s1 = normalize(original)
        val s2 = normalize(spoken)
        
        if (s1 == s2) return 100f
        if (s1.isEmpty() || s2.isEmpty()) return 0f
        
        val distance = levenshteinDistance(s1, s2)
        val maxLength = Math.max(s1.length, s2.length)
        
        return ((maxLength - distance).toFloat() / maxLength * 100f).coerceIn(0f, 100f)
    }

    private fun normalize(text: String): String {
        return text.lowercase()
            .replace("[\\p{Punct}¿¡]".toRegex(), "")
            .trim()
            .replace("\\s+".toRegex(), " ")
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val costs = IntArray(s2.length + 1)
        for (i in 0..s1.length) {
            var lastValue = i
            for (j in 0..s2.length) {
                if (i == 0) {
                    costs[j] = j
                } else if (j > 0) {
                    var newValue = costs[j - 1]
                    if (s1[i - 1] != s2[j - 1]) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1
                    }
                    costs[j - 1] = lastValue
                    lastValue = newValue
                }
            }
            if (i > 0) costs[s2.length] = lastValue
        }
        return costs[s2.length]
    }

    fun getFeedback(original: String, spoken: String): String {
        val originalWords = normalize(original).split(" ")
        val spokenWords = normalize(spoken).split(" ")
        
        val missingWords = originalWords.filter { it !in spokenWords && it.length > 1 }
        
        return if (missingWords.isEmpty()) {
            "نطق ممتاز ومثالي! ✨"
        } else {
            "حاول نطق هذه الكلمات بشكل أفضل: ${missingWords.take(2).joinToString(", ")}"
        }
    }

    fun getMissingWords(original: String, spoken: String): Set<String> {
        val originalWords = normalize(original).split(" ")
        val spokenWords = normalize(spoken).split(" ")
        return originalWords.filter { it !in spokenWords && it.length > 1 }.toSet()
    }
}
