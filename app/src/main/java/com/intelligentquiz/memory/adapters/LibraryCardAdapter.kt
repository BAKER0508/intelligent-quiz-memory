package com.intelligentquiz.memory.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelligentquiz.memory.databinding.ItemLibraryCardBinding
import com.intelligentquiz.memory.database.entities.QuestionLibrary

class LibraryCardAdapter(
    private val onLibraryClick: (QuestionLibrary) -> Unit
) : ListAdapter<QuestionLibrary, LibraryCardAdapter.LibraryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = ItemLibraryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LibraryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LibraryViewHolder(
        private val binding: ItemLibraryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(library: QuestionLibrary) {
            binding.apply {
                textLibraryName.text = library.name
                textTotalQuestions.text = library.totalQuestions.toString()
                textCompletedQuestions.text = library.completedQuestions.toString()
                
                // Calculate progress
                val progress = if (library.totalQuestions > 0) {
                    (library.completedQuestions.toFloat() / library.totalQuestions * 100).toInt()
                } else {
                    0
                }
                progressBarLibrary.progress = progress
                textProgress.text = "${progress}%"
                
                // Set click listener
                root.setOnClickListener {
                    onLibraryClick(library)
                }
                
                // Set long click for options menu
                root.setOnLongClickListener {
                    // Show options menu for rename/delete
                    true
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<QuestionLibrary>() {
            override fun areItemsTheSame(
                oldItem: QuestionLibrary,
                newItem: QuestionLibrary
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: QuestionLibrary,
                newItem: QuestionLibrary
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}