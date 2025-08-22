package com.intelligentquiz.memory.database

import android.content.Context
import androidx.room.*
import com.intelligentquiz.memory.database.dao.QuestionDao
import com.intelligentquiz.memory.database.dao.QuestionLibraryDao
import com.intelligentquiz.memory.database.dao.StudyRecordDao
import com.intelligentquiz.memory.database.entities.Question
import com.intelligentquiz.memory.database.entities.QuestionLibrary
import com.intelligentquiz.memory.database.entities.StudyRecord

@Database(
    entities = [QuestionLibrary::class, Question::class, StudyRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun questionLibraryDao(): QuestionLibraryDao
    abstract fun questionDao(): QuestionDao
    abstract fun studyRecordDao(): StudyRecordDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "intelligent_quiz_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}