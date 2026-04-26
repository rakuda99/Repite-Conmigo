package com.repite.conmigo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM sentences")
    fun getAllSentences(): Flow<List<Sentence>>

    @Query("SELECT * FROM sentences WHERE contentType = :type")
    fun getByContentType(type: String): Flow<List<Sentence>>

    @Query("SELECT DISTINCT category FROM sentences")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT DISTINCT contentType FROM sentences")
    fun getContentTypes(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentences(sentences: List<Sentence>)

    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserProgress(progress: UserProgress)

    @Query("DELETE FROM sentences")
    suspend fun clearSentences()

    @Query("SELECT * FROM learning_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<LearningRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LearningRecord)

    @Query("SELECT COUNT(*) FROM sentences WHERE category = :category")
    suspend fun getSentenceCountByCategory(category: String): Int

    @Query("DELETE FROM sentences WHERE category = :category")
    suspend fun deleteByCategory(category: String)
}
