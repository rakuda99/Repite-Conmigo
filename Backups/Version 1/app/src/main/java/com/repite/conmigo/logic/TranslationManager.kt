package com.repite.conmigo.logic

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationManager {
    
    private val optionsES2AR = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.SPANISH)
        .setTargetLanguage(TranslateLanguage.ARABIC)
        .build()

    private val optionsEN2AR = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.ARABIC)
        .build()

    private val es2arTranslator = Translation.getClient(optionsES2AR)
    private val en2arTranslator = Translation.getClient(optionsEN2AR)

    suspend fun translate(text: String, fromLang: String): String {
        return try {
            val translator = if (fromLang == "es") es2arTranslator else en2arTranslator
            // Ensure model is downloaded (you might want to handle this in UI with a progress bar)
            translator.downloadModelIfNeeded().await()
            translator.translate(text).await()
        } catch (e: Exception) {
            "Error en traducción"
        }
    }

    fun close() {
        es2arTranslator.close()
        en2arTranslator.close()
    }
}
