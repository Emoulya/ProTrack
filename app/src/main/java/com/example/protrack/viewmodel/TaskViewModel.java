package com.example.protrack.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.database.TaskEntity;
import com.example.protrack.repository.TaskRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<TaskEntity>> allTasks;
    private LiveData<List<TaskEntity>> activeTasks;
    private LiveData<Integer> totalTaskCount; // New LiveData for total tasks
    private LiveData<Integer> inProgressTaskCount; // Sudah dideklarasikan, sekarang akan diinisialisasi

    private MutableLiveData<TaskPriority> selectedPriorityFilter = new MutableLiveData<>();

    private MediatorLiveData<List<TaskEntity>> filteredTasks = new MediatorLiveData<>();

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
        activeTasks = repository.getActiveTasks();
        totalTaskCount = repository.getTotalTaskCount();
        inProgressTaskCount = repository.getInProgressTaskCount();

        selectedPriorityFilter.setValue(null);

        filteredTasks.addSource(selectedPriorityFilter, filter -> {
            applyFilter();
        });

        filteredTasks.addSource(activeTasks, tasks -> {
            applyFilter();
        });
    }

    // Metode baru untuk menerapkan filter
    private void applyFilter() {
        TaskPriority currentFilter = selectedPriorityFilter.getValue();
        List<TaskEntity> currentActiveTasks = activeTasks.getValue(); // Dapatkan nilai tugas aktif saat ini

        if (currentActiveTasks == null) {
            filteredTasks.setValue(new ArrayList<>()); // Jika belum ada data, set ke list kosong
            return;
        }

        if (currentFilter == null) {
            // Jika filter adalah null (Semua Prioritas), tampilkan semua tugas aktif (incomplete)
            filteredTasks.setValue(currentActiveTasks);
        } else {
            // Jika ada filter prioritas, filter dari daftar tugas aktif (incomplete)
            List<TaskEntity> filteredList = currentActiveTasks.stream()
                    .filter(task -> task.getPriority() == currentFilter)
                    .collect(Collectors.toList());
            filteredTasks.setValue(filteredList);
        }
    }

    // LiveData getters
    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getFilteredTasks() {
        return filteredTasks;
    }

    public LiveData<List<TaskEntity>> getActiveTasks() {
        return activeTasks;
    }

    public LiveData<TaskEntity> getTaskById(int id) {
        return repository.getTaskById(id);
    }

    public LiveData<Integer> getTaskCountByPriority(TaskPriority priority) {
        return repository.getTaskCountByPriority(priority);
    }

    public LiveData<Integer> getTotalTaskCount() {
        return totalTaskCount;
    }

    public LiveData<Integer> getTaskCountByStatus(TaskStatus status) {
        return repository.getTaskCountByStatus(status);
    }

    public LiveData<Integer> getInProgressTaskCount() {
        return inProgressTaskCount;
    }

    // Data modification methods
    public void insert(TaskEntity task) {
        repository.insert(task);
    }

    public void update(TaskEntity task) {
        repository.update(task);
    }

    public void delete(TaskEntity task) {
        repository.delete(task);
    }

    public void markTaskAsCompleted(int id) {
        repository.markTaskAsCompleted(id);
    }

    public void markTaskAsIncomplete(int id, TaskStatus status) {
        repository.markTaskAsIncomplete(id, status);
    }

    public void setTaskPriorityFilter(TaskPriority priority) {
        if (!Objects.equals(selectedPriorityFilter.getValue(), priority)) { // Hindari update jika filter sama
            selectedPriorityFilter.setValue(priority);
        }
    }
}