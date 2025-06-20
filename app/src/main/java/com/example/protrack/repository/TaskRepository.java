package com.example.protrack.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.database.AppDatabase;
import com.example.protrack.database.TaskDao;
import com.example.protrack.database.TaskEntity;
import java.util.List;

public class TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<TaskEntity>> allTasks;
    private LiveData<List<TaskEntity>> activeTasks;
    private LiveData<List<TaskEntity>> completedTasks;
    private LiveData<Integer> activeTaskCount;
    private LiveData<Integer> completedTaskCount;
    private LiveData<Integer> totalTaskCount;
    private final LiveData<Integer> inProgressTaskCount;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
        activeTasks = taskDao.getActiveTasks();
        completedTasks = taskDao.getCompletedTasks();
        activeTaskCount = taskDao.getActiveTaskCount();
        completedTaskCount = taskDao.getCompletedTaskCount();
        totalTaskCount = taskDao.getTotalTaskCount();
        inProgressTaskCount = taskDao.getInProgressTaskCount();
    }

    public LiveData<List<TaskEntity>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<TaskEntity>> getActiveTasks() {
        return activeTasks;
    }

    public LiveData<List<TaskEntity>> getCompletedTasks() {
        return completedTasks;
    }

    public LiveData<Integer> getActiveTaskCount() {
        return activeTaskCount;
    }

    public LiveData<Integer> getCompletedTaskCount() {
        return completedTaskCount;
    }

    public LiveData<List<TaskEntity>> getTasksByPriority(TaskPriority priority) {
        return taskDao.getTasksByPriority(priority);
    }

    public LiveData<List<TaskEntity>> getTasksByStatus(TaskStatus status) {
        return taskDao.getTasksByStatus(status);
    }

    public LiveData<List<TaskEntity>> searchTasks(String searchQuery) {
        return taskDao.searchTasks(searchQuery);
    }

    public LiveData<TaskEntity> getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public LiveData<Integer> getTaskCountByPriority(TaskPriority priority) {
        return taskDao.getTaskCountByPriority(priority);
    }

    public LiveData<Integer> getTotalTaskCount() {
        return totalTaskCount;
    }

    public LiveData<Integer> getTaskCountByStatus(TaskStatus status) {
        return taskDao.getTaskCountByStatus(status);
    }

    public LiveData<Integer> getInProgressTaskCount() {
        return inProgressTaskCount;
    }

    // Database modification methods (must be called on background thread)
    public void insert(TaskEntity task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insertTask(task);
        });
    }

    public void insertTasks(TaskEntity... tasks) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insertTasks(tasks);
        });
    }

    public void update(TaskEntity task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.updateTask(task);
        });
    }

    public void delete(TaskEntity task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteTask(task);
        });
    }

    public void deleteById(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteTaskById(id);
        });
    }

    public void deleteCompletedTasks() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteCompletedTasks();
        });
    }

    public void deleteAllTasks() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteAllTasks();
        });
    }

    public void markTaskAsCompleted(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            TaskEntity task = taskDao.getTaskByIdSync(id); // Ambil task secara sinkron
            if (task != null) {
                task.setStatus(TaskStatus.DONE); // Set status, isCompleted akan otomatis diperbarui
                taskDao.updateTask(task); // Simpan perubahan
            }
        });
    }

    public void markTaskAsIncomplete(int id, TaskStatus newStatus) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            TaskEntity task = taskDao.getTaskByIdSync(id); // Ambil task secara sinkron
            if (task != null) {
                task.setStatus(newStatus); // Set status baru, isCompleted akan otomatis diperbarui
                taskDao.updateTask(task); // Simpan perubahan
            }
        });
    }

    public interface InsertCallback {
        void onInsertComplete(long taskId);
    }

    public void insertWithCallback(TaskEntity task, InsertCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long taskId = taskDao.insertTask(task);
            if (callback != null) {
                callback.onInsertComplete(taskId);
            }
        });
    }
}