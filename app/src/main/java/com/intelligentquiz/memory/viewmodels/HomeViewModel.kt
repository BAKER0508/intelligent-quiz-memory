package com.intelligentquiz.memory.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intelligentquiz.memory.database.entities.QuestionLibrary
import com.intelligentquiz.memory.repository.QuestionRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuestionRepository(application)
    
    private val _libraries = MutableLiveData<List<QuestionLibrary>>()
    val libraries: LiveData<List<QuestionLibrary>> = _libraries
    
    private val _overallStats = MutableLiveData<OverallStats>()
    val overallStats: LiveData<OverallStats> = _overallStats
    
    data class OverallStats(
        val totalLibraries: Int,
        val totalQuestions: Int,
        val masteredQuestions: Int,
        val todayStudied: Int
    )
    
    init {
        loadLibraries()
    }
    
    private fun loadLibraries() {
        viewModelScope.launch {
            try {
                repository.getAllLibraries().collect { libraryList ->
                    _libraries.value = libraryList
                    updateOverallStats(libraryList)
                }
            } catch (e: Exception) {
                _libraries.value = emptyList()
                _overallStats.value = OverallStats(0, 0, 0, 0)
            }
        }
    }
    
    private suspend fun updateOverallStats(libraries: List<QuestionLibrary>) {
        var totalQuestions = 0
        var masteredQuestions = 0
        var todayStudied = 0
        
        for (library in libraries) {
            val stats = repository.getLibraryStats(library.id)
            totalQuestions += stats.totalQuestions
            masteredQuestions += stats.masteredQuestions
            todayStudied += stats.todayStudyCount
        }
        
        _overallStats.value = OverallStats(
            totalLibraries = libraries.size,
            totalQuestions = totalQuestions,
            masteredQuestions = masteredQuestions,
            todayStudied = todayStudied
        )
    }
    
    fun refreshData() {
        loadLibraries()
    }
}