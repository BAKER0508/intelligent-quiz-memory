package com.intelligentquestionbank.memory.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.intelligentquestionbank.memory.database.AppDatabase;
import com.intelligentquestionbank.memory.database.entities.Question;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import com.intelligentquestionbank.memory.repositories.ImportRepository;
import com.intelligentquestionbank.memory.utils.QuestionParser;
import java.util.List;

public class ImportViewModel extends AndroidViewModel {

    private ImportRepository repository;
    private LiveData<List<QuestionLibrary>> allLibraries;

    public interface ImportCallback {
        void onResult(boolean success);
    }

    public ImportViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        repository = new ImportRepository(database.libraryDao(), database.questionDao());
        allLibraries = repository.getAllLibraries();
    }

    public LiveData<List<QuestionLibrary>> getAllLibraries() {
        return allLibraries;
    }

    public void importQuestions(String content, QuestionLibrary targetLibrary, ImportCallback callback) {
        repository.importQuestions(content, targetLibrary, callback);
    }
}