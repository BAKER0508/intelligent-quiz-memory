package com.intelligentquestionbank.memory.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import java.util.List;

@Dao
public interface LibraryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(QuestionLibrary library);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(QuestionLibrary... libraries);
    
    @Update
    void update(QuestionLibrary library);
    
    @Delete
    void delete(QuestionLibrary library);
    
    @Query("DELETE FROM libraries WHERE id = :libraryId")
    void deleteById(long libraryId);
    
    @Query("SELECT * FROM libraries ORDER BY created_at DESC")
    LiveData<List<QuestionLibrary>> getAllLibraries();
    
    @Query("SELECT * FROM libraries ORDER BY created_at DESC")
    List<QuestionLibrary> getAllLibrariesSync();
    
    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    QuestionLibrary getLibraryById(long libraryId);
    
    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    LiveData<QuestionLibrary> getLibraryByIdLive(long libraryId);
    
    @Query("SELECT COUNT(*) FROM libraries")
    LiveData<Integer> getTotalLibraryCount();
    
    @Query("SELECT COUNT(*) FROM libraries")
    int getTotalLibraryCountSync();
    
    @Query("SELECT * FROM libraries WHERE name LIKE '%' || :keyword || '%' ORDER BY created_at DESC")
    LiveData<List<QuestionLibrary>> searchLibrariesByName(String keyword);
    
    @Query("DELETE FROM libraries")
    void deleteAll();
}