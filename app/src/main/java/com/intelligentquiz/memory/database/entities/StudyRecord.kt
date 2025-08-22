package com.intelligentquiz.memory.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "study_records",
    foreignKeys = [
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
data class StudyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionId: Long,
    val studiedAt: Long = System.currentTimeMillis(),
    val remembered: Boolean,
    val responseTime: Long = 0,
    val consecutiveCorrect: Int = 0,
    val easinessFactor: Double = 2.5,
    val nextReviewAt: Long? = null,
    val intervalDays: Int = 1,
    val isInErrorBank: Boolean = false
)