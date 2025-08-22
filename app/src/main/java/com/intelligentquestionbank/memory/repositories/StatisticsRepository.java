package com.intelligentquestionbank.memory.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.intelligentquestionbank.memory.database.dao.LibraryDao;
import com.intelligentquestionbank.memory.database.dao.QuestionDao;
import com.intelligentquestionbank.memory.database.dao.StudyRecordDao;
import com.intelligentquestionbank.memory.database.entities.StudyRecord;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StatisticsRepository {

    private LibraryDao libraryDao;
    private QuestionDao questionDao;
    private StudyRecordDao studyRecordDao;
    private Executor executor = Executors.newFixedThreadPool(4);

    public StatisticsRepository(LibraryDao libraryDao, QuestionDao questionDao, StudyRecordDao studyRecordDao) {
        this.libraryDao = libraryDao;
        this.questionDao = questionDao;
        this.studyRecordDao = studyRecordDao;
    }

    public LiveData<Integer> getTotalLibraries() {
        return libraryDao.getTotalLibraryCount();
    }

    public LiveData<Integer> getTotalQuestions() {
        return questionDao.getTotalQuestionCount();
    }

    public LiveData<Integer> getFillBlankCount() {
        return questionDao.getFillBlankQuestionCount();
    }

    public LiveData<Integer> getShortAnswerCount() {
        return questionDao.getShortAnswerQuestionCount();
    }

    public LiveData<Double> getCorrectRate() {
        MutableLiveData<Double> correctRateLiveData = new MutableLiveData<>();
        
        executor.execute(() -> {
            List<StudyRecord> allRecords = studyRecordDao.getAllStudyRecords();
            if (allRecords.isEmpty()) {
                correctRateLiveData.postValue(0.0);
                return;
            }

            int totalAttempts = 0;
            int totalCorrect = 0;

            for (StudyRecord record : allRecords) {
                totalAttempts += record.getTotalAttempts();
                totalCorrect += record.getCorrectAttempts();
            }

            double correctRate = totalAttempts > 0 ? (double) totalCorrect / totalAttempts * 100 : 0.0;
            correctRateLiveData.postValue(correctRate);
        });

        return correctRateLiveData;
    }
}