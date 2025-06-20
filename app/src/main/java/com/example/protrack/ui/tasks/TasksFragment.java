package com.example.protrack.ui.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protrack.R;
import com.example.protrack.TaskAdapter;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.viewmodel.TaskViewModel;
import android.widget.Toast;
import com.example.protrack.OnTaskActionsListener;

public class TasksFragment extends Fragment implements
        TaskAdapter.OnTaskClickListener, TaskAdapter.OnTaskActionListener {

    private TextView tvAllTaskCount, tvPendingCount, tvInProgressCount, tvDoneCount; // Ringkasan Tugas
    private RecyclerView rvAllTasks;
    private TaskAdapter taskAdapter;
    private TaskViewModel taskViewModel;
    private OnTaskActionsListener taskActionsListener;

    public TasksFragment() {
        // Required empty public constructor
    }

    public void setOnTaskActionsListener(OnTaskActionsListener listener) {
        this.taskActionsListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupViewModel();
        observeData();
    }

    private void initViews(View view) {
        tvAllTaskCount = view.findViewById(R.id.tvAllTaskCount);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvInProgressCount = view.findViewById(R.id.tvInProgressCount);
        tvDoneCount = view.findViewById(R.id.tvDoneCount);
        rvAllTasks = view.findViewById(R.id.rvAllTasks);
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter();
        taskAdapter.setOnTaskClickListener(this);
        taskAdapter.setOnTaskActionListener(this);
        rvAllTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAllTasks.setAdapter(taskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void observeData() {
        // Mengamati semua tugas (aktif, pending, done)
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                taskAdapter.submitList(tasks);
            }
        });

        // Mengamati jumlah total tugas
        taskViewModel.getTotalTaskCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvAllTaskCount.setText(String.valueOf(count));
            }
        });

        // Mengamati jumlah tugas Pending
        taskViewModel.getTaskCountByStatus(TaskStatus.PENDING).observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvPendingCount.setText(String.valueOf(count));
            }
        });

        taskViewModel.getInProgressTaskCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvInProgressCount.setText(String.valueOf(count));
            }
        });

        // Mengamati jumlah tugas Done
        taskViewModel.getTaskCountByStatus(TaskStatus.DONE).observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvDoneCount.setText(String.valueOf(count));
            }
        });
    }

    // --- Implementasi Listener TaskAdapter ---
    @Override
    public void onTaskClick(TaskEntity task) {
        Toast.makeText(getContext(), "Task clicked in TasksFragment: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Arahkan ke TaskDetailActivity
    }

    @Override
    public void onTaskLongClick(TaskEntity task) {
        Toast.makeText(getContext(), "Task long clicked in TasksFragment: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Tampilkan pop-up menu atau opsi lainnya
    }

    @Override
    public void onMoreOptionsClick(TaskEntity task, View anchorView) {
        // Implementasi PopupMenu untuk aksi Edit, Delete, Duplicate
        PopupMenu popup = new PopupMenu(getContext(), anchorView);
        popup.getMenuInflater().inflate(R.menu.task_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (taskActionsListener != null) { // <-- Periksa apakah listener sudah diatur
                if (itemId == R.id.menu_edit) {
                    taskActionsListener.onEditTask(task); // <-- Panggil metode dari Activity
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    taskActionsListener.onDeleteTask(task); // <-- Panggil metode dari Activity
                    return true;
                } else if (itemId == R.id.menu_duplicate) {
                    taskActionsListener.onDuplicateTask(task); // <-- Panggil metode dari Activity
                    return true;
                }
            } else {
                Toast.makeText(getContext(), "Error: Task actions listener not set.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onTaskCompleted(TaskEntity task) {
        taskViewModel.markTaskAsCompleted(task.getId());
    }

    @Override
    public void onTaskIncomplete(TaskEntity task) {
        taskViewModel.markTaskAsIncomplete(task.getId(), TaskStatus.PENDING);
    }
}