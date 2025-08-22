package com.intelligentquestionbank.memory.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.intelligentquestionbank.memory.database.AppDatabase;
import com.intelligentquestionbank.memory.repositories.StatisticsRepository;

public class StatisticsViewModel extends AndroidViewModel {

    private StatisticsRepository repository;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        repository = new StatisticsRepository(
                database.libraryDao(), 
                database.questionDao(), 
                database.studyRecordDao()
        );
    }

    public LiveData<Integer> getTotalLibraries() {
        return repository.getTotalLibraries();
    }

    public LiveData<Integer> getTotalQuestions() {
        return repository.getTotalQuestions();
    }

    public LiveData<Integer> getFillBlankCount() {
        return repository.getFillBlankCount();
    }

    public LiveData<Integer> getShortAnswerCount() {
        return repository.getShortAnswerCount();
    }

    public LiveData<Double> getCorrectRate() {
        return repository.getCorrectRate();
    }
}