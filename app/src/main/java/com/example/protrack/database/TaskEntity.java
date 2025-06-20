package com.example.protrack.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;

@Entity(tableName = "tasks")
@TypeConverters(Converters.class)
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String deadline;
    private TaskStatus status;
    private String note;
    private TaskPriority priority;
    private boolean isCompleted;
    private long createdAt;
    private long updatedAt;

    public TaskEntity() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        // Pastikan konsistensi awal jika status tidak diinisialisasi secara eksplisit
        this.isCompleted = (this.status == TaskStatus.DONE);
    }

    public TaskEntity(String title, String deadline, TaskStatus status, String note, TaskPriority priority) {
        this.title = title;
        this.deadline = deadline;
        this.status = status;
        this.note = note;
        this.priority = priority;
        this.isCompleted = (status == TaskStatus.DONE); // Nilai isCompleted berdasarkan status yang diberikan
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters (tidak ada perubahan)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public TaskStatus getStatus() { return status; }
    public String getNote() { return note; }
    public TaskPriority getPriority() { return priority; }
    public boolean isCompleted() { return isCompleted; } // Getter tetap ada
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.isCompleted = (status == TaskStatus.DONE); // isCompleted secara otomatis mengikuti status
        this.updatedAt = System.currentTimeMillis();
    }

    public void setNote(String note) {
        this.note = note;
        this.updatedAt = System.currentTimeMillis();
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        this.updatedAt = System.currentTimeMillis();
    }

    // --- HAPUS ATAU MODIFIKASI setCompleted() ---
    public void setCompleted(boolean completed) {
        if (this.isCompleted != completed) { // Hanya update jika ada perubahan
            this.isCompleted = completed;
            // Jika completed, set status ke DONE. Jika tidak, set ke IN_PROGRESS (atau default lain)
            this.status = completed ? TaskStatus.DONE : TaskStatus.IN_PROGRESS;
            this.updatedAt = System.currentTimeMillis();
        }
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}