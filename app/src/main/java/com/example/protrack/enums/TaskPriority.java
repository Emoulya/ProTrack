package com.example.protrack.enums;

public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH;

    // Tambahkan metode ini
    public static String[] stringValues() {
        TaskPriority[] values = TaskPriority.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}
