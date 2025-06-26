package com.example.protrack.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protrack.OnTaskActionsListener;
import com.example.protrack.R;
import com.example.protrack.TaskAdapter;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.viewmodel.TaskViewModel;
import com.example.protrack.enums.TaskStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment implements
        TaskAdapter.OnTaskClickListener, TaskAdapter.OnTaskActionListener {

    private TextView tvGreeting, tvDate;
    private TextView tvProgress1, tvProgress2, tvProgress3;
    private RecyclerView rvTasks;
    private TaskAdapter homeTaskAdapter;
    private TaskViewModel taskViewModel;
    private OnTaskActionsListener taskActionsListener;
    private ImageView ivFilterPriority;

    public HomeFragment() {
        // Required empty public constructor
    }

    public void setOnTaskActionsListener(OnTaskActionsListener listener) {
        this.taskActionsListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view); // Perlu passing view
        setupRecyclerView();
        setupViewModel();
        setupPriorityFilterButton();
        updateDateTime();
        observeData();
    }

    private void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDate = view.findViewById(R.id.tvDate);
        tvProgress1 = view.findViewById(R.id.tvProgress1);
        tvProgress2 = view.findViewById(R.id.tvProgress2);
        tvProgress3 = view.findViewById(R.id.tvProgress3);
        rvTasks = view.findViewById(R.id.rvTasks);
        ivFilterPriority = view.findViewById(R.id.ivFilterPriority);
    }

    private void setupRecyclerView() {
        homeTaskAdapter = new TaskAdapter();
        homeTaskAdapter.setOnTaskClickListener(this);
        homeTaskAdapter.setOnTaskActionListener(this);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext())); // Gunakan getContext()
        rvTasks.setAdapter(homeTaskAdapter);
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void setupPriorityFilterButton() {
        ivFilterPriority.setOnClickListener(v -> {
            showPriorityFilterMenu(v);
        });
    }

    private void showPriorityFilterMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(getContext(), anchorView);
        popup.getMenuInflater().inflate(R.menu.priority_filter_menu, popup.getMenu()); // Menggunakan menu baru

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            TaskPriority filterPriority = null;

            if (itemId == R.id.filter_all_priority) {
                filterPriority = null; // "Semua Prioritas"
            } else if (itemId == R.id.filter_high_priority) {
                filterPriority = TaskPriority.HIGH;
            } else if (itemId == R.id.filter_medium_priority) {
                filterPriority = TaskPriority.MEDIUM;
            } else if (itemId == R.id.filter_low_priority) {
                filterPriority = TaskPriority.LOW;
            } else {
                return false; // Menu item tidak dikenal
            }

            taskViewModel.setTaskPriorityFilter(filterPriority);
            return true;
        });
        popup.show();
    }

    private void observeData() {
        // Observe active tasks for the main list (untuk Home tab)
        taskViewModel.getFilteredTasks().observe(getViewLifecycleOwner(), tasks -> { // Gunakan LiveData baru
            if (tasks != null) {
                homeTaskAdapter.submitList(tasks);
            }
        });

        // Observe task counts for progress indicators (untuk Home tab)
        taskViewModel.getTaskCountByPriority(TaskPriority.HIGH).observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvProgress1.setText(String.valueOf(count));
            }
        });

        taskViewModel.getTaskCountByPriority(TaskPriority.MEDIUM).observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvProgress2.setText(String.valueOf(count));
            }
        });

        taskViewModel.getTaskCountByPriority(TaskPriority.LOW).observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvProgress3.setText(String.valueOf(count));
            }
        });
    }

    private void updateDateTime() {
        String greeting = getGreetingBasedOnTime();
        tvGreeting.setText(greeting + "👋");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM", new Locale("id", "ID")); // Check locale here
        String currentDate = "Hari ini: " + sdf.format(new Date());
        tvDate.setText(currentDate);
    }

    private String getGreetingBasedOnTime() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) { return "Selamat Pagi"; }
        else if (hour >= 12 && hour < 15) { return "Selamat Siang"; }
        else if (hour >= 15 && hour < 18) { return "Selamat Sore"; }
        else { return "Selamat Malam"; }
    }

    // TaskAdapter.OnTaskClickListener implementation
    @Override
    public void onTaskClick(TaskEntity task) {
        Toast.makeText(getContext(), "Task clicked (Home): " + task.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Open TaskDetailActivity
    }

    @Override
    public void onTaskLongClick(TaskEntity task) {
        Toast.makeText(getContext(), "Task long clicked (Home): " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMoreOptionsClick(TaskEntity task, View anchorView) {
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