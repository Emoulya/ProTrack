package com.example.protrack.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    LiveData<List<TaskEntity>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    LiveData<List<TaskEntity>> getActiveTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    LiveData<List<TaskEntity>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    LiveData<List<TaskEntity>> getTasksByPriority(TaskPriority priority);

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY createdAt DESC")
    LiveData<List<TaskEntity>> getTasksByStatus(TaskStatus status);

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :searchQuery || '%' OR note LIKE '%' || :searchQuery || '%'")
    LiveData<List<TaskEntity>> searchTasks(String searchQuery);

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<TaskEntity> getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE id = :id")
    TaskEntity getTaskByIdSync(int id);

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    LiveData<Integer> getActiveTaskCount();

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    LiveData<Integer> getCompletedTaskCount();

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = :priority AND isCompleted = 0")
    LiveData<Integer> getTaskCountByPriority(TaskPriority priority);

    @Query("SELECT COUNT(*) FROM tasks")
    LiveData<Integer> getTotalTaskCount(); // New query to get total task count

    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    LiveData<Integer> getTaskCountByStatus(TaskStatus status); // New query to get count by status

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'IN_PROGRESS'")
    LiveData<Integer> getInProgressTaskCount();

    @Insert
    long insertTask(TaskEntity task);

    @Insert
    void insertTasks(TaskEntity... tasks);

    @Update
    void updateTask(TaskEntity task);

    @Delete
    void deleteTask(TaskEntity task);

    @Query("DELETE FROM tasks WHERE id = :id")
    void deleteTaskById(int id);

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    void deleteCompletedTasks();

    @Query("DELETE FROM tasks")
    void deleteAllTasks();
}