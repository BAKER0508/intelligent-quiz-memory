package com.intelligentquestionbank.memory.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.intelligentquestionbank.memory.database.entities.Question;
import java.util.List;

@Dao
public interface QuestionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Question question);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Question... questions);
    
    @Update
    void update(Question question);
    
    @Delete
    void delete(Question question);
    
    @Query("DELETE FROM questions WHERE id = :questionId")
    void deleteById(long questionId);
    
    @Query("SELECT * FROM questions WHERE library_id = :libraryId ORDER BY id ASC")
    List<Question> getQuestionsByLibraryId(long libraryId);
    
    @Query("SELECT * FROM questions WHERE library_id = :libraryId ORDER BY RANDOM()")
    List<Question> getRandomQuestionsByLibraryId(long libraryId);
    
    @Query("SELECT q.* FROM questions q " +
           "LEFT JOIN study_records sr ON q.id = sr.question_id " +
           "WHERE q.library_id = :libraryId AND (sr.is_mastered = 0 OR sr.is_mastered IS NULL) " +
           "ORDER BY sr.next_review_time ASC")
    List<Question> getQuestionsForMemoryMode(long libraryId);
    
    @Query("SELECT q.* FROM questions q " +
           "INNER JOIN study_records sr ON q.id = sr.question_id " +
           "WHERE q.library_id = :libraryId AND sr.is_error = 1 " +
           "ORDER BY sr.last_study_time DESC")
    List<Question> getErrorQuestionsByLibraryId(long libraryId);
    
    @Query("SELECT * FROM questions WHERE library_id = :libraryId AND is_favorite = 1 ORDER BY id ASC")
    List<Question> getFavoriteQuestionsByLibraryId(long libraryId);
    
    @Query("SELECT COUNT(*) FROM questions")
    LiveData<Integer> getTotalQuestionCount();
    
    @Query("SELECT COUNT(*) FROM questions WHERE type = 'fill_in_blank'")
    LiveData<Integer> getFillBlankQuestionCount();
    
    @Query("SELECT COUNT(*) FROM questions WHERE type = 'short_answer'")
    LiveData<Integer> getShortAnswerQuestionCount();
    
    @Query("SELECT COUNT(*) FROM questions WHERE library_id = :libraryId")
    int getQuestionCountByLibraryId(long libraryId);
    
    @Query("SELECT COUNT(*) FROM questions WHERE library_id = :libraryId AND type = 'fill_in_blank'")
    int getFillBlankCountByLibraryId(long libraryId);
    
    @Query("SELECT COUNT(*) FROM questions WHERE library_id = :libraryId AND type = 'short_answer'")
    int getShortAnswerCountByLibraryId(long libraryId);
    
    @Query("SELECT * FROM questions WHERE library_id = :libraryId AND id = :questionId")
    Question getQuestionById(long libraryId, long questionId);
    
    @Query("UPDATE questions SET is_favorite = :isFavorite WHERE id = :questionId")
    void updateFavoriteStatus(long questionId, boolean isFavorite);
    
    @Query("DELETE FROM questions WHERE library_id = :libraryId")
    void deleteQuestionsByLibraryId(long libraryId);
    
    @Query("DELETE FROM questions")
    void deleteAll();
}