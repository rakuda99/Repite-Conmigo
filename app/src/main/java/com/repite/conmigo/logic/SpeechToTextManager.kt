package com.repite.conmigo.logic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SpeechToTextManager(private val context: Context) {
    private val speechRecognizer: SpeechRecognizer = try {
        // Use system default SpeechRecognizer (safer, prevents silent hangs on Samsung/Xiaomi/etc.)
        SpeechRecognizer.createSpeechRecognizer(context)
    } catch (e: Exception) {
        // Fallback to Google Search app service if default creation fails
        try {
            val isGoogleInstalled = try {
                context.packageManager.getPackageInfo("com.google.android.googlequicksearchbox", 0)
                true
            } catch (_: Exception) {
                false
            }
            if (isGoogleInstalled) {
                SpeechRecognizer.createSpeechRecognizer(context, android.content.ComponentName(
                    "com.google.android.googlequicksearchbox",
                    "com.google.android.voicesearch.service.GoogleRecognitionService"
                ))
            } else {
                SpeechRecognizer.createSpeechRecognizer(context)
            }
        } catch (_: Exception) {
            SpeechRecognizer.createSpeechRecognizer(context)
        }
    }
    private val mainHandler = Handler(Looper.getMainLooper())
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText

    private val _rmsDb = MutableStateFlow(0f)
    val rmsDb: StateFlow<Float> = _rmsDb

    private var onResult: ((String) -> Unit)? = null
    private var pendingIntent: Intent? = null
    private var retryCount = 0

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
                _partialText.value = ""
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                _rmsDb.value = rmsdB
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _isListening.value = false
                _rmsDb.value = 0f
            }

            override fun onError(error: Int) {
                _isListening.value = false
                _rmsDb.value = 0f
                if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY && retryCount < 3) {
                    // Auto-retry after a brief delay
                    retryCount++
                    val intentToRetry = pendingIntent ?: return
                    mainHandler.postDelayed({
                        try {
                            speechRecognizer.stopListening()
                        } catch (_: Exception) {}
                        mainHandler.postDelayed({
                            try {
                                _isListening.value = true
                                speechRecognizer.startListening(intentToRetry)
                            } catch (e: Exception) {
                                onResult?.invoke("ERR: تعذّر تشغيل الميكروفون 🎤")
                            }
                        }, 300L)
                    }, 200L)
                    return
                }
                retryCount = 0
                val errorMsg = when(error) {
                    SpeechRecognizer.ERROR_NETWORK -> "خطأ في الشبكة ⚠️"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "انتهت مهلة الشبكة ⚠️"
                    SpeechRecognizer.ERROR_AUDIO -> "خطأ في الميكروفون 🎤"
                    SpeechRecognizer.ERROR_NO_MATCH -> "لم أتعرف على أي كلمات ⏹️"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "المحرك مشغول، حاول مجدداً..."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "يجب تفعيل إذن الميكروفون 🎙️"
                    else -> "عذراً، محرك الصوت لا يستجيب ⚙️"
                }
                onResult?.invoke("ERR: $errorMsg") 
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                _rmsDb.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResult?.invoke(matches.joinToString("|"))
                } else {
                    onResult?.invoke("")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _partialText.value = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening(language: String, callback: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            callback("ERR: التعرف على الصوت غير متاح على هذا الجهاز 🎙️")
            return
        }
        onResult = callback
        retryCount = 0
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            val langTag = if (language == "es") "es-ES" else "en-US"
            val secondaryLangs = if (language == "es") arrayListOf("es", "es-MX", "es-US") else arrayListOf("en", "en-GB")
            
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, secondaryLangs)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }
        
        // Stop any existing session first to avoid ERROR_RECOGNIZER_BUSY
        mainHandler.post {
            try {
                speechRecognizer.stopListening()
            } catch (_: Exception) {}
        }
        
        mainHandler.postDelayed({
            try {
                _isListening.value = true
                _partialText.value = ""
                pendingIntent = intent
                speechRecognizer.startListening(intent)
            } catch (e: Exception) {
                _isListening.value = false
                callback("ERR: تعذّر تشغيل الميكروفون، حاول مرة أخرى 🎤")
            }
        }, 200L)
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        speechRecognizer.destroy()
    }
}
