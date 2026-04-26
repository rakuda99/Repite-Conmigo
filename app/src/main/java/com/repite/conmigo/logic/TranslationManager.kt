package com.repite.conmigo.logic

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationManager {
    
    suspend fun translate(text: String, fromLang: String, targetLang: String = "en"): String {
        return try {
            val source = getMLKitLang(fromLang)
            val target = getMLKitLang(targetLang)
            
            if (source == target) return text

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(source)
                .setTargetLanguage(target)
                .build()
            
            val translator = Translation.getClient(options)
            translator.downloadModelIfNeeded().await()
            val result = translator.translate(text).await()
            translator.close()
            result
        } catch (e: Exception) {
            "..."
        }
    }

    private fun getMLKitLang(code: String): String {
        return when (code.lowercase()) {
            "es" -> TranslateLanguage.SPANISH
            "en" -> TranslateLanguage.ENGLISH
            "ar" -> TranslateLanguage.ARABIC
            "fr" -> TranslateLanguage.FRENCH
            "de" -> TranslateLanguage.GERMAN
            "it" -> TranslateLanguage.ITALIAN
            "ja" -> TranslateLanguage.JAPANESE
            "ko" -> TranslateLanguage.KOREAN
            "ru" -> TranslateLanguage.RUSSIAN
            "pt" -> TranslateLanguage.PORTUGUESE
            "tr" -> TranslateLanguage.TURKISH
            else -> TranslateLanguage.ENGLISH
        }
    }
}
