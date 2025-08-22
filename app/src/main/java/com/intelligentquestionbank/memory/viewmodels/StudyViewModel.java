package com.intelligentquestionbank.memory.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.intelligentquestionbank.memory.database.AppDatabase;
import com.intelligentquestionbank.memory.database.entities.Question;
import com.intelligentquestionbank.memory.repositories.StudyRepository;
import java.util.List;

public class StudyViewModel extends AndroidViewModel {

    private StudyRepository repository;
    private MutableLiveData<Question> currentQuestion = new MutableLiveData<>();
    
    private long libraryId;
    private String studyMode;
    private List<Question> questionList;
    private int currentIndex = 0;

    public StudyViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        repository = new StudyRepository(database.questionDao(), database.studyRecordDao());
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public void setStudyMode(String studyMode) {
        this.studyMode = studyMode;
        loadQuestions();
    }

    private void loadQuestions() {
        repository.getQuestionsForStudyMode(libraryId, studyMode, questions -> {
            questionList = questions;
            currentIndex = 0;
            if (!questions.isEmpty()) {
                currentQuestion.setValue(questions.get(0));
            } else {
                currentQuestion.setValue(null);
            }
        });
    }

    public LiveData<Question> getNextQuestion() {
        if (questionList != null && currentIndex < questionList.size()) {
            Question question = questionList.get(currentIndex);
            currentQuestion.setValue(question);
            currentIndex++;
            return currentQuestion;
        } else {
            currentQuestion.setValue(null);
            return currentQuestion;
        }
    }

    public void updateStudyRecord(long questionId, boolean wasCorrect) {
        repository.updateStudyRecord(questionId, wasCorrect, studyMode);
    }
}