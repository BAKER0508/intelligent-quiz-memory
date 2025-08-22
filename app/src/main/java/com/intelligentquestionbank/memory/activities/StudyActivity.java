package com.intelligentquestionbank.memory.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.intelligentquestionbank.memory.R;
import com.intelligentquestionbank.memory.database.entities.Question;
import com.intelligentquestionbank.memory.utils.QuestionDisplayHelper;
import com.intelligentquestionbank.memory.viewmodels.StudyViewModel;

public class StudyActivity extends AppCompatActivity {

    public static final String EXTRA_LIBRARY_ID = "library_id";
    public static final String EXTRA_LIBRARY_NAME = "library_name";
    public static final String EXTRA_STUDY_MODE = "study_mode";

    public static final String MODE_SEQUENTIAL = "sequential";
    public static final String MODE_RANDOM = "random";
    public static final String MODE_MEMORY = "memory";
    public static final String MODE_ERROR = "error";
    public static final String MODE_FAVORITES = "favorites";

    private StudyViewModel viewModel;
    private TextView titleView;
    private TextView questionView;
    private LinearLayout memoryTestLayout;
    private LinearLayout answerLayout;
    private Button rememberButton;
    private Button notRememberButton;
    private Button correctButton;
    private Button incorrectButton;
    private TextView answerView;
    private Button nextButton;

    private long libraryId;
    private String libraryName;
    private String studyMode;
    private Question currentQuestion;
    private boolean isMemoryMode;
    private boolean showingMemoryTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        getIntentData();
        initViews();
        initViewModel();
        loadFirstQuestion();
    }

    private void getIntentData() {
        libraryId = getIntent().getLongExtra(EXTRA_LIBRARY_ID, -1);
        libraryName = getIntent().getStringExtra(EXTRA_LIBRARY_NAME);
        studyMode = getIntent().getStringExtra(EXTRA_STUDY_MODE);
        isMemoryMode = MODE_MEMORY.equals(studyMode);
    }

    private void initViews() {
        titleView = findViewById(R.id.tv_study_title);
        questionView = findViewById(R.id.tv_question);
        memoryTestLayout = findViewById(R.id.layout_memory_test);
        answerLayout = findViewById(R.id.layout_answer);
        rememberButton = findViewById(R.id.btn_remember);
        notRememberButton = findViewById(R.id.btn_not_remember);
        correctButton = findViewById(R.id.btn_correct);
        incorrectButton = findViewById(R.id.btn_incorrect);
        answerView = findViewById(R.id.tv_answer);
        nextButton = findViewById(R.id.btn_next);

        titleView.setText(libraryName + " - " + getModeTitle());

        setupClickListeners();
    }

    private String getModeTitle() {
        switch (studyMode) {
            case MODE_SEQUENTIAL: return "顺序练习";
            case MODE_RANDOM: return "随机练习";
            case MODE_MEMORY: return "记忆模式";
            case MODE_ERROR: return "错题练习";
            case MODE_FAVORITES: return "收藏练习";
            default: return "练习";
        }
    }

    private void setupClickListeners() {
        rememberButton.setOnClickListener(v -> onMemoryTestResponse(true));
        notRememberButton.setOnClickListener(v -> onMemoryTestResponse(false));
        correctButton.setOnClickListener(v -> onMemoryVerification(true));
        incorrectButton.setOnClickListener(v -> onMemoryVerification(false));
        nextButton.setOnClickListener(v -> loadNextQuestion());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(StudyViewModel.class);
        viewModel.setLibraryId(libraryId);
        viewModel.setStudyMode(studyMode);
    }

    private void loadFirstQuestion() {
        viewModel.getNextQuestion().observe(this, question -> {
            if (question != null) {
                displayQuestion(question);
            } else {
                showCompletionMessage();
            }
        });
    }

    private void displayQuestion(Question question) {
        this.currentQuestion = question;

        if (isMemoryMode) {
            showMemoryTest();
        } else {
            showNormalPractice();
        }
    }

    private void showMemoryTest() {
        showingMemoryTest = true;
        
        // 显示题目（不显示答案）
        String questionText = QuestionDisplayHelper.getQuestionForMemoryTest(currentQuestion);
        questionView.setText(questionText);
        
        // 显示记忆测试按钮
        memoryTestLayout.setVisibility(View.VISIBLE);
        answerLayout.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private void showNormalPractice() {
        // 显示完整题目和答案
        String questionText = QuestionDisplayHelper.getQuestionWithAnswer(currentQuestion);
        questionView.setText(questionText);
        
        memoryTestLayout.setVisibility(View.GONE);
        answerLayout.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void onMemoryTestResponse(boolean hasMemory) {
        showingMemoryTest = false;
        
        // 显示答案
        String answerText = QuestionDisplayHelper.getAnswerText(currentQuestion);
        answerView.setText(answerText);
        
        // 切换UI状态
        memoryTestLayout.setVisibility(View.GONE);
        answerLayout.setVisibility(View.VISIBLE);
    }

    private void onMemoryVerification(boolean wasCorrect) {
        // 更新学习记录
        viewModel.updateStudyRecord(currentQuestion.getId(), wasCorrect);
        
        // 显示下一题按钮
        answerLayout.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void loadNextQuestion() {
        viewModel.getNextQuestion().observe(this, question -> {
            if (question != null) {
                displayQuestion(question);
            } else {
                showCompletionMessage();
            }
        });
    }

    private void showCompletionMessage() {
        Toast.makeText(this, "练习完成！", Toast.LENGTH_SHORT).show();
        finish();
    }
}