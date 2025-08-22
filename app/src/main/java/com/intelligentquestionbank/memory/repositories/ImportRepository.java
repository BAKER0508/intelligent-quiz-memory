package com.intelligentquestionbank.memory.repositories;

import androidx.lifecycle.LiveData;
import com.intelligentquestionbank.memory.database.dao.LibraryDao;
import com.intelligentquestionbank.memory.database.dao.QuestionDao;
import com.intelligentquestionbank.memory.database.entities.Question;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import com.intelligentquestionbank.memory.utils.QuestionParser;
import com.intelligentquestionbank.memory.viewmodels.ImportViewModel.ImportCallback;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImportRepository {

    private LibraryDao libraryDao;
    private QuestionDao questionDao;
    private Executor executor = Executors.newFixedThreadPool(4);

    public ImportRepository(LibraryDao libraryDao, QuestionDao questionDao) {
        this.libraryDao = libraryDao;
        this.questionDao = questionDao;
    }

    public LiveData<List<QuestionLibrary>> getAllLibraries() {
        return libraryDao.getAllLibraries();
    }

    public void importQuestions(String content, QuestionLibrary targetLibrary, ImportCallback callback) {
        executor.execute(() -> {
            try {
                // 解析题目
                List<Question> questions = QuestionParser.parseQuestionsFromText(content);
                
                if (questions.size() > 1000) {
                    callback.onResult(false);
                    return;
                }

                // 处理题库
                long libraryId;
                if (targetLibrary.getId() == 0) {
                    // 创建新题库
                    targetLibrary.setTotalQuestions(questions.size());
                    updateQuestionCounts(targetLibrary, questions);
                    libraryId = libraryDao.insert(targetLibrary);
                } else {
                    // 使用现有题库
                    libraryId = targetLibrary.getId();
                    targetLibrary.setTotalQuestions(targetLibrary.getTotalQuestions() + questions.size());
                    updateQuestionCounts(targetLibrary, questions);
                    libraryDao.update(targetLibrary);
                }

                // 设置题目的题库ID并插入
                for (Question question : questions) {
                    question.setLibraryId(libraryId);
                    question.setCreatedAt(System.currentTimeMillis());
                }
                
                questionDao.insertAll(questions.toArray(new Question[0]));
                callback.onResult(true);

            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(false);
            }
        });
    }

    private void updateQuestionCounts(QuestionLibrary library, List<Question> questions) {
        int fillBlankCount = 0;
        int shortAnswerCount = 0;

        for (Question question : questions) {
            if ("fill_in_blank".equals(question.getType())) {
                fillBlankCount++;
            } else {
                shortAnswerCount++;
            }
        }

        library.setFillBlankCount(library.getFillBlankCount() + fillBlankCount);
        library.setShortAnswerCount(library.getShortAnswerCount() + shortAnswerCount);
    }
}