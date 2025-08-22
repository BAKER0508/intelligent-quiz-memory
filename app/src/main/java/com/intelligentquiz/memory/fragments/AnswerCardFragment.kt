package com.intelligentquiz.memory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.intelligentquiz.memory.databinding.FragmentAnswerCardBinding
import com.intelligentquiz.memory.adapters.AnswerCardAdapter

class AnswerCardFragment : DialogFragment() {
    
    private var _binding: FragmentAnswerCardBinding? = null
    private val binding get() = _binding!!
    
    private var libraryId: Long = -1
    
    companion object {
        private const val ARG_LIBRARY_ID = "library_id"
        
        fun newInstance(libraryId: Long): AnswerCardFragment {
            val fragment = AnswerCardFragment()
            val args = Bundle()
            args.putLong(ARG_LIBRARY_ID, libraryId)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryId = arguments?.getLong(ARG_LIBRARY_ID) ?: -1
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnswerCardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        val adapter = AnswerCardAdapter { questionIndex ->
            // Handle question selection
            dismiss()
            // TODO: Jump to specific question
        }
        
        binding.recyclerViewAnswerCard.layoutManager = GridLayoutManager(context, 6)
        binding.recyclerViewAnswerCard.adapter = adapter
        
        // TODO: Load question states and populate adapter
    }
    
    private fun setupClickListeners() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}