package com.intelligentquiz.memory.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intelligentquiz.memory.algorithm.EbbinghausAlgorithm
import com.intelligentquiz.memory.database.entities.Question
import com.intelligentquiz.memory.database.entities.StudyRecord
import com.intelligentquiz.memory.repository.QuestionRepository
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = QuestionRepository(application)
    
    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> = _currentQuestion
    
    private val _currentAnswer = MutableLiveData<String>()
    val currentAnswer: LiveData<String> = _currentAnswer
    
    private val _studyState = MutableLiveData<StudyState>()
    val studyState: LiveData<StudyState> = _studyState
    
    private val _progress = MutableLiveData<Progress>()
    val progress: LiveData<Progress> = _progress
    
    private val _studyComplete = MutableLiveData<Boolean>()
    val studyComplete: LiveData<Boolean> = _studyComplete
    
    private var currentLibraryId = -1L
    private var currentStudyMode = ""
    private var questionsToStudy = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var startTime = 0L
    
    enum class StudyState {
        ASKING_MEMORY,      // 询问是否记得
        SHOWING_ANSWER,     // 显示答案
        VERIFYING_MEMORY,   // 验证记忆是否正确
        AUTO_NEXT          // 自动进入下一题
    }
    
    data class Progress(
        val current: Int,
        val total: Int
    )
    
    fun startStudySession(libraryId: Long, studyMode: String) {
        currentLibraryId = libraryId
        currentStudyMode = studyMode
        
        viewModelScope.launch {
            loadQuestions()
            if (questionsToStudy.isNotEmpty()) {
                showNextQuestion()
            } else {
                _studyComplete.value = true
            }
        }
    }
    
    private suspend fun loadQuestions() {
        questionsToStudy.clear()
        
        when (currentStudyMode) {
            "sequential" -> {
                repository.getQuestionsByLibrary(currentLibraryId).collect { questions ->
                    questionsToStudy.addAll(questions)
                }
            }
            "random" -> {
                repository.getQuestionsByLibrary(currentLibraryId).collect { questions ->
                    questionsToStudy.addAll(questions.shuffled())
                }
            }
            "memory" -> {
                // Load unmastered questions for memory mode
                val question = repository.getRandomUnmasteredQuestion(currentLibraryId)
                question?.let { questionsToStudy.add(it) }
            }
            "error_bank" -> {
                val question = repository.getRandomErrorBankQuestion(currentLibraryId)
                question?.let { questionsToStudy.add(it) }
            }
        }
        
        currentQuestionIndex = 0
        updateProgress()
    }
    
    private fun showNextQuestion() {
        if (currentQuestionIndex >= questionsToStudy.size) {
            if (currentStudyMode == "memory" || currentStudyMode == "error_bank") {
                // For memory modes, try to load another question
                viewModelScope.launch {
                    val nextQuestion = if (currentStudyMode == "memory") {
                        repository.getRandomUnmasteredQuestion(currentLibraryId)
                    } else {
                        repository.getRandomErrorBankQuestion(currentLibraryId)
                    }
                    
                    if (nextQuestion != null) {
                        questionsToStudy.add(nextQuestion)
                        continueWithCurrentQuestion()
                    } else {
                        _studyComplete.value = true
                    }
                }
            } else {
                _studyComplete.value = true
            }
            return
        }
        
        continueWithCurrentQuestion()
    }
    
    private fun continueWithCurrentQuestion() {
        val question = questionsToStudy[currentQuestionIndex]
        _currentQuestion.value = question
        _currentAnswer.value = question.answer
        
        startTime = System.currentTimeMillis()
        
        if (currentStudyMode == "memory") {
            _studyState.value = StudyState.ASKING_MEMORY
        } else {
            _studyState.value = StudyState.SHOWING_ANSWER
        }
        
        updateProgress()
    }
    
    fun answerRemember() {
        _studyState.value = StudyState.VERIFYING_MEMORY
    }
    
    fun answerNotRemember() {
        // Record as incorrect and show answer briefly
        recordStudyResult(false)
        _studyState.value = StudyState.AUTO_NEXT
    }
    
    fun answerCorrect() {
        recordStudyResult(true)
        nextQuestion()
    }
    
    fun answerIncorrect() {
        recordStudyResult(false)
        nextQuestion()
    }
    
    fun showAnswer() {
        _studyState.value = StudyState.SHOWING_ANSWER
    }
    
    fun nextQuestion() {
        currentQuestionIndex++
        showNextQuestion()
    }
    
    private fun recordStudyResult(remembered: Boolean) {
        viewModelScope.launch {
            val currentQuestion = _currentQuestion.value ?: return@launch
            val responseTime = System.currentTimeMillis() - startTime
            
            // Get previous record for Ebbinghaus calculation
            val previousRecord = repository.getLatestRecord(currentQuestion.id)
            
            // Calculate next review using Ebbinghaus algorithm
            val reviewResult = EbbinghausAlgorithm.calculateNextReview(
                previousRecord,
                remembered,
                System.currentTimeMillis()
            )
            
            // Create new study record
            val studyRecord = StudyRecord(
                questionId = currentQuestion.id,
                studiedAt = System.currentTimeMillis(),
                remembered = remembered,
                responseTime = responseTime,
                consecutiveCorrect = reviewResult.consecutiveCorrect,
                easinessFactor = reviewResult.easinessFactor,
                nextReviewAt = reviewResult.nextReviewAt,
                intervalDays = reviewResult.intervalDays,
                isInErrorBank = reviewResult.shouldAddToErrorBank
            )
            
            repository.insertStudyRecord(studyRecord)
        }
    }
    
    private fun updateProgress() {
        _progress.value = Progress(
            current = currentQuestionIndex + 1,
            total = questionsToStudy.size
        )
    }
}