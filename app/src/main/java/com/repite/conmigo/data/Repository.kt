package com.repite.conmigo.data

import kotlinx.coroutines.flow.Flow

class LessonRepository(private val lessonDao: LessonDao) {
    
    val allSentences: Flow<List<Sentence>> = lessonDao.getAllSentences()
    val categories: Flow<List<String>> = lessonDao.getCategories()
    val contentTypes: Flow<List<String>> = lessonDao.getContentTypes()
    val userProgress: Flow<UserProgress?> = lessonDao.getUserProgress()

    fun getByContentType(type: String): Flow<List<Sentence>> = lessonDao.getByContentType(type)

    suspend fun insertSentences(sentences: List<Sentence>) {
        lessonDao.insertSentences(sentences)
    }

    suspend fun updateUserProgress(progress: UserProgress) {
        lessonDao.updateUserProgress(progress)
    }

    suspend fun clearAll() {
        lessonDao.clearSentences()
    }

    val allRecords: Flow<List<LearningRecord>> = lessonDao.getAllRecords()

    suspend fun addRecord(record: LearningRecord) {
        lessonDao.insertRecord(record)
    }

    suspend fun getSentenceCountByCategory(category: String): Int {
        return lessonDao.getSentenceCountByCategory(category)
    }

    suspend fun deleteByCategory(category: String) {
        lessonDao.deleteByCategory(category)
    }
}
