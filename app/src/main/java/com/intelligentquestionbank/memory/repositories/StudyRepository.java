package com.intelligentquestionbank.memory.repositories;

import android.os.AsyncTask;
import com.intelligentquestionbank.memory.database.dao.QuestionDao;
import com.intelligentquestionbank.memory.database.dao.StudyRecordDao;
import com.intelligentquestionbank.memory.database.entities.Question;
import com.intelligentquestionbank.memory.database.entities.StudyRecord;
import com.intelligentquestionbank.memory.utils.EbbinghausAlgorithm;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StudyRepository {

    private QuestionDao questionDao;
    private StudyRecordDao studyRecordDao;
    private Executor executor = Executors.newFixedThreadPool(4);

    public interface QuestionListCallback {
        void onResult(List<Question> questions);
    }

    public StudyRepository(QuestionDao questionDao, StudyRecordDao studyRecordDao) {
        this.questionDao = questionDao;
        this.studyRecordDao = studyRecordDao;
    }

    public void getQuestionsForStudyMode(long libraryId, String studyMode, QuestionListCallback callback) {
        executor.execute(() -> {
            List<Question> questions;
            switch (studyMode) {
                case "sequential":
                    questions = questionDao.getQuestionsByLibraryId(libraryId);
                    break;
                case "random":
                    questions = questionDao.getRandomQuestionsByLibraryId(libraryId);
                    break;
                case "memory":
                    questions = questionDao.getQuestionsForMemoryMode(libraryId);
                    break;
                case "error":
                    questions = questionDao.getErrorQuestionsByLibraryId(libraryId);
                    break;
                case "favorites":
                    questions = questionDao.getFavoriteQuestionsByLibraryId(libraryId);
                    break;
                default:
                    questions = questionDao.getQuestionsByLibraryId(libraryId);
                    break;
            }
            callback.onResult(questions);
        });
    }

    public void updateStudyRecord(long questionId, boolean wasCorrect, String studyMode) {
        executor.execute(() -> {
            StudyRecord record = studyRecordDao.getStudyRecordByQuestionId(questionId);
            if (record == null) {
                record = new StudyRecord();
                record.setQuestionId(questionId);
                record.setTotalAttempts(0);
                record.setCorrectAttempts(0);
                record.setConsecutiveRememberCount(0);
                record.setLastStudyTime(System.currentTimeMillis());
            }

            record.setTotalAttempts(record.getTotalAttempts() + 1);
            if (wasCorrect) {
                record.setCorrectAttempts(record.getCorrectAttempts() + 1);
            }

            if ("memory".equals(studyMode)) {
                EbbinghausAlgorithm.updateForEbbinghausMode(record, wasCorrect);
            } else {
                // 普通练习模式
                if (!wasCorrect) {
                    record.setIsError(true);
                }
            }

            record.setLastStudyTime(System.currentTimeMillis());
            
            if (record.getId() == 0) {
                studyRecordDao.insert(record);
            } else {
                studyRecordDao.update(record);
            }
        });
    }
}