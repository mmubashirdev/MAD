package com.example.studentdirectory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity
        implements StudentAdapter.OnStudentActionListener {

    RecyclerView recyclerViewStudents;
    StudentAdapter adapter;

    List<Student> studentList = new ArrayList<>();

    StudentDao dao;
    ActivityLogDbHelper logDb;
    AppPrefs prefs;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private int selectedPositionForContextMenu = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = new AppPrefs(this);
        logDb = new ActivityLogDbHelper(this);
        dao = AppDatabase.getInstance(this).studentDao();

        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentList, this);
        recyclerViewStudents.setAdapter(adapter);
        registerForContextMenu(recyclerViewStudents);

        int launches = prefs.incrementLaunchCount();
        Toast.makeText(this,
                launches == 1 ? "Welcome! This is your first launch."
                        : "Welcome back! App opened " + launches + " times.",
                Toast.LENGTH_SHORT).show();

        loadStudents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void loadStudents() {
        executor.execute(() -> {
            seedIfFirstRun();

            List<Student> result = prefs.isSortByName()
                    ? dao.getAllSortedByName()
                    : dao.getAll();

            runOnUiThread(() -> {
                studentList.clear();
                studentList.addAll(result);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void seedIfFirstRun() {
        if (prefs.isSeeded()) return;

        List<Student> sample = new ArrayList<>();
        sample.add(new Student("Ali Khan", "Software Engineering"));
        sample.add(new Student("Sara Ahmed", "Computer Science"));
        sample.add(new Student("Bilal Hassan", "Information Technology"));
        sample.add(new Student("Ayesha Malik", "Software Engineering"));
        sample.add(new Student("Usman Tariq", "Computer Science"));
        sample.add(new Student("Hina Raza", "Software Engineering"));
        sample.add(new Student("Zain Abbas", "Computer Science"));
        sample.add(new Student("Mahnoor Iqbal", "Information Technology"));
        sample.add(new Student("Omar Farooq", "Software Engineering"));
        sample.add(new Student("Fatima Sheikh", "Computer Science"));

        dao.insertAll(sample);
        prefs.setSeeded(true);
    }

    private void log(String message) {
        executor.execute(() -> logDb.addLog(message));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            showStudentDialog(null);
            return true;

        } else if (id == R.id.action_sort) {
            prefs.setSortByName(!prefs.isSortByName());
            loadStudents();
            Toast.makeText(this,
                    prefs.isSortByName() ? "Sorted by name" : "Original order",
                    Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_activity_log) {
            showActivityLog();
            return true;

        } else if (id == R.id.action_backup) {
            backupToFile();
            return true;

        } else if (id == R.id.action_restore) {
            restoreFromBackup();
            return true;

        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemLongClick(int position) {
        selectedPositionForContextMenu = position;
        recyclerViewStudents.showContextMenu();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context_student, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (selectedPositionForContextMenu == -1) {
            return super.onContextItemSelected(item);
        }

        Student student = studentList.get(selectedPositionForContextMenu);
        int id = item.getItemId();

        if (id == R.id.action_view_details) {
            Toast.makeText(this,
                    student.getName() + " - " + student.getDepartment(),
                    Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_remove_context) {
            removeStudentWithUndo(selectedPositionForContextMenu);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onOverflowClick(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.menu_item_popup, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_edit) {
                showStudentDialog(studentList.get(position));
                return true;
            } else if (id == R.id.action_delete) {
                removeStudentWithUndo(position);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    @Override
    public void onItemClick(int position) {
        Student student = studentList.get(position);

        new AlertDialog.Builder(this)
                .setTitle(student.getName())
                .setMessage("Department: " + student.getDepartment())
                .setPositiveButton("Remove", (dialog, which) -> removeStudentWithUndo(position))
                .setNegativeButton("Close", null)
                .show();
    }

    private void showStudentDialog(@Nullable Student existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        EditText etName = dialogView.findViewById(R.id.etDialogName);
        EditText etDept = dialogView.findViewById(R.id.etDialogDept);
        boolean isEdit = existing != null;

        if (isEdit) {
            etName.setText(existing.getName());
            etDept.setText(existing.getDepartment());
        } else {
            etDept.setText(prefs.getLastDepartment());
        }

        new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Student" : "Add New Student")
                .setView(dialogView)
                .setPositiveButton(isEdit ? "Save" : "Add", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String dept = etDept.getText().toString().trim();

                    if (name.isEmpty() || dept.isEmpty()) {
                        Toast.makeText(this, "Please fill in both fields",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    prefs.setLastDepartment(dept);

                    if (isEdit) {
                        existing.setName(name);
                        existing.setDepartment(dept);
                        executor.execute(() -> dao.update(existing));
                        log("Edited student: " + name);
                        Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show();
                    } else {
                        executor.execute(() -> dao.insert(new Student(name, dept)));
                        log("Added student: " + name);
                    }

                    loadStudents();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("Student Directory App\n\n"
                        + "SharedPreferences: settings\n"
                        + "SQLite: activity log\n"
                        + "Room: students\n"
                        + "Files + ContentProvider: backup and restore")
                .setPositiveButton("OK", null)
                .setNeutralButton("Reset settings", (dialog, which) -> {
                    prefs.resetSettings();
                    loadStudents();
                    Toast.makeText(this, "Settings cleared", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showActivityLog() {
        executor.execute(() -> {
            List<String> logs = logDb.getAllLogs();
            String text = logs.isEmpty() ? "No activity yet." : TextUtils.join("\n\n", logs);

            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Activity Log (SQLite)")
                    .setMessage(text)
                    .setPositiveButton("Close", null)
                    .setNeutralButton("Clear log",
                            (dialog, which) -> executor.execute(() -> logDb.clearLogs()))
                    .show());
        });
    }

    private void removeStudentWithUndo(int position) {
        Student removed = studentList.get(position);

        executor.execute(() -> dao.delete(removed));
        log("Deleted student: " + removed.getName());
        loadStudents();

        Snackbar.make(recyclerViewStudents, removed.getName() + " removed",
                        Snackbar.LENGTH_LONG)
                .setAction("UNDO", v -> {
                    executor.execute(() -> dao.insert(removed));
                    log("Restored (undo): " + removed.getName());
                    loadStudents();
                })
                .show();
    }

    private void backupToFile() {
        executor.execute(() -> {
            List<Student> students = new ArrayList<>();

            try (Cursor c = getContentResolver().query(
                    StudentProvider.CONTENT_URI, null, null, null, null)) {
                if (c != null) {
                    int nameIdx = c.getColumnIndexOrThrow("name");
                    int deptIdx = c.getColumnIndexOrThrow("department");
                    while (c.moveToNext()) {
                        students.add(new Student(c.getString(nameIdx), c.getString(deptIdx)));
                    }
                }
            }

            try {
                StudentFileStore.saveBackup(this, students);
                File external = StudentFileStore.exportToExternal(this, students);

                log("Backup saved (" + students.size() + " students)");
                runOnUiThread(() -> Toast.makeText(this,
                        "Saved " + students.size() + " students\n" + external.getAbsolutePath(),
                        Toast.LENGTH_LONG).show());

            } catch (IOException | JSONException e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Backup failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void restoreFromBackup() {
        executor.execute(() -> {
            try {
                List<Student> students = StudentFileStore.readBackup(this);

                getContentResolver().delete(StudentProvider.CONTENT_URI, null, null);

                for (Student s : students) {
                    ContentValues values = new ContentValues();
                    values.put("name", s.getName());
                    values.put("department", s.getDepartment());
                    getContentResolver().insert(StudentProvider.CONTENT_URI, values);
                }

                log("Restored backup (" + students.size() + " students)");
                runOnUiThread(() -> Toast.makeText(this,
                        "Restored " + students.size() + " students",
                        Toast.LENGTH_SHORT).show());
                loadStudents();

            } catch (FileNotFoundException e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "No backup file yet. Use 'Backup to File' first.",
                        Toast.LENGTH_SHORT).show());
            } catch (IOException | JSONException e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Restore failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
