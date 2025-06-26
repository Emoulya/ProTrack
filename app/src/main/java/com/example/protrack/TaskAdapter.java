package com.example.protrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;

public class TaskAdapter extends ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder> {

    private OnTaskClickListener onTaskClickListener;
    private OnTaskActionListener onTaskActionListener;

    public TaskAdapter() {
        super(TaskDiffCallback.INSTANCE);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    public void setOnTaskActionListener(OnTaskActionListener listener) {
        this.onTaskActionListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskEntity currentTask = getItem(position);
        holder.bind(currentTask);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPriorityIndicator;
        private TextView tvTaskTitle;
        private TextView tvTaskDeadline;
        private TextView tvTaskStatus;
        private TextView tvTaskNote;
        private TextView btnMarkDone;
        private ImageView ivMoreOptions;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPriorityIndicator = itemView.findViewById(R.id.ivPriorityIndicator);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDeadline = itemView.findViewById(R.id.tvTaskDeadline);
            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
            tvTaskNote = itemView.findViewById(R.id.tvTaskNote);
            btnMarkDone = itemView.findViewById(R.id.btnMarkDone);
            ivMoreOptions = itemView.findViewById(R.id.ivMoreOptions);
        }

        public void bind(TaskEntity task) {
            tvTaskTitle.setText(task.getTitle());
            tvTaskDeadline.setText("Deadline: " + task.getDeadline());
            tvTaskStatus.setText("Status: " + getStatusText(task.getStatus()));
            tvTaskNote.setText("Note: " + task.getNote());

            // Set priority indicator color
            setPriorityIndicator(task.getPriority());

            // Update button text based on completion status
            if (task.isCompleted()) {
                btnMarkDone.setText("Mark as Incomplete");
                btnMarkDone.setBackgroundResource(R.drawable.button_blue_bg);
            } else {
                btnMarkDone.setText("Mark as Done");
                btnMarkDone.setBackgroundResource(R.drawable.button_blue_bg);
            }

            btnMarkDone.setOnClickListener(v -> {
                if (onTaskActionListener != null) {
                    if (task.isCompleted()) {
                        onTaskActionListener.onTaskIncomplete(task);
                    } else {
                        onTaskActionListener.onTaskCompleted(task);
                    }
                }
            });

            ivMoreOptions.setOnClickListener(v -> {
                if (onTaskClickListener != null) {
                    onTaskClickListener.onMoreOptionsClick(task, v);
                }
            });

            itemView.setOnClickListener(v -> {
                if (onTaskClickListener != null) {
                    onTaskClickListener.onTaskClick(task);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onTaskClickListener != null) {
                    onTaskClickListener.onTaskLongClick(task);
                }
                return true;
            });
        }

        private void setPriorityIndicator(TaskPriority priority) {
            switch (priority) {
                case HIGH:
                    ivPriorityIndicator.setBackgroundResource(R.drawable.circle_priority_bg);
                    break;
                case MEDIUM:
                    ivPriorityIndicator.setBackgroundResource(R.drawable.circle_medium_bg);
                    break;
                case LOW:
                    ivPriorityIndicator.setBackgroundResource(R.drawable.circle_low_bg);
                    break;
            }
        }

        private String getStatusText(TaskStatus status) {
            switch (status) {
                case PENDING:
                    return "Pending";
                case IN_PROGRESS:
                    return "In Progress";
                case DONE:
                    return "Done";
                default:
                    return "Unknown";
            }
        }
    }

    // Interface for task click events
    public interface OnTaskClickListener {
        void onTaskClick(TaskEntity task);
        void onTaskLongClick(TaskEntity task);
        void onMoreOptionsClick(TaskEntity task, View anchorView);
    }

    // Interface for task action events
    public interface OnTaskActionListener {
        void onTaskCompleted(TaskEntity task);
        void onTaskIncomplete(TaskEntity task);
    }

    // DiffUtil callback for efficient list updates
    private static final class TaskDiffCallback extends DiffUtil.ItemCallback<TaskEntity> {
        static final TaskDiffCallback INSTANCE = new TaskDiffCallback();

        @Override
        public boolean areItemsTheSame(@NonNull TaskEntity oldItem, @NonNull TaskEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskEntity oldItem, @NonNull TaskEntity newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDeadline().equals(newItem.getDeadline()) &&
                    oldItem.getStatus() == newItem.getStatus() &&
                    oldItem.getNote().equals(newItem.getNote()) &&
                    oldItem.getPriority() == newItem.getPriority() &&
                    oldItem.isCompleted() == newItem.isCompleted() &&
                    oldItem.getUpdatedAt() == newItem.getUpdatedAt();
        }
    }
}