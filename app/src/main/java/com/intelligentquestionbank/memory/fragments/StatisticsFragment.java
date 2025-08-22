package com.intelligentquestionbank.memory.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.intelligentquestionbank.memory.R;
import com.intelligentquestionbank.memory.viewmodels.StatisticsViewModel;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel viewModel;
    private TextView tvTotalLibraries;
    private TextView tvTotalQuestions;
    private TextView tvFillBlankCount;
    private TextView tvShortAnswerCount;
    private TextView tvTotalStudyTime;
    private TextView tvCorrectRate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        
        initViews(view);
        initViewModel();
        observeData();
        
        return view;
    }

    private void initViews(View view) {
        tvTotalLibraries = view.findViewById(R.id.tv_total_libraries);
        tvTotalQuestions = view.findViewById(R.id.tv_total_questions);
        tvFillBlankCount = view.findViewById(R.id.tv_fill_blank_count);
        tvShortAnswerCount = view.findViewById(R.id.tv_short_answer_count);
        tvTotalStudyTime = view.findViewById(R.id.tv_total_study_time);
        tvCorrectRate = view.findViewById(R.id.tv_correct_rate);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
    }

    private void observeData() {
        viewModel.getTotalLibraries().observe(getViewLifecycleOwner(), count -> 
                tvTotalLibraries.setText(String.valueOf(count)));
        
        viewModel.getTotalQuestions().observe(getViewLifecycleOwner(), count -> 
                tvTotalQuestions.setText(String.valueOf(count)));
        
        viewModel.getFillBlankCount().observe(getViewLifecycleOwner(), count -> 
                tvFillBlankCount.setText(String.valueOf(count)));
        
        viewModel.getShortAnswerCount().observe(getViewLifecycleOwner(), count -> 
                tvShortAnswerCount.setText(String.valueOf(count)));
        
        viewModel.getCorrectRate().observe(getViewLifecycleOwner(), rate -> 
                tvCorrectRate.setText(String.format("%.1f%%", rate)));
    }
}