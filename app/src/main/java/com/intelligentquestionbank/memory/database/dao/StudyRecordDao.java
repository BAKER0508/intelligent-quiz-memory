package com.intelligentquestionbank.memory.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.intelligentquestionbank.memory.database.entities.StudyRecord;
import java.util.List;

@Dao
public interface StudyRecordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StudyRecord studyRecord);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(StudyRecord... studyRecords);
    
    @Update
    void update(StudyRecord studyRecord);
    
    @Delete
    void delete(StudyRecord studyRecord);
    
    @Query("DELETE FROM study_records WHERE id = :recordId")
    void deleteById(long recordId);
    
    @Query("SELECT * FROM study_records WHERE question_id = :questionId")
    StudyRecord getStudyRecordByQuestionId(long questionId);
    
    @Query("SELECT * FROM study_records WHERE question_id = :questionId")
    LiveData<StudyRecord> getStudyRecordByQuestionIdLive(long questionId);
    
    @Query("SELECT * FROM study_records")
    List<StudyRecord> getAllStudyRecords();
    
    @Query("SELECT * FROM study_records")
    LiveData<List<StudyRecord>> getAllStudyRecordsLive();
    
    @Query("SELECT * FROM study_records WHERE is_mastered = 0 ORDER BY next_review_time ASC")
    List<StudyRecord> getActiveStudyRecords();
    
    @Query("SELECT * FROM study_records WHERE is_error = 1")
    List<StudyRecord> getErrorStudyRecords();
    
    @Query("SELECT COUNT(*) FROM study_records WHERE is_mastered = 1")
    LiveData<Integer> getMasteredCount();
    
    @Query("SELECT COUNT(*) FROM study_records WHERE is_error = 1")
    LiveData<Integer> getErrorCount();
    
    @Query("SELECT AVG(CAST(correct_attempts AS REAL) / total_attempts * 100) FROM study_records WHERE total_attempts > 0")
    LiveData<Double> getAverageCorrectRate();
    
    @Query("UPDATE study_records SET is_error = :isError WHERE question_id = :questionId")
    void updateErrorStatus(long questionId, boolean isError);
    
    @Query("UPDATE study_records SET is_mastered = :isMastered WHERE question_id = :questionId")
    void updateMasteredStatus(long questionId, boolean isMastered);
    
    @Query("DELETE FROM study_records WHERE question_id IN " +
           "(SELECT id FROM questions WHERE library_id = :libraryId)")
    void deleteRecordsByLibraryId(long libraryId);
    
    @Query("DELETE FROM study_records")
    void deleteAll();
}