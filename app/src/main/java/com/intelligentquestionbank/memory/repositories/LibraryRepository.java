package com.intelligentquestionbank.memory.repositories;

import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.intelligentquestionbank.memory.database.dao.LibraryDao;
import com.intelligentquestionbank.memory.database.dao.QuestionDao;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import java.util.List;

public class LibraryRepository {

    private LibraryDao libraryDao;
    private QuestionDao questionDao;
    private LiveData<List<QuestionLibrary>> allLibraries;

    public LibraryRepository(LibraryDao libraryDao, QuestionDao questionDao) {
        this.libraryDao = libraryDao;
        this.questionDao = questionDao;
        this.allLibraries = libraryDao.getAllLibraries();
    }

    public LiveData<List<QuestionLibrary>> getAllLibraries() {
        return allLibraries;
    }

    public void insert(QuestionLibrary library) {
        new InsertAsyncTask(libraryDao).execute(library);
    }

    public void update(QuestionLibrary library) {
        new UpdateAsyncTask(libraryDao).execute(library);
    }

    public void delete(QuestionLibrary library) {
        new DeleteAsyncTask(libraryDao).execute(library);
    }

    private static class InsertAsyncTask extends AsyncTask<QuestionLibrary, Void, Void> {
        private LibraryDao asyncTaskDao;

        InsertAsyncTask(LibraryDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final QuestionLibrary... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<QuestionLibrary, Void, Void> {
        private LibraryDao asyncTaskDao;

        UpdateAsyncTask(LibraryDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final QuestionLibrary... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<QuestionLibrary, Void, Void> {
        private LibraryDao asyncTaskDao;

        DeleteAsyncTask(LibraryDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final QuestionLibrary... params) {
            asyncTaskDao.delete(params[0]);
            return null;
        }
    }
}