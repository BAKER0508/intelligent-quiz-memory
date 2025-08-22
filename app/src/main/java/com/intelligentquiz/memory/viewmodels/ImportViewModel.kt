package com.intelligentquiz.memory.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intelligentquiz.memory.database.entities.QuestionLibrary
import com.intelligentquiz.memory.repository.QuestionRepository
import kotlinx.coroutines.launch

class ImportViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuestionRepository(application)
    
    private val _libraries = MutableLiveData<List<QuestionLibrary>>()
    val libraries: LiveData<List<QuestionLibrary>> = _libraries
    
    private val _importResult = MutableLiveData<Result<Int>>()
    val importResult: LiveData<Result<Int>> = _importResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun loadLibraries() {
        viewModelScope.launch {
            try {
                repository.getAllLibraries().collect { libraryList ->
                    _libraries.value = libraryList
                }
            } catch (e: Exception) {
                _libraries.value = emptyList()
            }
        }
    }
    
    fun importQuestions(content: String, libraryName: String, createNew: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val importedCount = repository.importQuestions(content, libraryName, createNew)
                _importResult.value = Result.success(importedCount)
                
                // Refresh libraries list after import
                loadLibraries()
            } catch (e: Exception) {
                _importResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}