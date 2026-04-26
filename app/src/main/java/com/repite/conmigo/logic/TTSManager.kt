package com.repite.conmigo.logic

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class TTSManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _highlightedWordIndex = MutableStateFlow(-1)
    val highlightedWordIndex: StateFlow<Int> = _highlightedWordIndex

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _highlightedWordIndex.value = -1
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    if (utteranceId != null && utteranceId.startsWith("H|")) {
                        val text = utteranceId.substring(2)
                        val words = text.split(" ")
                        var currentPos = 0
                        for (i in words.indices) {
                            if (start >= currentPos && start < currentPos + words[i].length + 1) {
                                _highlightedWordIndex.value = i
                                break
                            }
                            currentPos += words[i].length + 1
                        }
                    }
                }
            })
        }
    }

    fun speak(text: String, language: String, speed: Float = 1.0f) {
        val locale = when (language) {
            "es" -> Locale("es", "ES")
            "en" -> Locale.ENGLISH
            else -> Locale.ENGLISH
        }
        tts?.setLanguage(locale)
        tts?.setSpeechRate(speed)
        
        // Pass text in utteranceId too for highlighting parsing
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "H|$text")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutDown() {
        tts?.shutdown()
    }
}
