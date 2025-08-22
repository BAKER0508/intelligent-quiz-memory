package com.intelligentquestionbank.memory.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.intelligentquestionbank.memory.R;
import com.intelligentquestionbank.memory.database.entities.Question;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import com.intelligentquestionbank.memory.utils.QuestionParser;
import com.intelligentquestionbank.memory.viewmodels.ImportViewModel;
import java.util.List;

public class ImportFragment extends Fragment {

    private EditText editTextContent;
    private EditText editTextLibraryName;
    private Spinner spinnerExistingLibrary;
    private Button buttonCreateNew;
    private Button buttonUseExisting;
    private Button buttonImport;
    private TextView textViewStats;
    private TextView textViewCharCount;

    private ImportViewModel viewModel;
    private boolean useNewLibrary = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import, container, false);
        
        initViews(view);
        initViewModel();
        setupListeners();
        observeData();
        
        return view;
    }

    private void initViews(View view) {
        editTextContent = view.findViewById(R.id.edit_text_content);
        editTextLibraryName = view.findViewById(R.id.edit_text_library_name);
        spinnerExistingLibrary = view.findViewById(R.id.spinner_existing_library);
        buttonCreateNew = view.findViewById(R.id.button_create_new);
        buttonUseExisting = view.findViewById(R.id.button_use_existing);
        buttonImport = view.findViewById(R.id.button_import);
        textViewStats = view.findViewById(R.id.text_view_stats);
        textViewCharCount = view.findViewById(R.id.text_view_char_count);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ImportViewModel.class);
    }

    private void setupListeners() {
        buttonCreateNew.setOnClickListener(v -> {
            useNewLibrary = true;
            updateLibrarySelectionUI();
        });

        buttonUseExisting.setOnClickListener(v -> {
            useNewLibrary = false;
            updateLibrarySelectionUI();
        });

        buttonImport.setOnClickListener(v -> performImport());

        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharCount(s.length());
                parseAndShowPreview(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeData() {
        viewModel.getAllLibraries().observe(getViewLifecycleOwner(), libraries -> {
            ArrayAdapter<QuestionLibrary> adapter = new ArrayAdapter<>(
                    getContext(), android.R.layout.simple_spinner_item, libraries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerExistingLibrary.setAdapter(adapter);
        });
    }

    private void updateLibrarySelectionUI() {
        if (useNewLibrary) {
            buttonCreateNew.setBackgroundResource(R.drawable.button_primary);
            buttonUseExisting.setBackgroundResource(R.drawable.button_outline);
            editTextLibraryName.setVisibility(View.VISIBLE);
            spinnerExistingLibrary.setVisibility(View.GONE);
        } else {
            buttonCreateNew.setBackgroundResource(R.drawable.button_outline);
            buttonUseExisting.setBackgroundResource(R.drawable.button_primary);
            editTextLibraryName.setVisibility(View.GONE);
            spinnerExistingLibrary.setVisibility(View.VISIBLE);
        }
    }

    private void updateCharCount(int count) {
        textViewCharCount.setText(String.format("字符数: %d / 100000", count));
        textViewCharCount.setTextColor(count > 100000 ? 
                getResources().getColor(R.color.error_color) : 
                getResources().getColor(R.color.secondary_text_color));
    }

    private void parseAndShowPreview(String content) {
        if (content.trim().isEmpty()) {
            textViewStats.setText("预览统计：\n无内容");
            return;
        }

        try {
            List<Question> questions = QuestionParser.parseQuestionsFromText(content);
            int fillBlankCount = 0;
            int shortAnswerCount = 0;

            for (Question question : questions) {
                if ("fill_in_blank".equals(question.getType())) {
                    fillBlankCount++;
                } else {
                    shortAnswerCount++;
                }
            }

            textViewStats.setText(String.format(
                    "预览统计：\n总题数：%d\n填空题：%d\n简答题：%d",
                    questions.size(), fillBlankCount, shortAnswerCount));

        } catch (Exception e) {
            textViewStats.setText("解析错误：" + e.getMessage());
        }
    }

    private void performImport() {
        String content = editTextContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "请输入题目内容", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.length() > 100000) {
            Toast.makeText(getContext(), "内容超过10万字符限制", Toast.LENGTH_SHORT).show();
            return;
        }

        QuestionLibrary targetLibrary;
        if (useNewLibrary) {
            String libraryName = editTextLibraryName.getText().toString().trim();
            if (libraryName.isEmpty()) {
                Toast.makeText(getContext(), "请输入题库名称", Toast.LENGTH_SHORT).show();
                return;
            }
            targetLibrary = new QuestionLibrary();
            targetLibrary.setName(libraryName);
            targetLibrary.setDescription("通过文本导入创建");
            targetLibrary.setCreatedAt(System.currentTimeMillis());
        } else {
            targetLibrary = (QuestionLibrary) spinnerExistingLibrary.getSelectedItem();
            if (targetLibrary == null) {
                Toast.makeText(getContext(), "请选择目标题库", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        buttonImport.setEnabled(false);
        buttonImport.setText("导入中...");

        viewModel.importQuestions(content, targetLibrary, success -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    buttonImport.setEnabled(true);
                    buttonImport.setText("开始导入");
                    
                    if (success) {
                        Toast.makeText(getContext(), "导入成功！", Toast.LENGTH_SHORT).show();
                        editTextContent.setText("");
                        editTextLibraryName.setText("");
                    } else {
                        Toast.makeText(getContext(), "导入失败，请检查格式", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}