package com.intelligentquiz.memory.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question_libraries")
data class QuestionLibrary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val totalQuestions: Int = 0,
    val completedQuestions: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastStudiedAt: Long? = null,
    val isActive: Boolean = true
)