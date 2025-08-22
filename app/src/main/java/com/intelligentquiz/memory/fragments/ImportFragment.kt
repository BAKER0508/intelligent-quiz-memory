package com.intelligentquiz.memory.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.intelligentquiz.memory.R
import com.intelligentquiz.memory.databinding.FragmentImportBinding
import com.intelligentquiz.memory.utils.QuestionParser
import com.intelligentquiz.memory.viewmodels.ImportViewModel
import kotlinx.coroutines.launch

class ImportFragment : Fragment() {
    
    private var _binding: FragmentImportBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: ImportViewModel
    private var isCreatingNewLibrary = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[ImportViewModel::class.java]
        
        setupSpinner()
        setupTextWatcher()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupSpinner() {
        // Load existing libraries and add "创建新题库" option
        viewModel.loadLibraries()
        
        viewModel.libraries.observe(viewLifecycleOwner) { libraries ->
            val spinnerItems = mutableListOf<String>()
            spinnerItems.add("创建新题库")
            spinnerItems.addAll(libraries.map { it.name })
            
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerItems
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerTargetLibrary.adapter = adapter
            
            binding.spinnerTargetLibrary.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    isCreatingNewLibrary = position == 0
                    binding.editTextNewLibraryName.visibility = if (isCreatingNewLibrary) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
                
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
    
    private fun setupTextWatcher() {
        binding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val content = s?.toString() ?: ""
                updateCharCount(content.length)
                updatePreviewStats(content)
            }
        })
    }
    
    private fun setupClickListeners() {
        binding.buttonImport.setOnClickListener {
            performImport()
        }
    }
    
    private fun observeViewModel() {
        viewModel.importResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "导入成功！共导入${result.getOrNull()}道题目", Toast.LENGTH_LONG).show()
                binding.editTextContent.setText("")
                binding.editTextNewLibraryName.setText("")
                // Return to home fragment
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "导入失败：${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.buttonImport.isEnabled = !isLoading
            binding.buttonImport.text = if (isLoading) "导入中..." else "开始导入"
        }
    }
    
    private fun updateCharCount(count: Int) {
        // Find the char count view in the layout
        // binding.textViewCharCount.text = "字符数: $count / ${QuestionParser.MAX_IMPORT_LENGTH}"
        // For now, we'll skip the char count display since the view references need to be fixed
    }
    
    private fun updatePreviewStats(content: String) {
        lifecycleScope.launch {
            if (content.isEmpty()) {
                // binding.textViewStats.text = "预览统计：\n无内容"
                return@launch
            }
            
            val parseResult = QuestionParser.parseText(content, 0) // temporary library ID
            // binding.textViewStats.text = """
            //     预览统计：
            //     总题数：${parseResult.totalParsed}
            //     填空题：${parseResult.fillInBlankCount}
            //     简答题：${parseResult.shortAnswerCount}
            //     ${if (parseResult.errors.isNotEmpty()) "\n错误：${parseResult.errors.size}处" else ""}
            // """.trimIndent()
        }
    }
    
    private fun performImport() {
        val content = binding.editTextContent.text.toString().trim()
        
        if (content.isEmpty()) {
            Toast.makeText(context, "请输入题目内容", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (content.length > QuestionParser.MAX_IMPORT_LENGTH) {
            Toast.makeText(context, "内容超过${QuestionParser.MAX_IMPORT_LENGTH}字符限制", Toast.LENGTH_SHORT).show()
            return
        }
        
        val libraryName = if (isCreatingNewLibrary) {
            val newName = binding.editTextNewLibraryName.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(context, "请输入新题库名称", Toast.LENGTH_SHORT).show()
                return
            }
            newName
        } else {
            val selectedPosition = binding.spinnerTargetLibrary.selectedItemPosition
            if (selectedPosition <= 0) {
                Toast.makeText(context, "请选择目标题库", Toast.LENGTH_SHORT).show()
                return
            }
            binding.spinnerTargetLibrary.selectedItem.toString()
        }
        
        viewModel.importQuestions(content, libraryName, isCreatingNewLibrary)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}