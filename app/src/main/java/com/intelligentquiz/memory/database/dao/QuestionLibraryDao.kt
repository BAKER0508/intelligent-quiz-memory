package com.intelligentquiz.memory.database.dao

import androidx.room.*
import androidx.lifecycle.LiveData
import com.intelligentquiz.memory.database.entities.QuestionLibrary
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionLibraryDao {
    
    @Query("SELECT * FROM question_libraries WHERE isActive = 1 ORDER BY lastStudiedAt DESC, createdAt DESC")
    fun getAllActiveLibraries(): Flow<List<QuestionLibrary>>
    
    @Query("SELECT * FROM question_libraries WHERE id = :id")
    suspend fun getLibraryById(id: Long): QuestionLibrary?
    
    @Query("SELECT * FROM question_libraries WHERE name = :name LIMIT 1")
    suspend fun getLibraryByName(name: String): QuestionLibrary?
    
    @Insert
    suspend fun insertLibrary(library: QuestionLibrary): Long
    
    @Update
    suspend fun updateLibrary(library: QuestionLibrary)
    
    @Query("UPDATE question_libraries SET isActive = 0 WHERE id = :id")
    suspend fun deleteLibrary(id: Long)
    
    @Query("UPDATE question_libraries SET totalQuestions = :count WHERE id = :id")
    suspend fun updateTotalQuestions(id: Long, count: Int)
    
    @Query("UPDATE question_libraries SET completedQuestions = :count WHERE id = :id")
    suspend fun updateCompletedQuestions(id: Long, count: Int)
    
    @Query("UPDATE question_libraries SET lastStudiedAt = :timestamp WHERE id = :id")
    suspend fun updateLastStudiedAt(id: Long, timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM question_libraries WHERE isActive = 1")
    suspend fun getActiveLibraryCount(): Int
}