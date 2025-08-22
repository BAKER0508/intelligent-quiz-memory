package com.intelligentquiz.memory.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.intelligentquiz.memory.R
import com.intelligentquiz.memory.databinding.ActivityStudyBinding
import com.intelligentquiz.memory.fragments.AnswerCardFragment
import com.intelligentquiz.memory.viewmodels.StudyViewModel

class StudyActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStudyBinding
    private lateinit var viewModel: StudyViewModel
    private var libraryId: Long = -1
    private var studyMode: String = ""
    private var answerDisplayTime = 3000L // 3 seconds default
    
    companion object {
        const val EXTRA_LIBRARY_ID = "library_id"
        const val EXTRA_STUDY_MODE = "study_mode"
        const val MODE_SEQUENTIAL = "sequential"
        const val MODE_RANDOM = "random"
        const val MODE_MEMORY = "memory"
        const val MODE_ERROR_BANK = "error_bank"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        libraryId = intent.getLongExtra(EXTRA_LIBRARY_ID, -1)
        studyMode = intent.getStringExtra(EXTRA_STUDY_MODE) ?: MODE_SEQUENTIAL
        
        if (libraryId == -1L) {
            Toast.makeText(this, "无效的题库ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        viewModel = ViewModelProvider(this)[StudyViewModel::class.java]
        
        setupToolbar()
        setupClickListeners()
        observeViewModel()
        
        viewModel.startStudySession(libraryId, studyMode)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = when(studyMode) {
            MODE_SEQUENTIAL -> "顺序练习"
            MODE_RANDOM -> "随机练习"
            MODE_MEMORY -> "记忆模式"
            MODE_ERROR_BANK -> "错题集"
            else -> "学习模式"
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonBack.setOnClickListener {
            finish()
        }
        
        binding.buttonAnswerCard.setOnClickListener {
            showAnswerCard()
        }
        
        binding.buttonRemember.setOnClickListener {
            viewModel.answerRemember()
        }
        
        binding.buttonNotRemember.setOnClickListener {
            viewModel.answerNotRemember()
        }
        
        binding.buttonCorrect.setOnClickListener {
            viewModel.answerCorrect()
        }
        
        binding.buttonIncorrect.setOnClickListener {
            viewModel.answerIncorrect()
        }
        
        binding.buttonNext.setOnClickListener {
            viewModel.nextQuestion()
        }
        
        binding.buttonShowAnswer.setOnClickListener {
            viewModel.showAnswer()
        }
    }
    
    private fun observeViewModel() {
        viewModel.currentQuestion.observe(this) { question ->
            if (question != null) {
                binding.textQuestionContent.text = question.content
                resetAnswerDisplay()
            }
        }
        
        viewModel.studyState.observe(this) { state ->
            updateUI(state)
        }
        
        viewModel.progress.observe(this) { progress ->
            binding.textProgress.text = "${progress.current}/${progress.total}"
            binding.progressBar.progress = if (progress.total > 0) {
                (progress.current * 100 / progress.total)
            } else 0
        }
        
        viewModel.currentAnswer.observe(this) { answer ->
            binding.textAnswer.text = answer
        }
        
        viewModel.studyComplete.observe(this) { isComplete ->
            if (isComplete) {
                Toast.makeText(this, "学习完成！", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    
    private fun updateUI(state: StudyViewModel.StudyState) {
        when (state) {
            StudyViewModel.StudyState.ASKING_MEMORY -> {
                // Memory mode: ask if user remembers
                binding.layoutMemoryButtons.visibility = View.VISIBLE
                binding.layoutVerifyButtons.visibility = View.GONE
                binding.layoutAnswerDisplay.visibility = View.GONE
                binding.buttonNext.visibility = View.GONE
                binding.buttonShowAnswer.visibility = if (studyMode == MODE_MEMORY) View.GONE else View.VISIBLE
            }
            
            StudyViewModel.StudyState.SHOWING_ANSWER -> {
                // Show answer and wait for next action
                binding.layoutMemoryButtons.visibility = View.GONE
                binding.layoutVerifyButtons.visibility = View.GONE
                binding.layoutAnswerDisplay.visibility = View.VISIBLE
                binding.buttonNext.visibility = View.VISIBLE
                binding.buttonShowAnswer.visibility = View.GONE
            }
            
            StudyViewModel.StudyState.VERIFYING_MEMORY -> {
                // Ask if memory was correct
                binding.layoutMemoryButtons.visibility = View.GONE
                binding.layoutVerifyButtons.visibility = View.VISIBLE
                binding.layoutAnswerDisplay.visibility = View.VISIBLE
                binding.buttonNext.visibility = View.GONE
                binding.buttonShowAnswer.visibility = View.GONE
            }
            
            StudyViewModel.StudyState.AUTO_NEXT -> {
                // Auto-advance after showing answer for "not remember"
                binding.layoutMemoryButtons.visibility = View.GONE
                binding.layoutVerifyButtons.visibility = View.GONE
                binding.layoutAnswerDisplay.visibility = View.VISIBLE
                binding.buttonNext.visibility = View.GONE
                binding.buttonShowAnswer.visibility = View.GONE
                
                // Auto advance after display time
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.nextQuestion()
                }, answerDisplayTime)
            }
        }
    }
    
    private fun resetAnswerDisplay() {
        binding.layoutAnswerDisplay.visibility = View.GONE
        binding.layoutMemoryButtons.visibility = if (studyMode == MODE_MEMORY) View.VISIBLE else View.GONE
        binding.layoutVerifyButtons.visibility = View.GONE
        binding.buttonNext.visibility = if (studyMode == MODE_MEMORY) View.GONE else View.VISIBLE
        binding.buttonShowAnswer.visibility = if (studyMode == MODE_MEMORY) View.GONE else View.VISIBLE
    }
    
    private fun showAnswerCard() {
        val fragment = AnswerCardFragment.newInstance(libraryId)
        fragment.show(supportFragmentManager, "answer_card")
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}