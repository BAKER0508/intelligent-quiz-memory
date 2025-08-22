package com.intelligentquestionbank.memory.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.intelligentquestionbank.memory.R;
import com.intelligentquestionbank.memory.activities.StudyActivity;
import com.intelligentquestionbank.memory.database.entities.QuestionLibrary;

public class LibraryAdapter extends ListAdapter<QuestionLibrary, LibraryAdapter.LibraryViewHolder> {

    private OnLibraryClickListener onLibraryClickListener;

    public interface OnLibraryClickListener {
        void onLibraryClick(QuestionLibrary library);
    }

    public LibraryAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnLibraryClickListener(OnLibraryClickListener listener) {
        this.onLibraryClickListener = listener;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_card, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        QuestionLibrary library = getItem(position);
        holder.bind(library);
    }

    class LibraryViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView descriptionView;
        private TextView questionCountView;
        private TextView fillBlankCountView;
        private TextView shortAnswerCountView;
        private ImageView practiceIcon, randomIcon, memoryIcon, errorIcon, favoriteIcon, addIcon;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.tv_library_title);
            descriptionView = itemView.findViewById(R.id.tv_library_description);
            questionCountView = itemView.findViewById(R.id.tv_question_count);
            fillBlankCountView = itemView.findViewById(R.id.tv_fill_blank_count);
            shortAnswerCountView = itemView.findViewById(R.id.tv_short_answer_count);
            
            practiceIcon = itemView.findViewById(R.id.iv_sequential_practice);
            randomIcon = itemView.findViewById(R.id.iv_random_practice);
            memoryIcon = itemView.findViewById(R.id.iv_memory_mode);
            errorIcon = itemView.findViewById(R.id.iv_error_questions);
            favoriteIcon = itemView.findViewById(R.id.iv_favorites);
            addIcon = itemView.findViewById(R.id.iv_add_questions);

            setupClickListeners();
        }

        public void bind(QuestionLibrary library) {
            titleView.setText(library.getName());
            descriptionView.setText(library.getDescription());
            questionCountView.setText(String.valueOf(library.getTotalQuestions()));
            fillBlankCountView.setText(String.valueOf(library.getFillBlankCount()));
            shortAnswerCountView.setText(String.valueOf(library.getShortAnswerCount()));
        }

        private void setupClickListeners() {
            practiceIcon.setOnClickListener(v -> startStudyActivity(StudyActivity.MODE_SEQUENTIAL));
            randomIcon.setOnClickListener(v -> startStudyActivity(StudyActivity.MODE_RANDOM));
            memoryIcon.setOnClickListener(v -> startStudyActivity(StudyActivity.MODE_MEMORY));
            errorIcon.setOnClickListener(v -> startStudyActivity(StudyActivity.MODE_ERROR));
            favoriteIcon.setOnClickListener(v -> startStudyActivity(StudyActivity.MODE_FAVORITES));
            addIcon.setOnClickListener(v -> {
                // TODO: Navigate to add questions functionality
            });
        }

        private void startStudyActivity(String mode) {
            QuestionLibrary library = getItem(getAdapterPosition());
            Intent intent = new Intent(itemView.getContext(), StudyActivity.class);
            intent.putExtra(StudyActivity.EXTRA_LIBRARY_ID, library.getId());
            intent.putExtra(StudyActivity.EXTRA_LIBRARY_NAME, library.getName());
            intent.putExtra(StudyActivity.EXTRA_STUDY_MODE, mode);
            itemView.getContext().startActivity(intent);
        }
    }

    private static final DiffUtil.ItemCallback<QuestionLibrary> DIFF_CALLBACK = 
            new DiffUtil.ItemCallback<QuestionLibrary>() {
                @Override
                public boolean areItemsTheSame(@NonNull QuestionLibrary oldItem, @NonNull QuestionLibrary newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull QuestionLibrary oldItem, @NonNull QuestionLibrary newItem) {
                    return oldItem.equals(newItem);
                }
            };
}