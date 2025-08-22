package com.intelligentquiz.memory.database.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.intelligentquiz.memory.database.entities.Question
import com.intelligentquiz.memory.database.entities.QuestionType
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    
    @Query("SELECT * FROM questions WHERE libraryId = :libraryId AND isActive = 1 ORDER BY createdAt")
    fun getQuestionsByLibrary(libraryId: Long): Flow<List<Question>>
    
    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): Question?
    
    @Query("SELECT * FROM questions WHERE libraryId = :libraryId AND isActive = 1 LIMIT :limit OFFSET :offset")
    suspend fun getQuestionsPaginated(libraryId: Long, limit: Int, offset: Int): List<Question>
    
    @Query("SELECT * FROM questions WHERE libraryId = :libraryId AND type = :type AND isActive = 1")
    suspend fun getQuestionsByType(libraryId: Long, type: QuestionType): List<Question>
    
    @Insert
    suspend fun insertQuestion(question: Question): Long
    
    @Insert
    suspend fun insertQuestions(questions: List<Question>): List<Long>
    
    @Update
    suspend fun updateQuestion(question: Question)
    
    @Query("UPDATE questions SET isActive = 0 WHERE id = :id")
    suspend fun deleteQuestion(id: Long)
    
    @Query("UPDATE questions SET isActive = 0 WHERE libraryId = :libraryId")
    suspend fun deleteQuestionsByLibrary(libraryId: Long)
    
    @Query("SELECT COUNT(*) FROM questions WHERE libraryId = :libraryId AND isActive = 1")
    suspend fun getQuestionCountByLibrary(libraryId: Long): Int
    
    @Query("SELECT COUNT(*) FROM questions WHERE libraryId = :libraryId AND type = :type AND isActive = 1")
    suspend fun getQuestionCountByType(libraryId: Long, type: QuestionType): Int
    
    @Query("SELECT * FROM questions WHERE libraryId = :libraryId AND isActive = 1 AND id IN (SELECT questionId FROM study_records WHERE consecutiveCorrect < 3) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomUnmasteredQuestion(libraryId: Long): Question?
    
    @Query("SELECT * FROM questions WHERE libraryId = :libraryId AND isActive = 1 AND id IN (SELECT questionId FROM study_records WHERE isInErrorBank = 1) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomErrorBankQuestion(libraryId: Long): Question?
}