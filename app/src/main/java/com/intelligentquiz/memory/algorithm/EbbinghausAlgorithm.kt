package com.intelligentquiz.memory.algorithm

import com.intelligentquiz.memory.database.entities.StudyRecord
import kotlin.math.roundToInt

object EbbinghausAlgorithm {
    
    private const val MIN_EASINESS_FACTOR = 1.3
    private const val MAX_EASINESS_FACTOR = 2.5
    private const val MASTERY_THRESHOLD = 3
    
    data class ReviewResult(
        val nextReviewAt: Long,
        val intervalDays: Int,
        val easinessFactor: Double,
        val consecutiveCorrect: Int,
        val isMastered: Boolean,
        val shouldAddToErrorBank: Boolean
    )
    
    fun calculateNextReview(
        currentRecord: StudyRecord?,
        remembered: Boolean,
        currentTime: Long = System.currentTimeMillis()
    ): ReviewResult {
        
        val previousEF = currentRecord?.easinessFactor ?: 2.5
        val previousInterval = currentRecord?.intervalDays ?: 1
        val previousConsecutive = currentRecord?.consecutiveCorrect ?: 0
        
        if (remembered) {
            val newConsecutive = previousConsecutive + 1
            val newEF = calculateNewEasinessFactor(previousEF, 5)
            
            val newInterval = when (newConsecutive) {
                1 -> 1
                2 -> 6
                else -> (previousInterval * newEF).roundToInt()
            }
            
            val nextReviewAt = currentTime + (newInterval * 24 * 60 * 60 * 1000)
            
            return ReviewResult(
                nextReviewAt = nextReviewAt,
                intervalDays = newInterval,
                easinessFactor = newEF,
                consecutiveCorrect = newConsecutive,
                isMastered = newConsecutive >= MASTERY_THRESHOLD,
                shouldAddToErrorBank = false
            )
            
        } else {
            val newEF = calculateNewEasinessFactor(previousEF, 0)
            val newInterval = 1
            val nextReviewAt = currentTime + (newInterval * 24 * 60 * 60 * 1000)
            
            return ReviewResult(
                nextReviewAt = nextReviewAt,
                intervalDays = newInterval,
                easinessFactor = newEF,
                consecutiveCorrect = 0,
                isMastered = false,
                shouldAddToErrorBank = true
            )
        }
    }
    
    private fun calculateNewEasinessFactor(currentEF: Double, quality: Int): Double {
        val newEF = currentEF + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        return newEF.coerceIn(MIN_EASINESS_FACTOR, MAX_EASINESS_FACTOR)
    }
    
    fun getDueQuestions(records: List<StudyRecord>, currentTime: Long): List<StudyRecord> {
        return records.filter { record ->
            val nextReview = record.nextReviewAt ?: 0
            nextReview <= currentTime && record.consecutiveCorrect < MASTERY_THRESHOLD
        }
    }
    
    fun getStudyPriority(record: StudyRecord, currentTime: Long): Int {
        val nextReview = record.nextReviewAt ?: currentTime
        val overdue = (currentTime - nextReview) / (24 * 60 * 60 * 1000)
        
        return when {
            record.isInErrorBank -> 100 + overdue.toInt()
            overdue > 0 -> 50 + overdue.toInt()
            else -> record.consecutiveCorrect
        }
    }
    
    fun isQuestionMastered(consecutiveCorrect: Int): Boolean {
        return consecutiveCorrect >= MASTERY_THRESHOLD
    }
    
    fun shouldRemoveFromMemoryMode(consecutiveCorrect: Int): Boolean {
        return consecutiveCorrect >= MASTERY_THRESHOLD
    }
    
    fun getMemoryStrength(record: StudyRecord): MemoryStrength {
        return when {
            record.consecutiveCorrect >= MASTERY_THRESHOLD -> MemoryStrength.MASTERED
            record.consecutiveCorrect >= 2 -> MemoryStrength.STRONG
            record.consecutiveCorrect >= 1 -> MemoryStrength.MODERATE
            record.isInErrorBank -> MemoryStrength.ERROR
            else -> MemoryStrength.WEAK
        }
    }
    
    enum class MemoryStrength {
        ERROR,
        WEAK,
        MODERATE,
        STRONG,
        MASTERED
    }
}