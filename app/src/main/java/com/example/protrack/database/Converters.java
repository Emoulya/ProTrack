package com.example.protrack.database;

import androidx.room.TypeConverter;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;

public class Converters {

    @TypeConverter
    public static TaskStatus fromTaskStatusString(String value) {
        return value == null ? null : TaskStatus.valueOf(value);
    }

    @TypeConverter
    public static String taskStatusToString(TaskStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static TaskPriority fromTaskPriorityString(String value) {
        return value == null ? null : TaskPriority.valueOf(value);
    }

    @TypeConverter
    public static String taskPriorityToString(TaskPriority priority) {
        return priority == null ? null : priority.name();
    }
}