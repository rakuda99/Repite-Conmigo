package com.repite.conmigo.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.repite.conmigo.data.*
import com.repite.conmigo.logic.*
import com.repite.conmigo.models.Lesson
import com.repite.conmigo.models.Verb
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.util.Log

class LessonViewModel(
    private val context: android.content.Context,
    private val repository: LessonRepository,
    val ttsManager: TTSManager,
    private val translationManager: TranslationManager,
    val sttManager: SpeechToTextManager,
    private val audioRecorder: AudioRecorder,
    private val backupManager: BackupManager,
    private val sharedPreferences: android.content.SharedPreferences,
    private val authService: AuthService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonState())
    val uiState: StateFlow<LessonState> = _uiState.asStateFlow()

    private val _cloudCatalog = MutableStateFlow<List<LessonMetadata>>(emptyList())
    val cloudCatalog: StateFlow<List<LessonMetadata>> = _cloudCatalog.asStateFlow()

    val rmsDb: StateFlow<Float> = sttManager.rmsDb
    val partialText: StateFlow<String> = sttManager.partialText

    val learningRecords = repository.allRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> = _selectedWord.asStateFlow()

    private val _selectedWordTranslation = MutableStateFlow<String?>(null)
    val selectedWordTranslation: StateFlow<String?> = _selectedWordTranslation.asStateFlow()

    private val _accuracyThreshold = MutableStateFlow(70f)
    val accuracyThreshold: StateFlow<Float> = _accuracyThreshold.asStateFlow()

    private val _learningLanguage = MutableStateFlow("es")
    val learningLanguage: StateFlow<String> = _learningLanguage.asStateFlow()

    private val _nativeLanguage = MutableStateFlow(sharedPreferences.getString("native_language", "ar") ?: "ar")
    val nativeLanguage: StateFlow<String> = _nativeLanguage.asStateFlow()

    private val _useSystemSpeechDialog = MutableStateFlow(sharedPreferences.getBoolean("use_system_speech", false))
    val useSystemSpeechDialog: StateFlow<Boolean> = _useSystemSpeechDialog.asStateFlow()

    fun setUseSystemSpeechDialog(useSystem: Boolean) {
        _useSystemSpeechDialog.value = useSystem
        sharedPreferences.edit().putBoolean("use_system_speech", useSystem).apply()
    }

    private val _cloudSyncKey = MutableStateFlow(sharedPreferences.getString("cloud_sync_key", "") ?: "")
    val cloudSyncKey: StateFlow<String> = _cloudSyncKey.asStateFlow()

    fun setCloudSyncKey(key: String) {
        _cloudSyncKey.value = key
        sharedPreferences.edit().putString("cloud_sync_key", key).apply()
    }

    private val defaultApiKey = ""
    private val _geminiApiKey = MutableStateFlow(
        sharedPreferences.getString("gemini_api_key_v2", defaultApiKey)?.takeIf { it.isNotBlank() } ?: defaultApiKey
    )
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _isGeneratingStory = MutableStateFlow(false)
    val isGeneratingStory: StateFlow<Boolean> = _isGeneratingStory.asStateFlow()

    private val _generatingExamplesProgress = MutableStateFlow<String?>(null)
    val generatingExamplesProgress: StateFlow<String?> = _generatingExamplesProgress.asStateFlow()

    private val _isGeneratingVerbs = MutableStateFlow(false)
    val isGeneratingVerbs: StateFlow<Boolean> = _isGeneratingVerbs.asStateFlow()

    private val _generatingVerbsProgress = MutableStateFlow<String?>(null)
    val generatingVerbsProgress: StateFlow<String?> = _generatingVerbsProgress.asStateFlow()

    fun setGeminiApiKey(key: String) {
        _geminiApiKey.value = key
        sharedPreferences.edit().putString("gemini_api_key_v2", key).apply()
    }

    private val _favoriteDecks = MutableStateFlow<Set<String>>(
        (sharedPreferences.getString("anki_decks_favorites", "") ?: "")
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
    )
    val favoriteDecks: StateFlow<Set<String>> = _favoriteDecks.asStateFlow()

    private val _verbs = MutableStateFlow<List<Verb>>(emptyList())
    val verbs: StateFlow<List<Verb>> = _verbs.asStateFlow()

    init {
        loadVerbs()
    }

    private fun loadVerbs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Load base verbs from assets
                val jsonString = context.assets.open("verbs.json").bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Verb>>() {}.type
                val baseVerbs: List<Verb> = Gson().fromJson(jsonString, listType)
                
                // Load custom verbs from internal storage
                val customVerbsFile = java.io.File(context.filesDir, "custom_verbs.json")
                val customVerbs = if (customVerbsFile.exists()) {
                    val customJsonString = customVerbsFile.readText()
                    Gson().fromJson<List<Verb>>(customJsonString, listType) ?: emptyList()
                } else {
                    emptyList()
                }
                
                _verbs.value = baseVerbs + customVerbs
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCustomVerb(
        infinitive: String,
        meaning: String,
        past: String,
        pastMeaning: String,
        present: String,
        presentMeaning: String,
        future: String,
        futureMeaning: String
    ) {
        // Keeping this for backwards compatibility or manual fallback
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentVerbs = _verbs.value
                val newId = if (currentVerbs.isEmpty()) 1 else currentVerbs.maxOf { it.id } + 1
                
                // Formatted with red dot automatically
                val formattedInfinitive = if (!infinitive.contains("🔴")) "$infinitive 🔴" else infinitive
                val formattedPast = if (!past.contains("🔴")) "$past 🔴" else past
                val formattedPresent = if (!present.contains("🔴")) "$present 🔴" else present
                val formattedFuture = if (!future.contains("🔴")) "$future 🔴" else future
                
                val newVerb = Verb(
                    id = newId,
                    infinitive = formattedInfinitive,
                    meaning = meaning,
                    past = formattedPast,
                    past_meaning = pastMeaning,
                    present = formattedPresent,
                    present_meaning = presentMeaning,
                    future = formattedFuture,
                    future_meaning = futureMeaning
                )

                saveAndDispatchNewVerb(newVerb, currentVerbs)
                
                val verbsToAnki = listOf(
                    Sentence(text = formattedInfinitive, translation = meaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null),
                    Sentence(text = formattedPast, translation = pastMeaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null),
                    Sentence(text = formattedPresent, translation = presentMeaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null),
                    Sentence(text = formattedFuture, translation = futureMeaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null)
                )
                repository.insertSentences(verbsToAnki)
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveAndDispatchNewVerb(newVerb: Verb, currentVerbs: List<Verb>) {
        val customVerbsFile = java.io.File(context.filesDir, "custom_verbs.json")
        val listType = object : TypeToken<List<Verb>>() {}.type
        val existingCustomVerbs = if (customVerbsFile.exists()) {
            Gson().fromJson<List<Verb>>(customVerbsFile.readText(), listType) ?: emptyList()
        } else {
            emptyList()
        }
        val updatedCustomVerbs = existingCustomVerbs + newVerb
        customVerbsFile.writeText(Gson().toJson(updatedCustomVerbs))
        
        withContext(Dispatchers.Main) {
            _verbs.value = currentVerbs + newVerb
        }
    }

    fun generateAndAddVerbsBatch(verbsText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isGeneratingVerbs.value = true
            try {
                // Split by commas, newlines, or spaces, and clean up
                val inputVerbs = verbsText.split(Regex("[,\n]+"))
                    .map { it.trim().lowercase() }
                    .filter { it.isNotBlank() }
                    .distinct()

                if (inputVerbs.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "الرجاء إدخال أفعال صحيحة", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Get current verbs to check for duplicates
                val currentVerbs = _verbs.value
                val existingInfinitives = currentVerbs.map { 
                    it.infinitive.replace("🔴", "").trim().lowercase() 
                }.toSet()

                val verbsToProcess = inputVerbs.filter { !existingInfinitives.contains(it) }
                val duplicateCount = inputVerbs.size - verbsToProcess.size

                if (verbsToProcess.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "جميع الأفعال المدخلة موجودة مسبقاً! ($duplicateCount تكرار)", android.widget.Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val generator = SentenceGenerator()
                var successCount = 0

                for ((index, verbStr) in verbsToProcess.withIndex()) {
                    _generatingVerbsProgress.value = "جاري معالجة الفعل (${index + 1}/${verbsToProcess.size}): $verbStr..."
                    
                    val result = generator.generateVerbDetails(
                        apiKey = _geminiApiKey.value,
                        verb = verbStr,
                        learningLanguage = _learningLanguage.value,
                        nativeLanguage = _nativeLanguage.value
                    )

                    if (result.isSuccess) {
                        val details = result.getOrNull()
                        if (details != null) {
                            val updatedVerbsList = _verbs.value
                            val newId = if (updatedVerbsList.isEmpty()) 1 else updatedVerbsList.maxOf { it.id } + 1
                            
                            val fInfinitive = "$verbStr 🔴"
                            val fPast = "${details.past} 🔴"
                            val fPresent = "${details.present} 🔴"
                            val fFuture = "${details.future} 🔴"

                            val newVerb = Verb(
                                id = newId,
                                infinitive = fInfinitive,
                                meaning = details.infinitive_meaning,
                                past = fPast,
                                past_meaning = details.past_meaning,
                                present = fPresent,
                                present_meaning = details.present_meaning,
                                future = fFuture,
                                future_meaning = details.future_meaning
                            )

                            saveAndDispatchNewVerb(newVerb, updatedVerbsList)

                            // Add words and sentences to Anki
                            val ankiItems = mutableListOf<Sentence>()
                            // 4 Words
                            ankiItems.add(Sentence(text = fInfinitive, translation = details.infinitive_meaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null))
                            ankiItems.add(Sentence(text = fPast, translation = details.past_meaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null))
                            ankiItems.add(Sentence(text = fPresent, translation = details.present_meaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null))
                            ankiItems.add(Sentence(text = fFuture, translation = details.future_meaning, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "word", imageUrl = null))
                            
                            // 4 Sentences
                            ankiItems.add(Sentence(text = details.sentence_infinitive, translation = details.trans_infinitive, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "sentence", imageUrl = null))
                            ankiItems.add(Sentence(text = details.sentence_past, translation = details.trans_past, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "sentence", imageUrl = null))
                            ankiItems.add(Sentence(text = details.sentence_present, translation = details.trans_present, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "sentence", imageUrl = null))
                            ankiItems.add(Sentence(text = details.sentence_future, translation = details.trans_future, targetLang = "es", sourceLang = "ar", category = "الأفعال المخصصة", contentType = "sentence", imageUrl = null))

                            repository.insertSentences(ankiItems)
                            successCount++
                        }
                    }
                    
                    // Small delay to prevent hitting API rate limits
                    delay(500)
                }

                withContext(Dispatchers.Main) {
                    val dupMsg = if (duplicateCount > 0) "\nتم تجاهل $duplicateCount فعل متكرر." else ""
                    android.widget.Toast.makeText(context, "تم بنجاح إضافة $successCount أفعال جديدة! ✨$dupMsg", android.widget.Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "حدث خطأ أثناء المعالجة", android.widget.Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            } finally {
                _isGeneratingVerbs.value = false
                _generatingVerbsProgress.value = null
            }
        }
    }

    fun toggleFavoriteDeck(deckName: String) {
        val current = _favoriteDecks.value
        val updated = if (current.contains(deckName)) {
            current - deckName
        } else {
            current + deckName
        }
        _favoriteDecks.value = updated
        sharedPreferences.edit().putString("anki_decks_favorites", updated.joinToString(",")).apply()
        
        // Auto backup if cloud sync key is set
        val syncKeyVal = _cloudSyncKey.value
        if (syncKeyVal.isNotBlank()) {
            backupUserData(syncKeyVal)
        }
    }

    fun setLanguage(lang: String) {
        _learningLanguage.value = lang
        loadSentences()
    }

    fun setNativeLanguage(lang: String) {
        _nativeLanguage.value = lang
        sharedPreferences.edit().putString("native_language", lang).apply()
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
                
                val sentencesToSync = all
                var translatedCount = 0
                val total = sentencesToSync.size
                
                sentencesToSync.chunked(50).forEach { chunk ->
                    val updatedSentences = chunk.map { sentence ->
                        val translation = translationManager.translate(sentence.text, sentence.targetLang, target)
                        sentence.copy(translation = translation, sourceLang = target)
                    }
                    repository.insertSentences(updatedSentences)
                    translatedCount += chunk.size
                    _uiState.update { it.copy(feedback = "Translating $translatedCount / $total to $target... ⏳") }
                }
                
                _uiState.update { it.copy(isSyncing = false, feedback = "All $total translations updated to ${target.uppercase()}! ✅") }
                loadSentences() 
            } catch (e: Exception) {
                _uiState.update { it.copy(isSyncing = false, feedback = "Translation failed.") }
            }
        }
    }

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedContentType = MutableStateFlow("sentence") // "word", "sentence", "passage"
    val selectedContentType: StateFlow<String> = _selectedContentType.asStateFlow()

    private val _practiceMemorizedMode = MutableStateFlow(false)
    val practiceMemorizedMode: StateFlow<Boolean> = _practiceMemorizedMode.asStateFlow()

    private val _customNumbersFrom = MutableStateFlow(1)
    val customNumbersFrom: StateFlow<Int> = _customNumbersFrom.asStateFlow()

    private val _customNumbersTo = MutableStateFlow(10)
    val customNumbersTo: StateFlow<Int> = _customNumbersTo.asStateFlow()

    val categories = repository.categories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allSentences = repository.allSentences.stateIn(
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

    private val _audioRepetitions = MutableStateFlow(sharedPreferences.getInt("audio_repetitions", 1))
    val audioRepetitions: StateFlow<Int> = _audioRepetitions.asStateFlow()

    private val _currentAudioPlayIndex = MutableStateFlow(0)
    val currentAudioPlayIndex: StateFlow<Int> = _currentAudioPlayIndex.asStateFlow()

    fun updateAudioRepetitions(reps: Int) {
        _audioRepetitions.value = reps
        sharedPreferences.edit().putInt("audio_repetitions", reps).apply()
    }

    private val _matchingQuizSize = MutableStateFlow(sharedPreferences.getInt("matching_quiz_size", 5))
    val matchingQuizSize: StateFlow<Int> = _matchingQuizSize.asStateFlow()

    private val _sessionSize = MutableStateFlow(sharedPreferences.getInt("session_size", 10))
    val sessionSize: StateFlow<Int> = _sessionSize.asStateFlow()

    fun setMatchingQuizSize(size: Int) {
        _matchingQuizSize.value = size
        sharedPreferences.edit().putInt("matching_quiz_size", size).apply()
    }

    fun setSessionSize(size: Int) {
        _sessionSize.value = size
        sharedPreferences.edit().putInt("session_size", size).apply()
        loadSentences()
    }

    private var speakJob: Job? = null

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
        refreshCloudCatalog()
        
        // One-time database migration to fix swapped contentTypes and update single-word cards
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allSentences = repository.allSentences.first()
                val sentencesToUpdate = allSentences.mapNotNull { sentence ->
                    var updated = sentence
                    var isModified = false
                    // The content type migration has been removed so it doesn't overwrite user custom decks.
                    
                    // Correct single-word cards in sentence categories to full sentences
                    if (updated.category == "Greetings & Basics") {
                        val replacement = when (updated.text.trim()) {
                            "Hola" -> "Hola, ¿qué tal?" to "مرحباً، ما الأخبار؟"
                            "Adiós" -> "Adiós, nos vemos." to "وداعاً، نلتقي لاحقاً."
                            "Gracias" -> "Muchas gracias." to "شكراً جزيلاً."
                            "Sí" -> "Sí, de acuerdo." to "نعم، أنا موافق."
                            "No" -> "No, lo siento." to "لا، أنا آسف."
                            "Bien" -> "Todo bien, gracias." to "كل شيء على ما يرام، شكراً."
                            "Mal" -> "No está mal." to "ليس سيئاً."
                            else -> null
                        }
                        if (replacement != null) {
                            updated = updated.copy(text = replacement.first, translation = replacement.second)
                            isModified = true
                        }
                    } else if (updated.category == "Asking Directions") {
                        val replacement = when (updated.text.trim()) {
                            "Derecha" -> "Gira a la derecha." to "انعطف يميناً."
                            "Izquierda" -> "Gira a la izquierda." to "انعطف يساراً."
                            else -> null
                        }
                        if (replacement != null) {
                            updated = updated.copy(text = replacement.first, translation = replacement.second)
                            isModified = true
                        }
                    }
                    
                    if (isModified) updated else null
                }
                if (sentencesToUpdate.isNotEmpty()) {
                    repository.insertSentences(sentencesToUpdate)
                    loadSentences()
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Failed to migrate content types and single words", e)
            }
            
            try {
                // Auto restore and sync if sync key is set
                val syncKeyVal = _cloudSyncKey.value
                if (syncKeyVal.isNotBlank()) {
                    // 1. Silent restore of user progress/favorites on startup
                    val doc = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("user_sync").document(syncKeyVal).get().await()
                    if (doc.exists()) {
                        val xp = doc.getLong("xp")?.toInt() ?: 0
                        val streak = doc.getLong("streak")?.toInt() ?: 0
                        val lastActiveDate = doc.getLong("lastActiveDate") ?: System.currentTimeMillis()
                        val highestAccuracy = doc.getDouble("highestAccuracy")?.toFloat() ?: 0f
                        val sessionCount = doc.getLong("sessionCount")?.toInt() ?: 0
                        val totalWordsLearned = doc.getLong("totalWordsLearned")?.toInt() ?: 0
                        val favList = doc.get("favoriteDecks") as? List<String> ?: emptyList()
                        
                        val progress = UserProgress(
                            xp = xp,
                            streak = streak,
                            lastActiveDate = lastActiveDate,
                            highestAccuracy = highestAccuracy,
                            sessionCount = sessionCount,
                            totalWordsLearned = totalWordsLearned
                        )
                        repository.updateUserProgress(progress)
                        
                        val favoritesSet = favList.toSet()
                        _favoriteDecks.value = favoritesSet
                        sharedPreferences.edit().putString("anki_decks_favorites", favoritesSet.joinToString(",")).apply()
                        Log.d("LessonViewModel", "Auto restored user data on startup")
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.e("LessonViewModel", "Auto restore failed", e)
            }
            
            try {
                // 3. Inject missing default sentences that were added to lessons.json
                val contentService = ContentService(context)
                val defaultLessons = contentService.loadLessonsFromAssets()
                val existingTexts = repository.allSentences.first().map { it.text.trim() }.toSet()
                val sentencesToInsert = mutableListOf<Sentence>()
                
                defaultLessons.forEach { lesson ->
                    lesson.content.forEach { sentence ->
                        if (!existingTexts.contains(sentence.text.trim())) {
                            sentencesToInsert.add(sentence)
                        }
                    }
                }
                
                if (sentencesToInsert.isNotEmpty()) {
                    repository.insertSentences(sentencesToInsert)
                    loadSentences()
                    Log.d("LessonViewModel", "Injected ${sentencesToInsert.size} missing default sentences")
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Failed to inject missing sentences", e)
            }
            
            // 4. Comprehensive bidirectional sync (sync lessons/sentences)
            val syncKeyValForSync = _cloudSyncKey.value
            if (syncKeyValForSync.isNotBlank()) {
                syncAll(context, isManualTrigger = false)
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

    private var isSeedingEnabled = true

    private fun loadSentences() {
        viewModelScope.launch {
            val customParamsFlow = combine(
                _selectedCategory,
                _customNumbersFrom,
                _customNumbersTo
            ) { cat, from, to -> Triple(cat, from, to) }

            combine(
                repository.allSentences, 
                _learningLanguage, 
                _selectedContentType,
                _practiceMemorizedMode,
                customParamsFlow
            ) { all, lang, type, practiceMemorized, customParams ->
                val (cat, from, to) = customParams
                val userClearedEverything = sharedPreferences.getBoolean("user_cleared_all", false)
                if (all.isEmpty() && isSeedingEnabled && !userClearedEverything) seedData()
                
                var filtered = if (cat == "CustomNumbers") {
                    com.repite.conmigo.logic.NumberLessonGenerator.generateCustomNumbers(from, to, lang)
                } else {
                    all.filter { 
                        it.targetLang == lang && 
                        if (cat != null) {
                            it.category == cat
                        } else {
                            it.contentType == type
                        }
                    }.distinctBy { it.text.trim().lowercase() }
                }
                
                if (practiceMemorized) {
                    // Only include words/sentences the user has practiced before
                    val records = repository.allRecords.first()
                    val practicedIds = records.map { it.sentenceId }.toSet()
                    val memorizedFiltered = filtered.filter { practicedIds.contains(it.id) }
                    
                    // Fallback to basic beginner sentences if they haven't practiced enough yet
                    if (memorizedFiltered.size < 5) {
                        filtered = filtered.filter { 
                            it.category == "The Alphabet" || 
                            it.category == "Numbers 1-100" || 
                            it.category == "CustomNumbers" ||
                            it.category == "Greetings & Basics" 
                        }.ifEmpty { filtered }
                    } else {
                        filtered = memorizedFiltered
                    }
                }
                
                filtered
            }.collect { filtered ->
                val finalSentences = if (_isQuizMode.value) filtered.shuffled() else filtered
                val limit = if (_selectedCategory.value == "CustomNumbers") filtered.size else sessionSize.value
                val limitedSentences = finalSentences.take(limit)
                _uiState.update { it.copy(sentences = limitedSentences, currentIndex = 0) }
            }
        }
    }

    fun setQuizMode(enabled: Boolean) {
        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()
        _isQuizMode.value = enabled
        if (!enabled) _practiceMemorizedMode.value = false // Reset practice mode when leaving quiz
        loadSentences()
    }

    fun setPracticeMemorizedMode(enabled: Boolean) {
        _practiceMemorizedMode.value = enabled
    }

    fun selectCategory(cat: String?) {
        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()
        _selectedCategory.value = cat
    }

    fun selectCustomNumbers(from: Int, to: Int) {
        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()
        _customNumbersFrom.value = from
        _customNumbersTo.value = to
        _selectedCategory.value = "CustomNumbers"
    }

    fun selectContentType(type: String) {
        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()
        _selectedContentType.value = type
    }

    fun seedData() {
        // Disabled dummy seed data
    }

    fun loadGlobalLessons(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val contentService = ContentService(context, repository)
            val lessons = contentService.getLessons()
            // Clean up old category name to prevent orphaned deck
            repository.deleteByCategory("Numbers 1-10")
            lessons.forEach { lesson ->
                if (lesson.type == "asset") {
                    repository.deleteByCategory(lesson.categoryId)
                    repository.insertSentences(lesson.content)
                }
            }
            // Auto merge specific decks and clean duplicates
            mergeDecksAndCleanDuplicates()
        }
    }

    suspend fun mergeDecksAndCleanDuplicates() {
        try {
            val all = repository.allSentences.first()
            val targetCategory = "الجمل المهمه لطالب اللغه"
            val sourceCategory = "اهم الجمل للطالب"
            
            // 1. Rename category of sentences from sourceCategory to targetCategory
            val toUpdate = all.filter { it.category.trim() == sourceCategory }
            if (toUpdate.isNotEmpty()) {
                val updated = toUpdate.map { it.copy(category = targetCategory) }
                repository.insertSentences(updated)
            }
            
            // 2. Load sentences again and clean up duplicates in targetCategory
            val allAfterMerge = repository.allSentences.first()
            val targetSentences = allAfterMerge.filter { it.category.trim() == targetCategory }
            val duplicatesToDelete = mutableListOf<Int>()
            val seenTexts = mutableSetOf<String>()
            
            // Sort to keep the best one (highest pronunciationScore, then highest difficulty rate, then smallest ID)
            val sorted = targetSentences.sortedWith(
                compareByDescending<Sentence> { it.pronunciationScore }
                    .thenByDescending { it.memorizationDifficulty }
                    .thenBy { it.id }
            )
            
            sorted.forEach { sentence ->
                val normalizedText = sentence.text.trim().lowercase().replace("[.,?!¿¡()]".toRegex(), "")
                if (seenTexts.contains(normalizedText)) {
                    duplicatesToDelete.add(sentence.id)
                } else {
                    seenTexts.add(normalizedText)
                }
            }
            
            if (duplicatesToDelete.isNotEmpty()) {
                repository.deleteSentencesByIds(duplicatesToDelete)
            }
            
            // Also delete the sourceCategory from local DB just in case
            repository.deleteByCategory(sourceCategory)
            
            // 3. Delete the source category from Firestore
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("lessons").document(sourceCategory).delete().await()
            db.collection("lessons").document(sourceCategory.trim()).delete().await()
            
            Log.d("LessonViewModel", "Merged '$sourceCategory' into '$targetCategory' and cleaned up ${duplicatesToDelete.size} duplicates.")
        } catch (e: Exception) {
            Log.e("LessonViewModel", "Merge decks failed", e)
        }
    }

    fun backupUserData(syncKey: String, onComplete: ((String) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val progress = userProgress.value ?: UserProgress()
                val progressMap = mapOf(
                    "xp" to progress.xp,
                    "streak" to progress.streak,
                    "lastActiveDate" to progress.lastActiveDate,
                    "highestAccuracy" to progress.highestAccuracy.toDouble(),
                    "sessionCount" to progress.sessionCount,
                    "totalWordsLearned" to progress.totalWordsLearned,
                    "favoriteDecks" to _favoriteDecks.value.toList()
                )
                db.collection("user_sync").document(syncKey).set(progressMap).await()
                onComplete?.invoke("تم رفع النسخة الاحتياطية بنجاح تحت الرمز '$syncKey' ✅")
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Backup failed", e)
                onComplete?.invoke("فشلت عملية النسخ الاحتياطي: ${e.localizedMessage}")
            }
        }
    }

    fun restoreUserData(syncKey: String, onComplete: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val doc = db.collection("user_sync").document(syncKey).get().await()
                if (doc.exists()) {
                    val xp = doc.getLong("xp")?.toInt() ?: 0
                    val streak = doc.getLong("streak")?.toInt() ?: 0
                    val lastActiveDate = doc.getLong("lastActiveDate") ?: 0L
                    val highestAccuracy = doc.getDouble("highestAccuracy")?.toFloat() ?: 0f
                    val sessionCount = doc.getLong("sessionCount")?.toInt() ?: 0
                    val totalWordsLearned = doc.getLong("totalWordsLearned")?.toInt() ?: 0

                    val progress = UserProgress(
                        xp = xp,
                        streak = streak,
                        lastActiveDate = lastActiveDate,
                        highestAccuracy = highestAccuracy,
                        sessionCount = sessionCount,
                        totalWordsLearned = totalWordsLearned
                    )
                    repository.updateUserProgress(progress)

                    val favorites = doc.get("favoriteDecks") as? List<*>
                    val favoritesSet = favorites?.mapNotNull { it?.toString() }?.toSet() ?: emptySet()
                    _favoriteDecks.value = favoritesSet
                    sharedPreferences.edit().putString("anki_decks_favorites", favoritesSet.joinToString(",")).apply()

                    onComplete("تم استعادة جميع بياناتك ومفضلتك بنجاح من السحابة تحت الرمز '$syncKey' ✅")
                } else {
                    onComplete("لم يتم العثور على أي بيانات سحابية للرمز '$syncKey' ❌")
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Restore failed", e)
                onComplete("فشلت عملية الاستعادة: ${e.localizedMessage}")
            }
        }
    }

    fun resetSeedingState() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().putBoolean("user_cleared_all", false).apply()
            repository.clearAll() // FULL WIPE
        }
    }

    fun speakCurrent() {
        val state = uiState.value
        if (state.sentences.isNotEmpty()) {
            val sentence = state.sentences[state.currentIndex]
            speakJob?.cancel()
            speakJob = viewModelScope.launch(Dispatchers.Main) {
                val repetitions = audioRepetitions.value
                for (i in 1..repetitions) {
                    _currentAudioPlayIndex.value = i
                    if (sentence.localAudioPath != null) {
                        audioRecorder.playFile(sentence.localAudioPath)
                        delay(2500)
                    } else {
                        ttsManager.speak(sentence.text, sentence.targetLang, speechRate.value)
                        var started = false
                        for (j in 1..10) {
                            if (ttsManager.isSpeaking.value) {
                                started = true
                                break
                            }
                            delay(50)
                        }
                        if (started) {
                            while (ttsManager.isSpeaking.value) {
                                delay(100)
                            }
                        } else {
                            delay(1500)
                        }
                    }
                    if (i < repetitions) {
                        delay(1200)
                    }
                }
                _currentAudioPlayIndex.value = 0
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
            _uiState.update { it.copy(isRecording = false) }
        } else {
            val lang = if (isInChat) _learningLanguage.value else (uiState.value.sentences.getOrNull(uiState.value.currentIndex)?.targetLang ?: "es")
            
            _uiState.update { it.copy(isRecording = true, feedback = "...") }
            
            sttManager.startListening(lang) { spokenText ->
                _uiState.update { it.copy(isRecording = false) }
                
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

    fun handleChatTranscription(text: String) {
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
        val cleanWord = word.trim().replace("[.,?!¿¡()\"]".toRegex(), "")
        _selectedWord.value = cleanWord
        _selectedWordTranslation.value = null // reset translation state

        viewModelScope.launch {
            val sentence = uiState.value.sentences.getOrNull(uiState.value.currentIndex)
            val lang = sentence?.targetLang ?: "es"
            val native = _nativeLanguage.value
            val translation = kotlinx.coroutines.withTimeoutOrNull(3000) {
                translationManager.translate(cleanWord, lang, native)
            }
            _selectedWordTranslation.value = if (translation == null || translation == "...") "ترجمة غير متوفرة" else translation
        }
    }

    fun dismissWordDialog() {
        _selectedWord.value = null
        _selectedWordTranslation.value = null
    }

    fun speakWord(word: String, slow: Boolean = false) {
        val lang = uiState.value.sentences.getOrNull(uiState.value.currentIndex)?.targetLang ?: "es"
        val speed = if (slow) 0.5f else 1.0f
        ttsManager.speak(word, lang, speed)
    }

    fun addCardToDeck(text: String, translation: String, category: String, contentType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sentence = Sentence(
                text = text,
                translation = translation,
                category = category,
                contentType = contentType,
                targetLang = if (contentType == "word") _learningLanguage.value else "es",
                sourceLang = _nativeLanguage.value
            )
            val sentencesToInsert = mutableListOf(sentence)
            repository.insertSentences(sentencesToInsert)
            
            if (contentType == "word") {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "جارٍ توليد 5 جمل للكلمة بواسطة الذكاء الاصطناعي...", android.widget.Toast.LENGTH_LONG).show()
                }
                val generator = com.repite.conmigo.logic.SentenceGenerator()
                val result = generator.generateSentences(
                    apiKey = _geminiApiKey.value,
                    word = text,
                    learningLanguage = _learningLanguage.value,
                    nativeLanguage = _nativeLanguage.value
                )
                
                if (result.isSuccess) {
                    val generated = result.getOrNull() ?: emptyList()
                    val newSentences = generated.map {
                        Sentence(
                            text = it.sentence,
                            translation = it.translation,
                            category = category,
                            contentType = "sentence",
                            targetLang = _learningLanguage.value,
                            sourceLang = _nativeLanguage.value
                        )
                    }
                    repository.insertSentences(newSentences)
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "تم إضافة 5 جمل جديدة للكلمة بنجاح!", android.widget.Toast.LENGTH_LONG).show()
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    val errorMsg = exception?.message ?: "Unknown error"
                    withContext(Dispatchers.Main) {
                        if (_geminiApiKey.value.isBlank()) {
                            android.widget.Toast.makeText(context, "تعذر توليد الجمل: الرجاء إدخال مفتاح Gemini API في الإعدادات.", android.widget.Toast.LENGTH_LONG).show()
                        } else {
                            android.widget.Toast.makeText(context, "فشل التوليد: $errorMsg", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            initPlanner(category, contentType)
            loadSentences()
            
            // Auto sync to cloud if key is set
            val syncKeyVal = _cloudSyncKey.value
            if (syncKeyVal.isNotBlank()) {
                syncAll(context, isManualTrigger = false)
            }
        }
    }

    fun editCard(card: Sentence, newText: String, newTranslation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedCard = card.copy(text = newText, translation = newTranslation)
            repository.insertSentences(listOf(updatedCard))
            initPlanner(card.category, card.contentType)
            loadSentences()
            
            // Auto sync to cloud if key is set
            val syncKeyVal = _cloudSyncKey.value
            if (syncKeyVal.isNotBlank()) {
                syncAll(context, isManualTrigger = false)
            }
        }
    }

    fun deleteCard(card: Sentence) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSentencesByIds(listOf(card.id))
            initPlanner(card.category, card.contentType)
            loadSentences()
            
            // Auto sync to cloud if key is set
            val syncKeyVal = _cloudSyncKey.value
            if (syncKeyVal.isNotBlank()) {
                syncAll(context, isManualTrigger = false)
            }
        }
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
            playSuccessSound()
            updateProgress(10, bestAcc)
            viewModelScope.launch {
                kotlinx.coroutines.delay(1500)
                nextSentence()
            }
        } else if (bestAcc > 0f) {
            playFailureSound()
        }
    }

    fun speakRecorded() {
        audioRecorder.playLastRecording()
    }

    private fun determineTargetAndTranslation(front: String, back: String): Pair<String, String> {
        val frontIsArabic = containsArabic(front)
        val backIsArabic = containsArabic(back)
        return if (frontIsArabic && !backIsArabic) {
            Pair(back, front)
        } else {
            Pair(front, back)
        }
    }

    fun addCustomSentences(text: String, language: String, category: String = "General", explicitContentType: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val lines = text.split("\n", "\r").filter { it.isNotBlank() }
            
            // Detect if it's an Anki tab-separated export
            val isAnkiExport = lines.any { line -> 
                line.contains("\t") && (line.startsWith("#") || line.split("\t").size >= 5) 
            }
            
            val newSentences = if (isAnkiExport) {
                lines.mapNotNull { line ->
                    val trimmedLine = line.trim()
                    if (trimmedLine.startsWith("#") || trimmedLine.isEmpty()) return@mapNotNull null
                    
                    val parts = trimmedLine.split("\t")
                    if (parts.size >= 5) {
                        var deckName = parts[2].trim()
                        if (deckName.contains("::")) {
                            deckName = deckName.substringAfterLast("::").trim()
                        }
                        if (deckName.isEmpty()) {
                            deckName = category
                        }
                        
                        val front = cleanAnkiText(parts[3])
                        val back = cleanAnkiText(parts[4])
                        if (front.isEmpty() || back.isEmpty()) return@mapNotNull null
                        
                        val (targetText, translationText) = determineTargetAndTranslation(front, back)
                        
                        Sentence(
                            text = targetText,
                            targetLang = language,
                            sourceLang = _nativeLanguage.value,
                            translation = translationText,
                            category = deckName,
                            contentType = explicitContentType ?: determineContentType(targetText, deckName)
                        )
                    } else if (parts.size >= 2) {
                        val front = cleanAnkiText(parts[0])
                        val back = cleanAnkiText(parts[1])
                        if (front.isEmpty() || back.isEmpty()) return@mapNotNull null
                        
                        val (targetText, translationText) = determineTargetAndTranslation(front, back)
                        
                        Sentence(
                            text = targetText,
                            targetLang = language,
                            sourceLang = _nativeLanguage.value,
                            translation = translationText,
                            category = category,
                            contentType = explicitContentType ?: determineContentType(targetText, category)
                        )
                    } else {
                        null
                    }
                }
            } else {
                lines.mapNotNull { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty()) return@mapNotNull null
                    
                    var targetText = trimmed
                    var translationText = ""
                    
                    if (trimmed.contains(";")) {
                        val parts = trimmed.split(";", limit = 2)
                        val p1 = parts[0].trim()
                        val p2 = parts.getOrNull(1)?.trim() ?: ""
                        
                        val (tText, transText) = determineTargetAndTranslation(p1, p2)
                        targetText = tText
                        translationText = transText
                    }
                    
                    if (targetText.isEmpty()) return@mapNotNull null
                    
                    if (translationText.isEmpty()) {
                        translationText = try {
                            translationManager.translate(targetText, language, _nativeLanguage.value)
                        } catch (e: Exception) {
                            "..."
                        }
                    }
                    
                    Sentence(
                        text = targetText,
                        targetLang = language,
                        sourceLang = _nativeLanguage.value,
                        translation = translationText,
                        category = category,
                        contentType = explicitContentType ?: determineContentType(targetText, category)
                    )
                }
            }
            
            if (newSentences.isNotEmpty()) {
                repository.insertSentences(newSentences)
                val totalImported = newSentences.size
                val categoriesCount = newSentences.map { it.category }.distinct().size
                if (isAnkiExport) {
                    _uiState.update { 
                        it.copy(feedback = "تم استيراد $categoriesCount مجموعات من Anki بنجاح (إجمالي $totalImported بطاقة)! 🎉") 
                    }
                } else {
                    _uiState.update { 
                        it.copy(feedback = "تم استيراد ${newSentences.first().category} بنجاح ($totalImported جملة)! ✅") 
                    }
                }
                loadSentences()
                currentPlannerDeck?.let { deck ->
                    initPlanner(deck, currentPlannerContentType)
                }
            }
        }
    }

    private fun cleanAnkiText(s: String): String {
        var text = s
        text = text.replace(Regex("\\[sound:[^\\]]+\\]"), "")
        text = text.replace(Regex("<[^>]*>"), "")
        text = text.replace("&amp;", "&")
        text = text.replace("&nbsp;", " ")
        text = text.replace("&quot;", "\"")
        text = text.replace("&#x27;", "'")
        text = text.replace("&apos;", "'")
        text = text.replace("&lt;", "<")
        text = text.replace("&gt;", ">")
        text = text.replace("\"\"", "\"")
        text = text.replace(Regex("\\s+"), " ")
        return text.trim()
    }

    private fun containsArabic(str: String): Boolean {
        for (char in str) {
            if (char.code in 0x0600..0x06FF) {
                return true
            }
        }
        return false
    }

    fun clearAllSentences() {
        viewModelScope.launch(Dispatchers.IO) {
            isSeedingEnabled = false
            sharedPreferences.edit().putBoolean("user_cleared_all", true).apply()
            repository.clearAll()
            _uiState.update { it.copy(feedback = "All local lessons cleared successfully 🗑️") }
            loadSentences()
        }
    }

    fun refreshCloudCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, feedback = "Fetching cloud catalog...") }
            val catalog = fetchCloudCatalog()
            _cloudCatalog.value = catalog
            _uiState.update { it.copy(isSyncing = false, feedback = "") }
        }
    }

    private val _syncReport = MutableStateFlow<String?>(null)
    val syncReport: StateFlow<String?> = _syncReport.asStateFlow()

    fun clearSyncReport() {
        _syncReport.value = null
    }

    private val _isExitSyncing = MutableStateFlow(false)
    val isExitSyncing: StateFlow<Boolean> = _isExitSyncing.asStateFlow()

    fun deleteLocalDeck(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteByCategory(categoryName)
            loadSentences()
        }
    }

    fun publishDeckToCloud(categoryName: String, onComplete: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sentences = repository.getByCategory(categoryName)
                if (sentences.isEmpty()) {
                    withContext(Dispatchers.Main) { onComplete("المجموعة فارغة!") }
                    return@launch
                }
                
                val jsonBody = org.json.JSONObject()
                val fields = org.json.JSONObject()
                
                val titleMap = org.json.JSONObject().apply {
                    put("fields", org.json.JSONObject().apply {
                        put("en-US", org.json.JSONObject().put("stringValue", categoryName))
                        put("ar-SA", org.json.JSONObject().put("stringValue", categoryName))
                    })
                }
                fields.put("title", org.json.JSONObject().put("mapValue", titleMap))
                fields.put("author", org.json.JSONObject().put("stringValue", "مستخدم مجهول"))
                
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                fields.put("uploadedAt", org.json.JSONObject().put("timestampValue", sdf.format(java.util.Date())))
                
                val sentencesArray = org.json.JSONArray()
                sentences.forEach { s ->
                    val sMap = org.json.JSONObject().apply {
                        put("fields", org.json.JSONObject().apply {
                            put("es", org.json.JSONObject().put("stringValue", s.text))
                            put("translation", org.json.JSONObject().put("stringValue", s.translation))
                            put("contentType", org.json.JSONObject().put("stringValue", s.contentType))
                        })
                    }
                    sentencesArray.put(org.json.JSONObject().put("mapValue", sMap))
                }
                
                fields.put("sentences", org.json.JSONObject().put("arrayValue", org.json.JSONObject().put("values", sentencesArray)))
                jsonBody.put("fields", fields)
                
                val url = java.net.URL("https://firestore.googleapis.com/v1/projects/repiteapp-6d79b/databases/(default)/documents/community_decks")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                
                val out = java.io.OutputStreamWriter(conn.outputStream)
                out.write(jsonBody.toString())
                out.close()
                
                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    withContext(Dispatchers.Main) { onComplete("نجاح") }
                } else {
                    val errorStream = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "No Error Stream"
                    Log.e("LessonViewModel", "Error publishing deck, code: $responseCode, msg: $errorStream")
                    withContext(Dispatchers.Main) { onComplete("Error $responseCode: $errorStream") }
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Error publishing deck", e)
                withContext(Dispatchers.Main) { onComplete("Exception: ${e.message}") }
            }
        }
    }

    private suspend fun runSyncEngine(context: android.content.Context): SyncSummary {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        
        // 1. Fetch remote lessons
        val snapshot = db.collection("lessons").get().await()
        val remoteLessons = snapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(com.repite.conmigo.models.Lesson::class.java)?.copy(id = doc.id)
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Error parsing lesson doc: ${doc.id}", e)
                null
            }
        }

        // 2. Fetch local sentences
        val localSentences = repository.allSentences.first()

        // 3. Local De-duplication
        val duplicatesToDelete = mutableListOf<Sentence>()
        val localDeletedNames = mutableListOf<String>()
        
        val localGrouped = localSentences.groupBy { Pair(it.category.trim().lowercase(), it.text.trim().lowercase()) }
        localGrouped.forEach { (key, group) ->
            if (group.size > 1) {
                val sortedGroup = group.sortedWith(compareByDescending<Sentence> { it.pronunciationScore }
                    .thenByDescending { it.memorizationDifficulty }
                    .thenBy { it.id })
                
                val best = sortedGroup.first()
                val duplicates = sortedGroup.drop(1)
                duplicatesToDelete.addAll(duplicates)
                duplicates.forEach { dup ->
                    localDeletedNames.add("«${getArabicCategoryName(dup.category)}: ${dup.text}»")
                }
            }
        }

        if (duplicatesToDelete.isNotEmpty()) {
            val idsToDelete = duplicatesToDelete.map { it.id }
            repository.deleteSentencesByIds(idsToDelete)
        }

        val cleanedLocalSentences = repository.allSentences.first()

        // 4. Bidirectional Synchronization & Merge (WITHOUT auto-downloading missing remote categories)
        val remoteLessonsToUpdate = mutableListOf<com.repite.conmigo.models.Lesson>()
        val localSentencesToInsert = mutableListOf<Sentence>()
        
        var uploadedCount = 0
        var downloadedCount = 0

        val localCategories = cleanedLocalSentences.groupBy { it.category.trim() }
        val remoteLessonsByTitle = remoteLessons.associateBy { it.title.trim().lowercase() }

        localCategories.forEach { (categoryName, localCategorySentences) ->
            val remoteLesson = remoteLessonsByTitle[categoryName.lowercase()]
            
            if (remoteLesson == null) {
                val contentToUpload = localCategorySentences.map { it.copy(id = 0) }
                val newRemoteLesson = com.repite.conmigo.models.Lesson(
                    id = categoryName,
                    title = categoryName,
                    content = contentToUpload,
                    type = "remote",
                    rawLevel = "beginner"
                )
                remoteLessonsToUpdate.add(newRemoteLesson)
                uploadedCount += contentToUpload.size
            } else {
                val remoteSentences = remoteLesson.content
                val remoteTexts = remoteSentences.map { it.text.trim().lowercase() }.toSet()
                
                val newLocalSentences = localCategorySentences.filter { 
                    !remoteTexts.contains(it.text.trim().lowercase()) 
                }
                
                val localTexts = localCategorySentences.map { it.text.trim().lowercase() }.toSet()
                val newRemoteSentences = remoteSentences.filter { 
                    !localTexts.contains(it.text.trim().lowercase()) 
                }

                if (newLocalSentences.isNotEmpty() || newRemoteSentences.isNotEmpty()) {
                    val mergedContent = (remoteSentences + newLocalSentences.map { it.copy(id = 0) })
                        .distinctBy { it.text.trim().lowercase() }
                    val updatedRemoteLesson = remoteLesson.copy(content = mergedContent)
                    remoteLessonsToUpdate.add(updatedRemoteLesson)
                    uploadedCount += newLocalSentences.size
                }

                newRemoteSentences.forEach { sentence ->
                    localSentencesToInsert.add(
                        sentence.copy(
                            id = 0,
                            category = categoryName
                        )
                    )
                    downloadedCount++
                }
            }
        }

        // 5. Save updates
        remoteLessonsToUpdate.forEach { updatedLesson ->
            db.collection("lessons")
                .document(updatedLesson.id)
                .set(updatedLesson)
                .await()
        }

        if (localSentencesToInsert.isNotEmpty()) {
            repository.insertSentences(localSentencesToInsert)
        }

        // 6. Audio downloads
        var audioSuccessCount = 0
        if (localSentencesToInsert.isNotEmpty()) {
            val updatedLocal = repository.allSentences.first()
            val insertedTexts = localSentencesToInsert.map { it.text.trim().lowercase() }.toSet()
            val newlyInsertedLocal = updatedLocal.filter { 
                insertedTexts.contains(it.text.trim().lowercase()) && it.localAudioPath == null
            }
            
            newlyInsertedLocal.forEach { sentence ->
                if (LessonLoader.downloadAudio(context, sentence, repository)) {
                    audioSuccessCount++
                }
            }
        }

        return SyncSummary(
            uploaded = uploadedCount,
            downloaded = downloadedCount,
            audioDownloaded = audioSuccessCount,
            duplicatesDeleted = duplicatesToDelete.size,
            deletedNames = localDeletedNames
        )
    }

    data class SyncSummary(
        val uploaded: Int,
        val downloaded: Int,
        val audioDownloaded: Int,
        val duplicatesDeleted: Int,
        val deletedNames: List<String>
    )

    fun syncAll(context: android.content.Context, isManualTrigger: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSyncing = true, feedback = "بدء المزامنة السحابية الشاملة... ☁️") }
            try {
                // Auto merge specific decks and clean duplicates
                mergeDecksAndCleanDuplicates()

                val summary = runSyncEngine(context)
                
                // Reload VM state
                loadSentences()

                // Auto backup user progress if sync key is set
                val syncKeyVal = cloudSyncKey.value
                if (syncKeyVal.isNotEmpty()) {
                    backupUserData(syncKeyVal)
                }

                // Compile Arabic Report
                val changesOccurred = summary.uploaded > 0 || summary.downloaded > 0 || summary.duplicatesDeleted > 0
                
                val sb = StringBuilder()
                sb.append("♻️ **تقرير المزامنة السحابية الشاملة** ♻️\n\n")
                
                if (summary.uploaded > 0) {
                    sb.append("📤 **الرفع للسحابة:** تم رفع `${summary.uploaded}` جملة جديدة إلى المكتبة السحابية.\n")
                } else {
                    sb.append("📤 **الرفع للسحابة:** السحابة محدثة بالكامل بكلماتك المحلية.\n")
                }
                
                if (summary.downloaded > 0) {
                    sb.append("📥 **التنزيل للجهاز:** تم تنزيل `${summary.downloaded}` جملة جديدة إلى جهازك.\n")
                    if (summary.audioDownloaded > 0) {
                        sb.append("🎧 **الملفات الصوتية:** تم تحميل `${summary.audioDownloaded}` ملف صوتي للجمل الجديدة.\n")
                    }
                } else {
                    sb.append("📥 **التنزيل للجهاز:** جهازك يحتوي على أحدث الكلمات السحابية بالفعل.\n")
                }
                
                if (summary.duplicatesDeleted > 0) {
                    sb.append("\n🗑️ **تصفية المكررات:** تم العثور على ونقل/حذف `${summary.duplicatesDeleted}` جملة مكررة للحفاظ على نظافة قاعدة البيانات ومستوى تقدمك:\n")
                    summary.deletedNames.take(10).forEach { name ->
                        sb.append("  • $name\n")
                    }
                    if (summary.deletedNames.size > 10) {
                        sb.append("  • ... و `${summary.deletedNames.size - 10}` جمل مكررة أخرى.\n")
                    }
                } else {
                    sb.append("\n✨ **تصفية المكررات:** لم يتم العثور على أي جمل مكررة في جهازك.\n")
                }
                
                val report = sb.toString()
                
                if (isManualTrigger || changesOccurred) {
                    _syncReport.value = report
                }

                _uiState.update { it.copy(isSyncing = false, feedback = "اكتملت المزامنة السحابية بنجاح ✅") }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Comprehensive sync failed", e)
                _uiState.update { it.copy(isSyncing = false, feedback = "فشلت المزامنة: ${e.message}") }
                if (isManualTrigger) {
                    _syncReport.value = "⚠️ **فشلت عملية المزامنة السحابية** ⚠️\n\nالسبب: ${e.localizedMessage ?: e.message}\n\nيرجى التحقق من اتصال الإنترنت والمحاولة مرة أخرى."
                }
            }
        }
    }

    fun syncAndExit(context: android.content.Context, activity: android.app.Activity?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isExitSyncing.value = true
            _uiState.update { it.copy(isSyncing = true, feedback = "جاري حفظ بياناتك ومزامنتها سحابياً قبل الخروج... ⏳") }
            try {
                // Run core sync
                runSyncEngine(context)
                
                loadSentences()
                
                _uiState.update { it.copy(isSyncing = false, feedback = "تمت المزامنة بنجاح. خروج... 👋") }
                
                // Exit
                activity?.runOnUiThread {
                    activity.finishAffinity()
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Sync before exit failed", e)
                _uiState.update { it.copy(isSyncing = false, feedback = "فشلت المزامنة: ${e.message}. خروج... 👋") }
                kotlinx.coroutines.delay(1000)
                activity?.runOnUiThread {
                    activity.finishAffinity()
                }
            }
        }
    }

    fun syncRemoteContent(context: android.content.Context) {
        syncAll(context, isManualTrigger = true)
    }

    fun importRemoteLesson(url: String, context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(feedback = "Downloading lesson... ⏳") }
            
            if (!url.startsWith("http")) {
                // It's a Firestore ID
                try {
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    val doc = db.collection("lessons").document(url).get().await()
                    val lesson = doc.toObject(com.repite.conmigo.models.Lesson::class.java)
                    if (lesson != null) {
                        repository.insertSentences(lesson.content)
                        _uiState.update { it.copy(feedback = "Imported ${lesson.title} successfully ✅") }
                        loadSentences()
                    } else {
                        _uiState.update { it.copy(feedback = "Lesson not found in cloud ❌") }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(feedback = "Cloud Error: ${e.message}") }
                }
                return@launch
            }

            val result = LessonLoader.fetchRemoteLesson(context, repository, url)
            
            if (result.isSuccess) {
                val lessonTitle = result.getOrNull() ?: ""
                _uiState.update { it.copy(feedback = "Imported $lessonTitle successfully. Downloading audio... 🎧") }
                
                // Automation: Fetch sentences of this lesson and download audio
                val allSentences = repository.allSentences.first()
                val newLessonSentences = allSentences.filter { it.category == lessonTitle }
                
                var successCount = 0
                newLessonSentences.forEach { sentence ->
                    if (LessonLoader.downloadAudio(context, sentence, repository)) {
                        successCount++
                    }
                }
                
                _uiState.update { it.copy(feedback = "Complete! Downloaded $successCount audio files for: $lessonTitle ✅") }
                loadSentences()
            } else {
                _uiState.update { it.copy(feedback = "Import Error: ${result.exceptionOrNull()?.message} ❌") }
            }
        }
    }

    suspend fun fetchCloudCatalog(): List<LessonMetadata> {
        return try {
            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            val snapshot = db.collection("lessons")
                .get()
                .await()
            
            val metadataList = mutableListOf<LessonMetadata>()
            snapshot.documents.forEach { doc ->
                val title = doc.getString("title") ?: "No Title"
                val lang = doc.getString("targetLanguage") ?: "es"
                val id = doc.id
                metadataList.add(
                    LessonMetadata(
                        lesson_id = id,
                        title = title,
                        version = 1,
                        target_lang = lang,
                        source_lang = "ar",
                        url = id
                    )
                )
            }
            metadataList
        } catch (e: Exception) {
            Log.e("LessonViewModel", "Error fetching cloud catalog: ${e.message}")
            emptyList<LessonMetadata>()
        }
    }

    fun speakText(text: String, lang: String = "es") {
        ttsManager.speak(text, lang)
    }

    fun addXp(amount: Int) {
        updateProgress(amount)
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
            
            val updated = current.copy(
                xp = newXp,
                streak = newStreak,
                lastActiveDate = now,
                highestAccuracy = newHighest,
                sessionCount = current.sessionCount + (if (amount > 10) 1 else 0)
            )
            repository.updateUserProgress(updated)
            
            // Auto backup to cloud if key is set
            val syncKeyVal = _cloudSyncKey.value
            if (syncKeyVal.isNotBlank()) {
                backupUserData(syncKeyVal)
            }
        }
    }

    fun nextSentence() {
        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()
        _uiState.update { 
            val nextIndex = (it.currentIndex + 1) % it.sentences.size
            // If we wrap around, reshuffle if in quiz mode
            val finalSentences = if (nextIndex == 0 && _isQuizMode.value) it.sentences.shuffled() else it.sentences
            it.copy(sentences = finalSentences, currentIndex = nextIndex, lastAccuracy = 0f, feedback = "")
        }
    }

    fun previousSentence() {
        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()
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


    fun playSuccessSound() {
        try {
            val toneGenerator = android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
            toneGenerator.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 150)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                toneGenerator.release()
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playFailureSound() {
        try {
            val toneGenerator = android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
            toneGenerator.startTone(android.media.ToneGenerator.TONE_PROP_NACK, 250)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                toneGenerator.release()
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun backup() = backupManager.backupProgress(userProgress.value ?: UserProgress())

    fun restore() {
        viewModelScope.launch {
            backupManager.restoreProgress()?.let {
                repository.updateUserProgress(it)
            }
        }
    }

    // --- Anki Spaced Repetition Logic ---
    private val _ankiState = MutableStateFlow(AnkiState())
    val ankiState: StateFlow<AnkiState> = _ankiState.asStateFlow()

    fun toggleAnkiLanguageSwap() {
        _ankiState.update { it.copy(swapLanguages = !it.swapLanguages) }
    }

    fun startAnkiSession(deck: String?, mode: String, contentType: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val allSentences = repository.allSentences.first()
            
            // 1. Filter by deck (category) if specified
            var filtered = if (deck != null) {
                allSentences.filter { it.category == deck }
            } else {
                allSentences
            }
            
            // Filter by contentType
            if (contentType != null) {
                filtered = filtered.filter { it.contentType == contentType }
            }

            // 2. Filter by mode
            filtered = when (mode) {
                "difficult" -> {
                    // Difficult cards: marked as hard (3), medium (2), or low pronunciation accuracy (< 70% but has been attempted)
                    val difficultCards = filtered.filter { 
                        it.memorizationDifficulty == 3 || 
                        it.memorizationDifficulty == 2 || 
                        (it.pronunciationScore in 0.1f..69.9f)
                    }
                    if (difficultCards.isEmpty()) {
                        // Fallback: If no difficult cards exist yet, grab any cards
                        filtered
                    } else {
                        difficultCards
                    }
                }
                else -> filtered // "all" or "random"
            }

            // 3. Shuffle if random or difficult mode
            val shuffledCards = if (mode == "random" || mode == "difficult") {
                filtered.shuffled()
            } else {
                filtered
            }
            val finalCards = shuffledCards.take(sessionSize.value)

            speakJob?.cancel()
            _currentAudioPlayIndex.value = 0
            ttsManager.stop()
            _ankiState.update {
                it.copy(
                    currentDeck = deck,
                    cards = finalCards,
                    currentIndex = 0,
                    showAnswer = false,
                    pronunciationScore = 0f,
                    feedback = "",
                    wrongWords = emptySet(),
                    lastTranscription = "",
                    testMode = mode
                )
            }
        }
    }

    fun setShowAnswer(show: Boolean) {
        _ankiState.update { it.copy(showAnswer = show) }
    }

    fun nextAnkiCard() {
        val state = _ankiState.value
        if (state.currentIndex < state.cards.size - 1) {
            speakJob?.cancel()
            _currentAudioPlayIndex.value = 0
            ttsManager.stop()
            _ankiState.update {
                it.copy(
                    currentIndex = state.currentIndex + 1,
                    showAnswer = false,
                    pronunciationScore = 0f,
                    feedback = "",
                    wrongWords = emptySet(),
                    lastTranscription = ""
                )
            }
        }
    }

    fun previousAnkiCard() {
        val state = _ankiState.value
        if (state.currentIndex > 0) {
            speakJob?.cancel()
            _currentAudioPlayIndex.value = 0
            ttsManager.stop()
            _ankiState.update {
                it.copy(
                    currentIndex = state.currentIndex - 1,
                    showAnswer = false,
                    pronunciationScore = 0f,
                    feedback = "",
                    wrongWords = emptySet(),
                    lastTranscription = ""
                )
            }
        }
    }

    fun updateAnkiCardDifficulty(sentenceId: Int, difficulty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = _ankiState.value
            val currentCard = state.cards.getOrNull(state.currentIndex)
            val score = currentCard?.pronunciationScore ?: 0f
            
            repository.updateRatings(sentenceId, score, difficulty)

            // Update in local state list immediately
            val updatedCards = state.cards.map { card ->
                if (card.id == sentenceId) {
                    card.copy(memorizationDifficulty = difficulty)
                } else {
                    card
                }
            }
            _ankiState.update { it.copy(cards = updatedCards) }
        }
    }

    fun speakAnkiCurrent() {
        val state = _ankiState.value
        if (state.cards.isNotEmpty()) {
            val sentence = state.cards[state.currentIndex]
            speakJob?.cancel()
            speakJob = viewModelScope.launch(Dispatchers.Main) {
                val repetitions = audioRepetitions.value
                for (i in 1..repetitions) {
                    _currentAudioPlayIndex.value = i
                    if (sentence.localAudioPath != null) {
                        audioRecorder.playFile(sentence.localAudioPath)
                        delay(2500)
                    } else {
                        val cleanText = sentence.text.replace("🔴", "").trim()
                        ttsManager.speak(cleanText, sentence.targetLang, speechRate.value)
                        var started = false
                        for (j in 1..10) {
                            if (ttsManager.isSpeaking.value) {
                                started = true
                                break
                            }
                            delay(50)
                        }
                        if (started) {
                            while (ttsManager.isSpeaking.value) {
                                delay(100)
                            }
                        } else {
                            delay(1500)
                        }
                    }
                    if (i < repetitions) {
                        delay(1200)
                    }
                }
                _currentAudioPlayIndex.value = 0
            }
        }
    }

    fun onAnkiRecordClick() {
        val state = _ankiState.value
        val isRecording = state.isRecording
        if (isRecording) {
            sttManager.stopListening()
            _ankiState.update { it.copy(isRecording = false) }
        } else {
            val currentCard = state.cards.getOrNull(state.currentIndex) ?: return
            _ankiState.update { it.copy(isRecording = true, feedback = "...") }

            sttManager.startListening(currentCard.targetLang) { spokenText ->
                _ankiState.update { it.copy(isRecording = false) }
                if (spokenText.isNotEmpty()) {
                    analyzeAnkiSpokenText(spokenText)
                }
            }
        }
    }

    fun analyzeAnkiSpokenText(spokenText: String) {
        _ankiState.update { it.copy(isRecording = false) }
        if (spokenText.startsWith("ERR: ")) {
            _ankiState.update { it.copy(feedback = spokenText.removePrefix("ERR: ")) }
            return
        }
        if (spokenText.isBlank()) {
            _ankiState.update { it.copy(feedback = "Didn't hear anything, try again.. 🎤", pronunciationScore = 0f, lastTranscription = "") }
            return
        }

        val state = _ankiState.value
        val currentCard = state.cards.getOrNull(state.currentIndex) ?: return

        val candidates = spokenText.split("|")
        var bestAcc = 0f
        var bestMatch = candidates[0]
        var minMisses = Int.MAX_VALUE

        candidates.forEach { candidate ->
            val acc = SpeechAnalyzer.calculateAccuracy(currentCard.text, candidate)
            val missing = SpeechAnalyzer.getMissingWords(currentCard.text, candidate)
            if (acc > bestAcc || (acc == bestAcc && missing.size < minMisses)) {
                bestAcc = acc
                bestMatch = candidate
                minMisses = missing.size
            }
        }

        val finalFeedback = SpeechAnalyzer.getFeedback(currentCard.text, bestMatch)

        // Save to Database
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRatings(currentCard.id, bestAcc, currentCard.memorizationDifficulty)
            
            // Also insert a normal learning record
            repository.addRecord(
                LearningRecord(
                    sentenceId = currentCard.id,
                    date = System.currentTimeMillis(),
                    accuracy = bestAcc,
                    transcription = bestMatch
                )
            )
        }

        // Update local list
        val updatedCards = state.cards.map { card ->
            if (card.id == currentCard.id) {
                card.copy(pronunciationScore = bestAcc)
            } else {
                card
            }
        }

        _ankiState.update {
            it.copy(
                pronunciationScore = bestAcc,
                feedback = if (bestAcc >= accuracyThreshold.value && bestAcc > 0) "Excellent pronunciation! ✨" else if (bestAcc == 0f) "Didn't hear you clearly... try again 🎙️" else finalFeedback,
                wrongWords = SpeechAnalyzer.getMissingWords(currentCard.text, bestMatch),
                lastTranscription = bestMatch,
                cards = updatedCards
            )
        }

        if (bestAcc >= accuracyThreshold.value) {
            playSuccessSound()
            updateProgress(10, bestAcc)
        } else if (bestAcc > 0f) {
            playFailureSound()
        }
    }

    // --- Daily Word/Sentence Planner ---
    private val _plannerCards = MutableStateFlow<List<Sentence>>(emptyList())
    val plannerCards: StateFlow<List<Sentence>> = _plannerCards.asStateFlow()

    private val _remainingDeckCards = MutableStateFlow<List<Sentence>>(emptyList())
    val remainingDeckCards: StateFlow<List<Sentence>> = _remainingDeckCards.asStateFlow()

    private var currentPlannerDeck: String? = null
    private var currentPlannerContentType: String = "word"
    private var lastInitializedDeck: String? = null

    private val _useOriginalOrder = MutableStateFlow(false)
    val useOriginalOrder: StateFlow<Boolean> = _useOriginalOrder.asStateFlow()

    private val _rangeStart = MutableStateFlow(1)
    val rangeStart: StateFlow<Int> = _rangeStart.asStateFlow()

    private val _rangeEnd = MutableStateFlow(30)
    val rangeEnd: StateFlow<Int> = _rangeEnd.asStateFlow()

    private val _totalDeckCardsCount = MutableStateFlow(0)
    val totalDeckCardsCount: StateFlow<Int> = _totalDeckCardsCount.asStateFlow()

    fun setUseOriginalOrder(useOriginal: Boolean) {
        _useOriginalOrder.value = useOriginal
        currentPlannerDeck?.let { deck ->
            initPlanner(deck, currentPlannerContentType)
        }
    }

    fun setCustomRange(start: Int, end: Int) {
        _rangeStart.value = start
        _rangeEnd.value = end
        currentPlannerDeck?.let { deck ->
            initPlanner(deck, currentPlannerContentType)
        }
    }

    fun initPlanner(deckName: String, contentType: String) {
        val isNewDeck = lastInitializedDeck != deckName || currentPlannerContentType != contentType
        lastInitializedDeck = deckName
        currentPlannerDeck = deckName
        currentPlannerContentType = contentType
        
        viewModelScope.launch(Dispatchers.IO) {
            val allSentences = repository.allSentences.first()
            val deckSentences = allSentences.filter { 
                it.category == deckName && it.contentType == contentType 
            }.distinctBy { it.text.trim().lowercase() }

            val totalSize = deckSentences.size
            _totalDeckCardsCount.value = totalSize

            if (isNewDeck) {
                _rangeStart.value = 1
                _rangeEnd.value = totalSize.coerceIn(3, 30.coerceAtLeast(3))
            }

            val sorted = if (_useOriginalOrder.value) {
                deckSentences.sortedBy { it.id } // Original insertion order
            } else {
                deckSentences.sortedWith(
                    compareBy<Sentence> { it.memorizationDifficulty == 1 } // Put known ones last
                        .thenByDescending { it.memorizationDifficulty } // Then prioritize hard/medium
                        .thenBy { it.pronunciationScore } // Then prioritize lower pronunciation scores
                )
            }

            if (_useOriginalOrder.value) {
                val start = (_rangeStart.value - 1).coerceIn(0, totalSize - 1)
                val end = _rangeEnd.value.coerceIn(start + 1, totalSize)
                val sliced = if (sorted.isNotEmpty()) sorted.subList(start, end) else emptyList()
                _plannerCards.value = sliced
                _remainingDeckCards.value = emptyList()
                _sessionSize.value = sliced.size
            } else {
                val currentLimit = sessionSize.value.coerceIn(3, sorted.size.coerceAtLeast(3))
                val initialPlanner = sorted.take(currentLimit)
                val initialRemaining = sorted.drop(currentLimit)

                _plannerCards.value = initialPlanner
                _remainingDeckCards.value = initialRemaining
            }
        }
    }

    fun adjustPlannerSize(newSize: Int) {
        val size = newSize.coerceIn(3, 100)
        _sessionSize.value = size
        sharedPreferences.edit().putInt("session_size", size).apply()

        val currentPlannerList = _plannerCards.value.toMutableList()
        val currentRemainingList = _remainingDeckCards.value.toMutableList()

        if (size > currentPlannerList.size) {
            val needed = size - currentPlannerList.size
            val toAdd = currentRemainingList.take(needed)
            currentPlannerList.addAll(toAdd)
            currentRemainingList.removeAll(toAdd)
        } else if (size < currentPlannerList.size) {
            val toRemoveCount = currentPlannerList.size - size
            val toRemove = currentPlannerList.takeLast(toRemoveCount)
            currentRemainingList.addAll(0, toRemove) // Put back at the beginning of remaining
            repeat(toRemoveCount) {
                if (currentPlannerList.isNotEmpty()) currentPlannerList.removeAt(currentPlannerList.size - 1)
            }
        }

        _plannerCards.value = currentPlannerList
        _remainingDeckCards.value = currentRemainingList
    }

    fun swapPlannerCard(sentenceId: Int) {
        val currentPlannerList = _plannerCards.value.toMutableList()
        val currentRemainingList = _remainingDeckCards.value.toMutableList()

        val indexToReplace = currentPlannerList.indexOfFirst { it.id == sentenceId }
        if (indexToReplace != -1 && currentRemainingList.isNotEmpty()) {
            val oldCard = currentPlannerList[indexToReplace]
            val newCard = currentRemainingList.removeAt(0)
            
            currentPlannerList[indexToReplace] = newCard
            currentRemainingList.add(oldCard) // Put old card at the end of remaining list

            _plannerCards.value = currentPlannerList
            _remainingDeckCards.value = currentRemainingList
        }
    }

    fun markPlannerCardAsKnown(sentenceId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // Update in DB (difficulty = 1 means Easy/Known)
            repository.updateRatings(sentenceId, 100f, 1)
            
            // Swap it out of the planner list
            val currentPlannerList = _plannerCards.value.toMutableList()
            val currentRemainingList = _remainingDeckCards.value.toMutableList()

            val indexToReplace = currentPlannerList.indexOfFirst { it.id == sentenceId }
            if (indexToReplace != -1) {
                val oldCard = currentPlannerList[indexToReplace].copy(memorizationDifficulty = 1, pronunciationScore = 100f)
                if (currentRemainingList.isNotEmpty()) {
                    val newCard = currentRemainingList.removeAt(0)
                    currentPlannerList[indexToReplace] = newCard
                    currentRemainingList.add(oldCard)
                } else {
                    // No remaining cards to swap with, just update its status in-place
                    currentPlannerList[indexToReplace] = oldCard
                }
                
                _plannerCards.value = currentPlannerList
                _remainingDeckCards.value = currentRemainingList
            }
        }
    }

    fun startPlannedSession(mode: String) {
        val cards = _plannerCards.value
        if (cards.isEmpty()) return

        speakJob?.cancel()
        _currentAudioPlayIndex.value = 0
        ttsManager.stop()

        // Set as active cards in AnkiState
        _ankiState.update {
            it.copy(
                currentDeck = currentPlannerDeck,
                cards = cards,
                currentIndex = 0,
                showAnswer = false,
                pronunciationScore = 0f,
                feedback = "",
                wrongWords = emptySet(),
                lastTranscription = "",
                testMode = mode
            )
        }

        // Set as active cards in UIState (Lessons/Quizzes)
        _uiState.update {
            it.copy(
                sentences = cards,
                currentIndex = 0,
                completed = false,
                feedback = ""
            )
        }
    }

    fun generateStoryForDeck(deckName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isGeneratingStory.value = true
            try {
                val allSentences = repository.allSentences.first()
                val deckWords = allSentences.filter { 
                    it.category == deckName && it.contentType == "word" 
                }.map { it.text }

                if (deckWords.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "لا توجد كلمات في هذه المجموعة لتأليف قصة", android.widget.Toast.LENGTH_LONG).show()
                    }
                    _isGeneratingStory.value = false
                    return@launch
                }

                val generator = SentenceGenerator()
                val result = generator.generateStoryFromWords(
                    apiKey = _geminiApiKey.value,
                    words = deckWords,
                    learningLanguage = _learningLanguage.value,
                    nativeLanguage = _nativeLanguage.value
                )

                if (result.isSuccess) {
                    val storySentence = result.getOrNull()
                    if (storySentence != null) {
                        val sentence = Sentence(
                            text = storySentence.sentence,
                            translation = storySentence.translation,
                            category = deckName,
                            contentType = "passage",
                            targetLang = _learningLanguage.value,
                            sourceLang = _nativeLanguage.value
                        )
                        repository.insertSentences(listOf(sentence))
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(context, "تم تأليف القصة وحفظها بنجاح! 📖", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "فشل تأليف القصة: ${result.exceptionOrNull()?.message}", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "حدث خطأ غير متوقع", android.widget.Toast.LENGTH_SHORT).show()
                }
            } finally {
                _isGeneratingStory.value = false
            }
        }
    }

    fun generateExamplesForDeck(deckName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allSentences = repository.allSentences.first()
                val deckWords = allSentences.filter { 
                    it.category == deckName && it.contentType == "word" 
                }

                if (deckWords.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(context, "لا توجد كلمات في هذه المجموعة", android.widget.Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                _generatingExamplesProgress.value = "جاري توليد جمل لـ ${deckWords.size} كلمات... ⏳"
                val generator = SentenceGenerator()
                var successCount = 0

                for ((index, word) in deckWords.withIndex()) {
                    _generatingExamplesProgress.value = "جاري توليد جمل للكلمة (${index + 1}/${deckWords.size}): ${word.text}..."
                    
                    val result = generator.generateSentences(
                        apiKey = _geminiApiKey.value,
                        word = word.text,
                        learningLanguage = _learningLanguage.value,
                        nativeLanguage = _nativeLanguage.value
                    )

                    if (result.isSuccess) {
                        val generatedSentences = result.getOrNull() ?: emptyList()
                        val sentencesToInsert = generatedSentences.map {
                            Sentence(
                                text = it.sentence,
                                translation = it.translation,
                                category = deckName,
                                contentType = "sentence",
                                targetLang = _learningLanguage.value,
                                sourceLang = _nativeLanguage.value
                            )
                        }
                        if (sentencesToInsert.isNotEmpty()) {
                            repository.insertSentences(sentencesToInsert)
                            successCount++
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "تم بنجاح توليد جمل لـ $successCount كلمات! ✨", android.widget.Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "حدث خطأ أثناء توليد الجمل", android.widget.Toast.LENGTH_SHORT).show()
                }
            } finally {
                _generatingExamplesProgress.value = null
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
