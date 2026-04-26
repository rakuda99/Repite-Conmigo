package com.repite.conmigo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.repite.conmigo.data.*
import com.repite.conmigo.logic.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.content.Context

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
            
            val level = if (catName.contains(" - ")) catName.split(" - ")[0] else if (catName.startsWith("Pingo:")) "دروس أساسية" else "دروسي الخاصة ✍️"
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
        // Ensure Pingo Lessons are always there
        viewModelScope.launch {
            if (repository.getSentenceCountByCategory("Pingo: Alphabet") == 0) {
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
                    it.language == lang && 
                    if (cat != null) {
                        it.category == cat
                    } else {
                        it.contentType == type
                    }
                }
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
        val defaultSentences = listOf(
            // --- PINGO LESSON 1: LETTERS ---
            Sentence(text = "A", language = "es", translation = "أ (آه)", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "B", language = "es", translation = "باء (بيه)", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "C", language = "es", translation = "سيه/كيه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "D", language = "es", translation = "دال (ديه)", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "E", language = "es", translation = "إي", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "F", language = "es", translation = "إفِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "G", language = "es", translation = "خيه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "H", language = "es", translation = "آتشِه (لا تُنطق)", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "I", language = "es", translation = "إي", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "J", language = "es", translation = "خوتا", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "K", language = "es", translation = "كا", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "L", language = "es", translation = "إلِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "M", language = "es", translation = "إمِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "N", language = "es", translation = "إنِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "Ñ", language = "es", translation = "إنيِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "O", language = "es", translation = "أو", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "P", language = "es", translation = "بيه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "Q", language = "es", translation = "كو", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "R", language = "es", translation = "إيرِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "S", language = "es", translation = "إسِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "T", language = "es", translation = "تيه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "U", language = "es", translation = "أو", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "V", language = "es", translation = "أوبِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "W", language = "es", translation = "أوبِه دوبلِه", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "X", language = "es", translation = "إكيس", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "Y", language = "es", translation = "إي غرييغا", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            Sentence(text = "Z", language = "es", translation = "ثيتا", translationMapping = "0:0", category = "Letters (الحروف)", contentType = "word"),
            
            // --- COMPOUND SOUNDS ---
            Sentence(text = "ch", language = "es", translation = "تْشِه", translationMapping = "0:0", category = "Sounds (الأصوات)", contentType = "word"),
            Sentence(text = "ll", language = "es", translation = "إيِّه / يا", translationMapping = "0:0", category = "Sounds (الأصوات)", contentType = "word"),
            Sentence(text = "rr", language = "es", translation = "إرِّه (مشددة)", translationMapping = "0:0", category = "Sounds (الأصوات)", contentType = "word"),
            Sentence(text = "gü", language = "es", translation = "غوֵ (تُنطق الواو)", translationMapping = "0:0", category = "Sounds (الأصوات)", contentType = "word"),

            // --- INTERMEDIATE: COMMON WORDS (Pingo: Alphabet equivalent) ---
            Sentence(text = "Desayuno", language = "es", translation = "فطور", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Escuela", language = "es", translation = "مدرسة", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Familia", language = "es", translation = "عائلة", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Gracias", language = "es", translation = "شكراً", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Hermano", language = "es", translation = "أخ", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Trabajo", language = "es", translation = "عمل", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Viaje", language = "es", translation = "رحلة", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Zapatos", language = "es", translation = "أحذية", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Dinero", language = "es", translation = "نقود", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),
            Sentence(text = "Tiempo", language = "es", translation = "وقت", translationMapping = "0:0", category = "Pingo: Alphabet", contentType = "word"),

            // --- INTERMEDIATE: IRREGULAR WORDS ---
            Sentence(text = "Conseguir", language = "es", translation = "يحصل على", translationMapping = "0:0,1", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Empezar", language = "es", translation = "يبدأ", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Encontrar", language = "es", translation = "يجد", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Entender", language = "es", translation = "يفهم", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Pensar", language = "es", translation = "يفكر", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Perder", language = "es", translation = "يخسر", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Poder", language = "es", translation = "يستطيع", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Querer", language = "es", translation = "يريد", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Sentir", language = "es", translation = "يشعر", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),
            Sentence(text = "Tener", language = "es", translation = "يملك", translationMapping = "0:0", category = "Irregular Words", contentType = "word"),

            // --- ADVANCED: SER vs ESTAR ---
            Sentence(text = "El cielo está nublado hoy", language = "es", translation = "السماء غائمة اليوم", translationMapping = "0:0,1:0,2:1,3:2,4:3", category = "Pingo: Ser y Estar", contentType = "sentence"),
            Sentence(text = "Mi madre es enfermera", language = "es", translation = "أمي ممرضة", translationMapping = "0:0,1:0,2:0,3:1", category = "Pingo: Ser y Estar", contentType = "sentence"),
            Sentence(text = "Ese carro es muy rápido", language = "es", translation = "تلك السيارة سريعة جداً", translationMapping = "0:0,1:1,2:1,3:3,4:2", category = "Pingo: Ser y Estar", contentType = "sentence"),
            Sentence(text = "Estamos muy ocupados ahora", language = "es", translation = "نحن مشغولون جداً الآن", translationMapping = "0:0,1:2,2:1,3:3", category = "Pingo: Ser y Estar", contentType = "sentence"),
            Sentence(text = "La fiesta es en mi casa", language = "es", translation = "الحفلة في منزلي", translationMapping = "0:0,1:0,2:1,3:1,4:2,5:2", category = "Pingo: Ser y Estar", contentType = "sentence"),

            // --- ADVANCED: REGULAR VERBS (Verbos) ---
            Sentence(text = "Siempre aprendo algo nuevo", language = "es", translation = "دائماً أتعلم شيئاً جديداً", translationMapping = "0:0,1:1,2:2,3:3", category = "Pingo: Verbos", contentType = "sentence"),
            Sentence(text = "Trabajamos todos los días", language = "es", translation = "نعمل كل يوم", translationMapping = "0:0,1:1,2:2,3:2", category = "Pingo: Verbos", contentType = "sentence"),
            Sentence(text = "Ellos comprenden la lección", language = "es", translation = "هم يفهمون الدرس", translationMapping = "0:0,1:1,2:2,3:2", category = "Pingo: Verbos", contentType = "sentence"),

            // --- PINGO PASSAGES (Advanced Mode) ---
            Sentence(
                text = "La persistencia es el camino del éxito. Nunca te rindas ante los obstáculos.",
                language = "es", 
                translation = "الإصرار هو طريق النجاح. لا تستسلم أبداً أمام العقبات.", 
                translationMapping = "0:0,1:0,2:1,3:2,4:3,5:3,6:4,7:6,8:5,9:5,10:7,11:8,12:8",
                category = "Pingo: Advanced", 
                contentType = "passage"
            ),
            Sentence(
                text = "Me encanta pasear por la playa al atardecer y escuchar el sonido de las olas.",
                language = "es", 
                translation = "أعشق التنزه على الشاطئ عند الغروب والاستماع إلى صوت الأمواج.", 
                translationMapping = "0:0,1:0,2:1,3:2,4:2,5:3,6:4,7:5,8:6,9:7,10:8,11:8,12:9,13:10,14:10",
                category = "Pingo: Advanced", 
                contentType = "passage"
            )
        )
        repository.insertSentences(defaultSentences)
    }

    fun loadPingoDataIfRequested(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.getSentenceCountByCategory("Grammar Level \uD83D\uDCD8 - الحروف والنطق")
            // If the first lesson isn't found, try to load it from assets
            if (count == 0) {
                LessonLoader.loadPingoData(context, repository)
            }
            
            // Force seed our new letters if they don't exist yet
            if (repository.getSentenceCountByCategory("Letters (الحروف)") == 0) {
                seedData()
            }
        }
    }


            







    fun speakCurrent() {
        val state = uiState.value
        if (state.sentences.isNotEmpty()) {
            val sentence = state.sentences[state.currentIndex]
            ttsManager.speak(sentence.text, sentence.language, speechRate.value)
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
            val lang = if (isInChat) _learningLanguage.value else (uiState.value.sentences.getOrNull(uiState.value.currentIndex)?.language ?: "es")
            
            _uiState.update { it.copy(isRecording = true, feedback = if (isInChat) "Escuchando محادثة..." else "Escuchando...") }
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
            "¡Entendido! Has dicho: \"$text\". نطقك رائع جداً. Sigamos hablando."
        } else {
            "I heard: \"$text\". Excellent pronunciation. Let's keep talking."
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
        val lang = uiState.value.sentences.getOrNull(uiState.value.currentIndex)?.language ?: "es"
        ttsManager.speak(word, lang, speechRate.value)
    }

    fun analyzeSpokenText(spokenText: String) {
        _uiState.update { it.copy(isRecording = false) } // Force Reset
        
        if (spokenText.startsWith("ERR: ")) {
            _uiState.update { it.copy(feedback = spokenText.removePrefix("ERR: ")) }
            return
        }

        if (spokenText.isBlank()) {
            _uiState.update { it.copy(feedback = "لم أسمع شيئاً، حاول التحدث بصوت أوضح وقريب من الميكروفون.. 🎤", lastAccuracy = 0f, lastTranscription = "") }
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
            feedback = if (bestAcc >= accuracyThreshold.value && bestAcc > 0) "نطق ممتاز ومثالي! ✨" else if (bestAcc == 0f) "لا يمكنني سماع جملة واضحة.. حاول التحدث بصوت أعلى 🎙️" else finalFeedback,
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
            // Regex to split by punctuation (. ! ?) but keep the punctuation attached to the sentence
            val sentenceRegex = "(?<=[.!?])\\s*".toRegex()
            val lines = text.split(sentenceRegex).filter { it.isNotBlank() && it.any { char -> char.isLetterOrDigit() } }
            
            val newSentences = lines.map { line ->
                val translation = try {
                    translationManager.translate(line, language)
                } catch (e: Exception) {
                    "..."
                }
                Sentence(text = line.trim(), language = language, translation = translation, category = category)
            }
            repository.insertSentences(newSentences)
        }
    }

    fun clearAllSentences() {
        viewModelScope.launch {
            repository.clearAll()
        }
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
            it.copy(currentIndex = nextIndex, lastAccuracy = 0f, feedback = "")
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

    fun setLanguage(lang: String) {
        _learningLanguage.value = lang
        // Filter sentences or reload
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
