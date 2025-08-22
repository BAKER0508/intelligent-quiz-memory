package com.intelligentquiz.memory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intelligentquiz.memory.R

class AnswerCardAdapter(
    private val onQuestionClick: (Int) -> Unit
) : RecyclerView.Adapter<AnswerCardAdapter.QuestionViewHolder>() {
    
    private var questions = listOf<QuestionState>()
    
    data class QuestionState(
        val number: Int,
        val status: Status
    )
    
    enum class Status {
        UNANSWERED,   // 未答题
        CORRECT,      // 答对
        INCORRECT,    // 答错  
        CURRENT       // 当前题
    }
    
    fun updateQuestions(newQuestions: List<QuestionState>) {
        questions = newQuestions
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_answer_card_question, parent, false)
        return QuestionViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }
    
    override fun getItemCount(): Int = questions.size
    
    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionNumber: TextView = itemView.findViewById(R.id.text_question_number)
        
        fun bind(questionState: QuestionState) {
            questionNumber.text = questionState.number.toString()
            
            val backgroundColor = when (questionState.status) {
                Status.UNANSWERED -> R.color.text_secondary
                Status.CORRECT -> R.color.success_green
                Status.INCORRECT -> R.color.error_red
                Status.CURRENT -> R.color.accent_primary
            }
            
            itemView.setBackgroundColor(itemView.context.getColor(backgroundColor))
            
            itemView.setOnClickListener {
                onQuestionClick(questionState.number - 1)
            }
        }
    }
}