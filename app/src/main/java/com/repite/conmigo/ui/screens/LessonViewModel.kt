package com.repite.conmigo.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repite.conmigo.data.*
import com.repite.conmigo.logic.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.util.Log

class LessonViewModel(
    private val repository: LessonRepository,
    private val ttsManager: TTSManager,
    private val translationManager: TranslationManager,
    private val sttManager: SpeechToTextManager,
    private val audioRecorder: AudioRecorder,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonState())
    val uiState: StateFlow<LessonState> = _uiState.asStateFlow()

    val rmsDb: StateFlow<Float> = sttManager.rmsDb

    val learningRecords = repository.allRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> = _selectedWord.asStateFlow()

    private val _accuracyThreshold = MutableStateFlow(70f)
    val accuracyThreshold: StateFlow<Float> = _accuracyThreshold.asStateFlow()

    private val _learningLanguage = MutableStateFlow("es")
    val learningLanguage: StateFlow<String> = _learningLanguage.asStateFlow()

    private val _nativeLanguage = MutableStateFlow("ar")
    val nativeLanguage: StateFlow<String> = _nativeLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _learningLanguage.value = lang
        loadSentences()
    }

    fun setNativeLanguage(lang: String) {
        _nativeLanguage.value = lang
        // Full DB sync: Translate EVERY sentence in the repository to the new native language
        translateAllInDatabase(lang)
    }

    private fun translateAllInDatabase(target: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSyncing = true, feedback = "Global Language Sync: $target... ⏳") }
            try {
                val all = repository.allSentences.first()
                if (all.isEmpty()) {
                    _uiState.update { it.copy(isSyncing = false, feedback = "") }
                    return@launch
                }
                
                val updatedSentences = all.map { sentence ->
                    val textToTranslate = sentence.text
                    val translation = if (textToTranslate.length == 1 && textToTranslate[0].isLetter()) {
                         // Manual mapping for letters if ML Kit fails
                         when(textToTranslate.uppercase()) {
                             "A" -> "أ"
                             "B" -> "ب"
                             "C" -> "ج"
                             else -> translationManager.translate(textToTranslate, sentence.targetLang, target)
                         }
                    } else {
                        translationManager.translate(textToTranslate, sentence.targetLang, target)
                    }
                    sentence.copy(translation = translation, sourceLang = target)
                }
                
                // Batch update to DB - only one trigger to the UI
                repository.insertSentences(updatedSentences)
                
                _uiState.update { it.copy(isSyncing = false, feedback = "Sync Complete! ✅") }
                loadSentences() // Final stable refresh
            } catch (e: Exception) {
                _uiState.update { it.copy(isSyncing = false, feedback = "Sync Failed.") }
            }
        }
    }

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedContentType = MutableStateFlow("sentence") // "word", "sentence", "passage"
    val selectedContentType: StateFlow<String> = _selectedContentType.asStateFlow()

    val categories = repository.categories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val contentTypes = repository.contentTypes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val userProgress = repository.userProgress.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UserProgress()
    )

    val categoryProgressStats = combine(
        repository.allSentences,
        repository.allRecords
    ) { sentences, records ->
        val recordMap = records.groupBy { it.sentenceId }
        val bestAccuracyMap = recordMap.mapValues { entry -> 
            entry.value.maxOf { it.accuracy }
        }

        val categoriesGroup = sentences.groupBy { it.category }
        
        categoriesGroup.mapNotNull { (catName, catSentences) ->
            
            var masteredCount = 0
            catSentences.forEach { s ->
                val bestAcc = bestAccuracyMap[s.id] ?: 0f
                if (bestAcc >= 85f) {
                    masteredCount++
                }
            }
            
            val total = catSentences.size
            val perc = if (total == 0) 0f else (masteredCount.toFloat() / total) * 100
            
            val level = if (catName.contains(" - ")) {
                catName.split(" - ")[0]
            } else if (catName.startsWith("Pingo:")) {
                "Pingo"
            } else {
                "Custom"
            }
            val cleanTitle = if (catName.contains(" - ")) catName.split(" - ")[1] else catName

            CategoryProgress(
                rawName = catName,
                cleanName = cleanTitle,
                levelPrefix = level,
                isMastered = total > 0 && perc >= 100f,
                progressPercent = perc.toInt()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRecording = MutableStateFlow(false)
    private val _isQuizMode = MutableStateFlow(false)
    val isQuizMode: StateFlow<Boolean> = _isQuizMode.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    fun toggleSpeechRate() {
        _speechRate.value = when (_speechRate.value) {
            1.0f -> 1.4f
            1.4f -> 0.6f
            else -> 1.0f
        }
    }

    init {
        loadSentences()
        observeTTSHighlight()
        // Force Refresh for v9.5.0 Content (Checking for version bump in content)
        viewModelScope.launch {
            val currentLang = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en"
            val hasEnglishLetters = repository.getSentenceCountByCategory("Letters") > 0
            val hasArabicLetters = repository.getSentenceCountByCategory("Letters (الحروف)") > 0
            
            // If the content language doesn't match the UI language for official lessons, refresh them
            if ((currentLang == "ar" && !hasArabicLetters) || (currentLang != "ar" && !hasEnglishLetters)) {
                repository.clearAll() // Careful: this clears custom too, but for current dev phase its best
                seedData()
            }
        }
    }

    private fun observeTTSHighlight() {
        viewModelScope.launch {
            ttsManager.highlightedWordIndex.collect { index ->
                _uiState.update { it.copy(highlightedIndex = index) }
            }
        }
    }

    private fun loadSentences() {
        viewModelScope.launch {
            combine(
                repository.allSentences, 
                _learningLanguage, 
                _selectedCategory, 
                _selectedContentType
            ) { all, lang, cat, type ->
                if (all.isEmpty()) seedData()
                all.filter { 
                    it.targetLang == lang && 
                    if (cat != null) {
                        it.category == cat
                    } else {
                        it.contentType == type
                    }
                }.distinctBy { it.text.trim().lowercase() }
            }.collect { filtered ->
                val finalSentences = if (_isQuizMode.value) filtered.shuffled() else filtered
                _uiState.update { it.copy(sentences = finalSentences, currentIndex = 0) }
            }
        }
    }

    fun setQuizMode(enabled: Boolean) {
        _isQuizMode.value = enabled
        loadSentences()
    }

    fun selectCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun selectContentType(type: String) {
        _selectedContentType.value = type
        _selectedCategory.value = null // Reset category filter when switching types
    }

    private suspend fun seedData() {
        val currentLang = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en"
        val isAr = currentLang == "ar"

        val defaultSentences = if (isAr) {
            listOf(
                Sentence(text = "A", targetLang = "es", sourceLang = "ar", translation = "أ (نطق: آه)", category = "Letters (الحروف)", contentType = "word"),
                Sentence(text = "B", targetLang = "es", sourceLang = "ar", translation = "باء (نطق: بيه)", category = "Letters (الحروف)", contentType = "word"),
                Sentence(text = "Desayuno", targetLang = "es", sourceLang = "ar", translation = "فطور", category = "دروس أساسية", contentType = "word"),
                Sentence(text = "Escuela", targetLang = "es", sourceLang = "ar", translation = "مدرسة", category = "دروس أساسية", contentType = "word"),
                Sentence(text = "El cielo está nublado hoy", targetLang = "es", sourceLang = "ar", translation = "السماء غائمة اليوم", category = "حياة يومية", contentType = "sentence")
            )
        } else {
            listOf(
                Sentence(text = "A", targetLang = "es", sourceLang = "en", translation = "A (Pronounced: Ah)", category = "Letters", contentType = "word"),
                Sentence(text = "B", targetLang = "es", sourceLang = "en", translation = "B (Pronounced: Beh)", category = "Letters", contentType = "word"),
                Sentence(text = "Desayuno", targetLang = "es", sourceLang = "en", translation = "Breakfast", category = "Common Words", contentType = "word"),
                Sentence(text = "Escuela", targetLang = "es", sourceLang = "en", translation = "School", category = "Common Words", contentType = "word"),
                Sentence(text = "El cielo está nublado hoy", targetLang = "es", sourceLang = "en", translation = "The sky is cloudy today", category = "Daily Life", contentType = "sentence")
            )
        }
        repository.insertSentences(defaultSentences)
    }

    fun loadGlobalLessons(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val contentService = ContentService(context, repository)
            val lessons = contentService.getLessons()
            lessons.forEach { lesson ->
                if (lesson.type == "asset") {
                    // Update: Clear and re-load asset lessons to ensure content fixes (like alphabet) are applied
                    repository.deleteByCategory(lesson.categoryId)
                    repository.insertSentences(lesson.content)
                    Log.d("LessonViewModel", "Updated lesson content: ${lesson.title}")
                }
            }
            
            // Cleanup: If the user has old local 'Letters' from previous versions, clear those too if empty or messy
            if (repository.getSentenceCountByCategory("Letters (الحروف)") < 10) {
                 repository.deleteByCategory("Letters (الحروف)")
            }
        }
    }


            







    fun speakCurrent() {
        val state = uiState.value
        if (state.sentences.isNotEmpty()) {
            val sentence = state.sentences[state.currentIndex]
            if (sentence.localAudioPath != null) {
                audioRecorder.playFile(sentence.localAudioPath)
            } else {
                ttsManager.speak(sentence.text, sentence.targetLang, speechRate.value)
            }
        }
    }

    // --- Chat State ---
    private val _chatMessages = MutableStateFlow(listOf(ChatMessage("¡Hola! Soy tu asistente. ¿Sobre qué quieres hablar hoy?", false)))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    fun onRecordClick(isInChat: Boolean = false) {
        val isRecording = _uiState.value.isRecording
        if (isRecording) {
            sttManager.stopListening()
            audioRecorder.stopRecording()
            _uiState.update { it.copy(isRecording = false) }
        } else {
            val lang = if (isInChat) _learningLanguage.value else (uiState.value.sentences.getOrNull(uiState.value.currentIndex)?.targetLang ?: "es")
            
            _uiState.update { it.copy(isRecording = true, feedback = "...") }
            audioRecorder.startRecording()
            
            sttManager.startListening(lang) { spokenText ->
                _uiState.update { it.copy(isRecording = false) }
                audioRecorder.stopRecording()
                
                if (spokenText.isNotEmpty()) {
                    if (isInChat) {
                        handleChatTranscription(spokenText)
                    } else {
                        analyzeSpokenText(spokenText)
                    }
                }
            }
        }
    }

    private fun handleChatTranscription(text: String) {
        if (text.startsWith("ERR:")) return
        
        val userMsg = ChatMessage(text, true)
        _chatMessages.value = _chatMessages.value + userMsg
        
        val lang = _learningLanguage.value
        val response = if (lang == "es") {
            "¡Entendido! Has dicho: \"$text\"."
        } else {
            "I heard: \"$text\"."
        }
        _chatMessages.value = _chatMessages.value + ChatMessage(response, false)
    }

    fun onWordClick(word: String) {
        _selectedWord.value = word
    }

    fun dismissWordDialog() {
        _selectedWord.value = null
    }

    fun speakWord(word: String) {
        val lang = uiState.value.sentences.getOrNull(uiState.value.currentIndex)?.targetLang ?: "es"
        ttsManager.speak(word, lang, speechRate.value)
    }

    fun analyzeSpokenText(spokenText: String) {
        _uiState.update { it.copy(isRecording = false) } // Force Reset
        
        if (spokenText.startsWith("ERR: ")) {
            _uiState.update { it.copy(feedback = spokenText.removePrefix("ERR: ")) }
            return
        }

        if (spokenText.isBlank()) {
            _uiState.update { it.copy(feedback = "Didn't hear anything, try again.. 🎤", lastAccuracy = 0f, lastTranscription = "") }
            return
        }
        val currentSentence = uiState.value.sentences.getOrNull(uiState.value.currentIndex) ?: return
        
        val candidates = spokenText.split("|")
        var bestAcc = 0f
        var bestMatch = candidates[0]
        var minMisses = Int.MAX_VALUE

        candidates.forEach { candidate ->
            val acc = SpeechAnalyzer.calculateAccuracy(currentSentence.text, candidate)
            val missing = SpeechAnalyzer.getMissingWords(currentSentence.text, candidate)
            if (acc > bestAcc || (acc == bestAcc && missing.size < minMisses)) {
                bestAcc = acc
                bestMatch = candidate
                minMisses = missing.size
            }
        }

        val finalFeedback = SpeechAnalyzer.getFeedback(currentSentence.text, bestMatch)
        
        _uiState.update { it.copy(
            lastAccuracy = bestAcc,
            feedback = if (bestAcc >= accuracyThreshold.value && bestAcc > 0) "Excellent pronunciation! ✨" else if (bestAcc == 0f) "Didn't hear you clearly... try again 🎙️" else finalFeedback,
            wrongWords = SpeechAnalyzer.getMissingWords(currentSentence.text, bestMatch),
            lastTranscription = bestMatch
        )}

        // Save Smart Record
        viewModelScope.launch {
            repository.addRecord(
                LearningRecord(
                    sentenceId = currentSentence.id,
                    date = System.currentTimeMillis(),
                    accuracy = bestAcc,
                    transcription = bestMatch
                )
            )
        }

        if (bestAcc >= accuracyThreshold.value) {
            updateProgress(10, bestAcc)
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500)
                nextSentence()
            }
        }
    }

    fun speakRecorded() {
        audioRecorder.playLastRecording()
    }

    fun addCustomSentences(text: String, language: String, category: String = "General") {
        viewModelScope.launch {
            // Updated split: First split by newline, then by sentence punctuation for robustness
            val lines = text.split("\n", "\r").flatMap { 
                val sentenceRegex = "(?<=[.!?])\\s*".toRegex()
                it.split(sentenceRegex)
            }.filter { it.isNotBlank() && it.any { char -> char.isLetterOrDigit() } }
            
            val newSentences = lines.map { line ->
                val translation = try {
                    translationManager.translate(line, language, _nativeLanguage.value)
                } catch (e: Exception) {
                    "..."
                }
                Sentence(text = line.trim(), targetLang = language, sourceLang = _nativeLanguage.value, translation = translation, category = category)
            }
            repository.insertSentences(newSentences)
        }
    }

    fun clearAllSentences() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun syncRemoteContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(feedback = "جاري مزامنة المحتوى... ⏳") }
            val result = RemoteContentService.syncContent(repository)
            if (result.isSuccess) {
                val count = result.getOrNull() ?: 0
                _uiState.update { it.copy(feedback = "Success: $count sentences.") }
                loadSentences() // Refresh the UI
            } else {
                _uiState.update { it.copy(feedback = "Failed: ${result.exceptionOrNull()?.message}") }
            }
        }
    }

    fun importRemoteLesson(url: String, context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(feedback = "جاري تحميل الدرس... ⏳") }
            val result = LessonLoader.fetchRemoteLesson(context, repository, url)
            
            if (result.isSuccess) {
                val lessonTitle = result.getOrNull() ?: ""
                _uiState.update { it.copy(feedback = "تم استيراد $lessonTitle بنجاح. جاري تحميل الصوتيات... 🎧") }
                
                // Automation: Fetch sentences of this lesson and download audio
                val allSentences = repository.allSentences.first()
                val newLessonSentences = allSentences.filter { it.category == lessonTitle }
                
                var successCount = 0
                newLessonSentences.forEach { sentence ->
                    if (LessonLoader.downloadAudio(context, sentence, repository)) {
                        successCount++
                    }
                }
                
                _uiState.update { it.copy(feedback = "مكتمل! تم تحميل $successCount ملف صوتي للدرس: $lessonTitle ✅") }
                loadSentences()
            } else {
                _uiState.update { it.copy(feedback = "خطأ في التحميل: ${result.exceptionOrNull()?.message} ❌") }
            }
        }
    }

    suspend fun fetchCloudCatalog(): List<LessonMetadata> {
        // الرابط العالمي النهائي المستقر
        val catalogUrl = "https://jsonblob.com/api/jsonBlob/019d9864-1f9e-7334-9063-075468551478" 
        return LessonLoader.fetchCatalog(catalogUrl)
    }

    fun speakText(text: String, lang: String = "es") {
        ttsManager.speak(text, lang)
    }

    private fun updateProgress(amount: Int, accuracy: Float = 0f) {
        viewModelScope.launch {
            val current = userProgress.value ?: UserProgress()
            val newXp = current.xp + amount
            val newHighest = if (accuracy > current.highestAccuracy) accuracy else current.highestAccuracy
            
            // Streak Logic: Increment if it's been > 12h but < 36h since last active
            val lastActive = current.lastActiveDate
            val now = System.currentTimeMillis()
            val hoursDiff = (now - lastActive) / (1000 * 60 * 60)
            
            val newStreak = when {
                hoursDiff in 12..36 -> current.streak + 1
                hoursDiff > 36 -> 1
                current.streak == 0 -> 1
                else -> current.streak
            }
            
            repository.updateUserProgress(current.copy(
                xp = newXp,
                streak = newStreak,
                lastActiveDate = now,
                highestAccuracy = newHighest,
                sessionCount = current.sessionCount + (if (amount > 10) 1 else 0)
            ))
        }
    }

    fun nextSentence() {
        _uiState.update { 
            val nextIndex = (it.currentIndex + 1) % it.sentences.size
            // If we wrap around, reshuffle if in quiz mode
            val finalSentences = if (nextIndex == 0 && _isQuizMode.value) it.sentences.shuffled() else it.sentences
            it.copy(sentences = finalSentences, currentIndex = nextIndex, lastAccuracy = 0f, feedback = "")
        }
    }

    fun previousSentence() {
        _uiState.update { 
            val prevIndex = if (it.currentIndex > 0) it.currentIndex - 1 else it.sentences.size - 1
            it.copy(currentIndex = prevIndex, lastAccuracy = 0f, feedback = "")
        }
    }

    fun dismissFeedback() {
        _uiState.update { it.copy(feedback = "") }
    }

    fun setThreshold(value: Float) {
        _accuracyThreshold.value = value
    }


    fun backup() = backupManager.backupProgress(userProgress.value ?: UserProgress())

    fun restore() {
        viewModelScope.launch {
            backupManager.restoreProgress()?.let {
                repository.updateUserProgress(it)
            }
        }
    }
}

data class CategoryProgress(
    val rawName: String,
    val cleanName: String,
    val levelPrefix: String,
    val isMastered: Boolean,
    val progressPercent: Int
)
