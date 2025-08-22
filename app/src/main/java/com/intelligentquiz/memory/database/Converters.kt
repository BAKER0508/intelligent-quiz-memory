package com.intelligentquiz.memory.database

import androidx.room.TypeConverter
import com.intelligentquiz.memory.database.entities.QuestionType

class Converters {
    
    @TypeConverter
    fun fromQuestionType(type: QuestionType): String {
        return type.name
    }
    
    @TypeConverter
    fun toQuestionType(type: String): QuestionType {
        return QuestionType.valueOf(type)
    }
}