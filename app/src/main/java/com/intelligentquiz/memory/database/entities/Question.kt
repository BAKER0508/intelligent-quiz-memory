package com.intelligentquiz.memory.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = QuestionLibrary::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("libraryId")]
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val libraryId: Long,
    val type: QuestionType,
    val content: String,
    val answer: String,
    val originalText: String,
    val difficulty: Int = 1,
    val tags: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class QuestionType {
    FILL_IN_BLANK,
    SHORT_ANSWER
}