package com.example.protrack;

import com.example.protrack.database.TaskEntity;

// Interface ini akan digunakan oleh fragment untuk memicu aksi pada MainActivity
public interface OnTaskActionsListener {
    void onEditTask(TaskEntity task);
    void onDeleteTask(TaskEntity task);
    void onDuplicateTask(TaskEntity task);
}