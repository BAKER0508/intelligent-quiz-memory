package com.intelligentquiz.memory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.intelligentquiz.memory.R
import com.intelligentquiz.memory.adapters.LibraryCardAdapter
import com.intelligentquiz.memory.databinding.FragmentHomeBinding
import com.intelligentquiz.memory.viewmodels.HomeViewModel

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var libraryAdapter: LibraryCardAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        libraryAdapter = LibraryCardAdapter { library ->
            onLibraryClick(library)
        }
        
        binding.recyclerViewLibraries.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = libraryAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.buttonImportLibrary.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ImportFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun onLibraryClick(library: QuestionLibrary) {
        // For now, start memory mode by default
        val intent = android.content.Intent(context, com.intelligentquiz.memory.activities.StudyActivity::class.java).apply {
            putExtra(com.intelligentquiz.memory.activities.StudyActivity.EXTRA_LIBRARY_ID, library.id)
            putExtra(com.intelligentquiz.memory.activities.StudyActivity.EXTRA_STUDY_MODE, "memory")
        }
        startActivity(intent)
    }
    
    private fun observeViewModel() {
        viewModel.libraries.observe(viewLifecycleOwner) { libraries ->
            libraryAdapter.submitList(libraries)
            updateOverallStats(libraries)
        }
        
        viewModel.overallStats.observe(viewLifecycleOwner) { stats ->
            binding.textTotalLibraries.text = stats.totalLibraries.toString()
            binding.textTotalQuestions.text = stats.totalQuestions.toString()
            binding.textMasteredQuestions.text = stats.masteredQuestions.toString()
            binding.textTodayStudied.text = stats.todayStudied.toString()
        }
    }
    
    private fun updateOverallStats(libraries: List<Any>) {
        // This will be populated by the adapter data
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}