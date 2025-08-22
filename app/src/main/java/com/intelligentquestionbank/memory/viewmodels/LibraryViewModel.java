package com.intelligentquestionbank.memory.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.intelligentquestionbank.memory.database.AppDatabase;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import com.intelligentquestionbank.memory.repositories.LibraryRepository;
import java.util.List;

public class LibraryViewModel extends AndroidViewModel {

    private LibraryRepository repository;
    private LiveData<List<QuestionLibrary>> allLibraries;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        repository = new LibraryRepository(database.libraryDao(), database.questionDao());
        allLibraries = repository.getAllLibraries();
    }

    public LiveData<List<QuestionLibrary>> getAllLibraries() {
        return allLibraries;
    }

    public void insert(QuestionLibrary library) {
        repository.insert(library);
    }

    public void update(QuestionLibrary library) {
        repository.update(library);
    }

    public void delete(QuestionLibrary library) {
        repository.delete(library);
    }
}