package com.intelligentquiz.memory.repository

import android.content.Context
import com.intelligentquiz.memory.database.AppDatabase
import com.intelligentquiz.memory.database.entities.Question
import com.intelligentquiz.memory.database.entities.QuestionLibrary
import com.intelligentquiz.memory.database.entities.StudyRecord
import com.intelligentquiz.memory.utils.QuestionParser
import kotlinx.coroutines.flow.Flow

class QuestionRepository(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val libraryDao = database.questionLibraryDao()
    private val questionDao = database.questionDao()
    private val recordDao = database.studyRecordDao()
    
    // Library operations
    fun getAllLibraries(): Flow<List<QuestionLibrary>> {
        return libraryDao.getAllActiveLibraries()
    }
    
    suspend fun getLibraryById(id: Long): QuestionLibrary? {
        return libraryDao.getLibraryById(id)
    }
    
    suspend fun getLibraryByName(name: String): QuestionLibrary? {
        return libraryDao.getLibraryByName(name)
    }
    
    suspend fun createLibrary(name: String, description: String? = null): Long {
        val library = QuestionLibrary(
            name = name,
            description = description,
            totalQuestions = 0,
            completedQuestions = 0,
            createdAt = System.currentTimeMillis(),
            isActive = true
        )
        return libraryDao.insertLibrary(library)
    }
    
    suspend fun updateLibraryStats(libraryId: Long) {
        val totalCount = questionDao.getQuestionCountByLibrary(libraryId)
        val masteredCount = recordDao.getMasteredQuestionCount(libraryId)
        
        libraryDao.updateTotalQuestions(libraryId, totalCount)
        libraryDao.updateCompletedQuestions(libraryId, masteredCount)
    }
    
    // Question operations
    fun getQuestionsByLibrary(libraryId: Long): Flow<List<Question>> {
        return questionDao.getQuestionsByLibrary(libraryId)
    }
    
    suspend fun getQuestionById(id: Long): Question? {
        return questionDao.getQuestionById(id)
    }
    
    suspend fun insertQuestions(questions: List<Question>): List<Long> {
        return questionDao.insertQuestions(questions)
    }
    
    suspend fun getRandomUnmasteredQuestion(libraryId: Long): Question? {
        return questionDao.getRandomUnmasteredQuestion(libraryId)
    }
    
    suspend fun getRandomErrorBankQuestion(libraryId: Long): Question? {
        return questionDao.getRandomErrorBankQuestion(libraryId)
    }
    
    // Study record operations
    suspend fun getLatestRecord(questionId: Long): StudyRecord? {
        return recordDao.getLatestRecord(questionId)
    }
    
    suspend fun insertStudyRecord(record: StudyRecord): Long {
        val recordId = recordDao.insertRecord(record)
        // Update library stats after recording study
        val question = questionDao.getQuestionById(record.questionId)
        question?.let { updateLibraryStats(it.libraryId) }
        return recordId
    }
    
    // Import functionality
    suspend fun importQuestions(content: String, libraryName: String, createNew: Boolean): Int {
        val library = if (createNew) {
            val libraryId = createLibrary(libraryName)
            libraryDao.getLibraryById(libraryId)!!
        } else {
            getLibraryByName(libraryName) ?: throw IllegalArgumentException("题库不存在: $libraryName")
        }
        
        val parseResult = QuestionParser.parseText(content, library.id)
        if (parseResult.errors.isNotEmpty()) {
            throw IllegalArgumentException("解析错误: ${parseResult.errors.joinToString(", ")}")
        }
        
        val questions = QuestionParser.convertToQuestions(parseResult.questions, library.id)
        insertQuestions(questions)
        updateLibraryStats(library.id)
        
        return parseResult.totalParsed
    }
    
    // Statistics
    suspend fun getLibraryStats(libraryId: Long): LibraryStats {
        val totalQuestions = questionDao.getQuestionCountByLibrary(libraryId)
        val masteredQuestions = recordDao.getMasteredQuestionCount(libraryId)
        val errorBankCount = recordDao.getErrorBankCount(libraryId)
        val todayStudyCount = recordDao.getTodayStudyCount(libraryId)
        val totalStudyCount = recordDao.getTotalStudyCount(libraryId)
        val correctAnswerCount = recordDao.getCorrectAnswerCount(libraryId)
        val averageResponseTime = recordDao.getAverageResponseTime(libraryId) ?: 0.0
        
        val correctRate = if (totalStudyCount > 0) {
            (correctAnswerCount.toDouble() / totalStudyCount * 100)
        } else {
            0.0
        }
        
        return LibraryStats(
            totalQuestions = totalQuestions,
            masteredQuestions = masteredQuestions,
            errorBankCount = errorBankCount,
            todayStudyCount = todayStudyCount,
            totalStudyCount = totalStudyCount,
            correctRate = correctRate,
            averageResponseTime = averageResponseTime
        )
    }
    
    data class LibraryStats(
        val totalQuestions: Int,
        val masteredQuestions: Int,
        val errorBankCount: Int,
        val todayStudyCount: Int,
        val totalStudyCount: Int,
        val correctRate: Double,
        val averageResponseTime: Double
    )
}