package com.example.protrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.ui.home.HomeFragment;
import com.example.protrack.ui.tasks.TasksFragment;
import com.example.protrack.viewmodel.TaskViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Implementasikan interface OnTaskActionsListener
public class MainActivity extends AppCompatActivity implements OnTaskActionsListener {
    private FloatingActionButton fabAddTask;
    private BottomNavigationView bottomNavigation;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi TaskViewModel di MainActivity
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        initViews();
        setupFAB();
        setupBottomNavigation();

        // Muat HomeFragment sebagai fragment awal saat aplikasi dibuka
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void initViews() {
        fabAddTask = findViewById(R.id.fabAddTask);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupFAB() {
        fabAddTask.setOnClickListener(v -> {
            addNewTask();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_tasks) {
                loadFragment(new TasksFragment());
                return true;
            }
            return false;
        });

        // Set Home sebagai item terpilih secara default
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    // Metode helper untuk memuat fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Penting: Lewatkan listener ke fragment
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).setOnTaskActionsListener(this);
        } else if (fragment instanceof TasksFragment) {
            ((TasksFragment) fragment).setOnTaskActionsListener(this);
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void addNewTask() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    // --- Implementasi metode OnTaskActionsListener ---

    @Override
    public void onDeleteTask(TaskEntity task) {
        new androidx.appcompat.app.AlertDialog.Builder(this) // Gunakan 'this' (MainActivity) sebagai konteks
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete \"" + task.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskViewModel.delete(task);
                    Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEditTask(TaskEntity task) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        startActivity(intent);
    }

    @Override
    public void onDuplicateTask(TaskEntity task) {
        TaskEntity duplicatedTask = new TaskEntity(
                task.getTitle() + " (Copy)",
                task.getDeadline(),
                TaskStatus.PENDING, // Task duplikat biasanya dimulai sebagai PENDING
                task.getNote(),
                task.getPriority()
        );
        taskViewModel.insert(duplicatedTask);
        Toast.makeText(this, "Task duplicated!", Toast.LENGTH_SHORT).show();
    }
}