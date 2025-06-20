package com.example.protrack.enums;

public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    DONE,
    CANCELLED; // Contoh tambahan

    // Tambahkan metode ini
    public static String[] stringValues() {
        TaskStatus[] values = TaskStatus.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}
