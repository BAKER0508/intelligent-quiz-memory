package com.intelligentquiz.memory.database.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.intelligentquiz.memory.database.entities.StudyRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyRecordDao {
    
    @Query("SELECT * FROM study_records WHERE questionId = :questionId ORDER BY studiedAt DESC")
    fun getRecordsByQuestion(questionId: Long): Flow<List<StudyRecord>>
    
    @Query("SELECT * FROM study_records WHERE questionId = :questionId ORDER BY studiedAt DESC LIMIT 1")
    suspend fun getLatestRecord(questionId: Long): StudyRecord?
    
    @Insert
    suspend fun insertRecord(record: StudyRecord): Long
    
    @Update
    suspend fun updateRecord(record: StudyRecord)
    
    @Query("SELECT COUNT(*) FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId AND sr.consecutiveCorrect >= 3")
    suspend fun getMasteredQuestionCount(libraryId: Long): Int
    
    @Query("SELECT COUNT(*) FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId AND sr.isInErrorBank = 1")
    suspend fun getErrorBankCount(libraryId: Long): Int
    
    @Query("SELECT COUNT(*) FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId AND DATE(sr.studiedAt/1000, 'unixepoch') = DATE('now')")
    suspend fun getTodayStudyCount(libraryId: Long): Int
    
    @Query("SELECT COUNT(*) FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId AND sr.remembered = 1")
    suspend fun getCorrectAnswerCount(libraryId: Long): Int
    
    @Query("SELECT COUNT(*) FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId")
    suspend fun getTotalStudyCount(libraryId: Long): Int
    
    @Query("SELECT AVG(sr.responseTime) FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId AND sr.responseTime > 0")
    suspend fun getAverageResponseTime(libraryId: Long): Double?
    
    @Query("DELETE FROM study_records WHERE questionId = :questionId")
    suspend fun deleteRecordsByQuestion(questionId: Long)
    
    @Query("SELECT * FROM study_records sr JOIN questions q ON sr.questionId = q.id WHERE q.libraryId = :libraryId AND sr.nextReviewAt <= :currentTime ORDER BY sr.nextReviewAt")
    suspend fun getQuestionsForReview(libraryId: Long, currentTime: Long): List<StudyRecord>
}