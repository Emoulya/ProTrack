package com.example.protrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.protrack.database.TaskEntity;
import com.example.protrack.enums.TaskPriority;
import com.example.protrack.enums.TaskStatus;
import com.example.protrack.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etTaskNote;
    private TextView tvDeadline; // Ini TextView, bukan TextInputEditText lagi
    private Spinner spPriority, spStatus;
    private Button btnSave, btnCancel, btnSelectDeadline;
    private TaskViewModel taskViewModel;

    private Calendar selectedDeadline;
    private SimpleDateFormat dateTimeFormat;

    private int taskId = -1; // Default: -1 menandakan mode "tambah baru"
    private TaskEntity currentTask; // Untuk menyimpan data task yang sedang diedit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupSpinners();
        setupViewModel();
        initializeDateTime();
        setupClickListeners();

        // --- PENTING: Deteksi Mode Edit/Tambah Baru ---
        android.content.Intent intent = getIntent();
        if (intent != null && intent.hasExtra("TASK_ID")) {
            taskId = intent.getIntExtra("TASK_ID", -1);
            if (taskId != -1) {
                // Mode Edit: Muat data task
                loadTaskDataForEdit();
                btnSave.setText("UPDATE"); // Ganti teks tombol jika mode edit
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Edit Task"); // Ubah judul toolbar
                }
            }
        }
        // --- Akhir Deteksi Mode ---
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskNote = findViewById(R.id.etTaskNote);
        tvDeadline = findViewById(R.id.tvDeadline); // ID ini sesuai dengan layout baru
        spPriority = findViewById(R.id.spPriority); // ID ini sesuai dengan layout baru
        spStatus = findViewById(R.id.spStatus);     // ID ini sesuai dengan layout baru
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnSelectDeadline = findViewById(R.id.btnSelectDeadline);
    }

    private void setupSpinners() {
        // Spinner Prioritas
        // SARAN: Gunakan Enum.stringValues() daripada hardcode array
        // String[] priorities = {"LOW", "MEDIUM", "HIGH"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskPriority.stringValues());
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(priorityAdapter);
        spPriority.setSelection(TaskPriority.MEDIUM.ordinal()); // Default ke MEDIUM (gunakan ordinal)

        // Spinner Status
        // SARAN: Gunakan Enum.stringValues() daripada hardcode array
        // String[] statuses = {"PENDING", "IN_PROGRESS", "DONE"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskStatus.stringValues());
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);
        spStatus.setSelection(TaskStatus.PENDING.ordinal()); // Default ke PENDING (gunakan ordinal)
    }

    private void setupViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    private void initializeDateTime() {
        selectedDeadline = Calendar.getInstance();
        // Set deadline default ke besok hari, pada jam 23:59
        selectedDeadline.add(Calendar.DAY_OF_MONTH, 1);
        selectedDeadline.set(Calendar.HOUR_OF_DAY, 23);
        selectedDeadline.set(Calendar.MINUTE, 59);
        selectedDeadline.set(Calendar.SECOND, 0);
        selectedDeadline.set(Calendar.MILLISECOND, 0);

        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        updateDeadlineDisplay();
    }

    private void setupClickListeners() {
        btnSelectDeadline.setOnClickListener(v -> showDateTimePicker());
        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Judul akan diatur di onCreate berdasarkan mode
            // getSupportActionBar().setTitle("Tambah Task Baru");
        }
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDeadline.set(Calendar.YEAR, year);
                    selectedDeadline.set(Calendar.MONTH, month);
                    selectedDeadline.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker();
                },
                selectedDeadline.get(Calendar.YEAR),
                selectedDeadline.get(Calendar.MONTH),
                selectedDeadline.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDeadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDeadline.set(Calendar.MINUTE, minute);
                    updateDeadlineDisplay();
                },
                selectedDeadline.get(Calendar.HOUR_OF_DAY),
                selectedDeadline.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDeadlineDisplay() {
        String deadlineText = dateTimeFormat.format(selectedDeadline.getTime());
        tvDeadline.setText(deadlineText);
    }

    // --- PENTING: Metode untuk memuat data task yang sudah ada (sesuaikan dengan TextView/Spinner) ---
    private void loadTaskDataForEdit() {
        taskViewModel.getTaskById(taskId).observe(this, task -> {
            if (task != null) {
                currentTask = task; // Simpan task yang sedang diedit
                etTaskTitle.setText(task.getTitle());
                tvDeadline.setText(task.getDeadline()); // Sekarang TextView, bukan EditText
                etTaskNote.setText(task.getNote());

                // Set spinner Priority
                String priorityName = task.getPriority().name();
                ArrayAdapter<String> priorityAdapter = (ArrayAdapter<String>) spPriority.getAdapter();
                if (priorityAdapter != null) {
                    int spinnerPosition = priorityAdapter.getPosition(priorityName);
                    spPriority.setSelection(spinnerPosition);
                }

                // Set spinner Status
                String statusName = task.getStatus().name();
                ArrayAdapter<String> statusAdapter = (ArrayAdapter<String>) spStatus.getAdapter();
                if (statusAdapter != null) {
                    int spinnerPosition = statusAdapter.getPosition(statusName);
                    spStatus.setSelection(spinnerPosition);
                }

                // Update Calendar instance with task's deadline for DateTimePicker
                try {
                    java.util.Date deadlineDate = dateTimeFormat.parse(task.getDeadline());
                    if (deadlineDate != null) {
                        selectedDeadline.setTime(deadlineDate);
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error parsing deadline date.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Task not found.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString().trim();
        String note = etTaskNote.getText().toString().trim();
        String deadline = tvDeadline.getText().toString().trim(); // Ambil dari TextView

        // Validasi input judul task
        if (TextUtils.isEmpty(title)) {
            etTaskTitle.setError("Judul task tidak boleh kosong");
            etTaskTitle.requestFocus();
            return;
        }
        // Validasi deadline (jika perlu)
        if (TextUtils.isEmpty(deadline) || !isValidDate(deadline)) { // Implementasikan isValidDate
            Toast.makeText(this, "Deadline tidak valid atau kosong", Toast.LENGTH_SHORT).show();
            return;
        }


        TaskPriority priority = TaskPriority.valueOf(spPriority.getSelectedItem().toString());
        TaskStatus status = TaskStatus.valueOf(spStatus.getSelectedItem().toString());


        if (taskId == -1) {
            // Mode Tambah Baru
            TaskEntity newTask = new TaskEntity(title, deadline, status, note, priority);
            taskViewModel.insert(newTask);
            Toast.makeText(this, "Task baru ditambahkan!", Toast.LENGTH_SHORT).show();
        } else {
            // Mode Edit
            if (currentTask != null) {
                currentTask.setTitle(title);
                currentTask.setDeadline(deadline);
                currentTask.setNote(note);
                currentTask.setPriority(priority);
                currentTask.setStatus(status); // TaskEntity akan mengelola isCompleted secara otomatis
                taskViewModel.update(currentTask);
                Toast.makeText(this, "Task diperbarui!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error: Task tidak ditemukan untuk diperbarui.", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    // Metode bantuan untuk validasi tanggal (opsional, tapi bagus untuk robustness)
    private boolean isValidDate(String dateStr) {
        dateTimeFormat.setLenient(false); // Pastikan parsing ketat
        try {
            dateTimeFormat.parse(dateStr);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}