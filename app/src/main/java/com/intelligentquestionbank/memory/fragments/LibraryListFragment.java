package com.intelligentquestionbank.memory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.intelligentquestionbank.memory.R;
import com.intelligentquestionbank.memory.activities.StudyActivity;
import com.intelligentquestionbank.memory.adapters.LibraryAdapter;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;
import com.intelligentquestionbank.memory.viewmodels.LibraryViewModel;

public class LibraryListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LibraryAdapter adapter;
    private LibraryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_list, container, false);
        
        initViews(view);
        initViewModel();
        observeData();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_libraries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new LibraryAdapter();
        adapter.setOnLibraryClickListener(this::onLibraryClick);
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);
    }

    private void observeData() {
        viewModel.getAllLibraries().observe(getViewLifecycleOwner(), libraries -> {
            adapter.submitList(libraries);
        });
    }

    private void onLibraryClick(QuestionLibrary library) {
        Intent intent = new Intent(getActivity(), StudyActivity.class);
        intent.putExtra(StudyActivity.EXTRA_LIBRARY_ID, library.getId());
        intent.putExtra(StudyActivity.EXTRA_LIBRARY_NAME, library.getName());
        startActivity(intent);
    }
}