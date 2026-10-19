package com.example.studentdirectory;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StudentFileStore {

    public static final String BACKUP_FILE = "students_backup.json";
    public static final String EXPORT_FILE = "students_export.json";

    private static String toJson(List<Student> students) throws JSONException {
        JSONArray array = new JSONArray();
        for (Student s : students) {
            JSONObject obj = new JSONObject();
            obj.put("name", s.getName());
            obj.put("department", s.getDepartment());
            array.put(obj);
        }
        return array.toString(2);
    }

    public static void saveBackup(Context context, List<Student> students)
            throws IOException, JSONException {
        try (FileOutputStream fos = context.openFileOutput(BACKUP_FILE, Context.MODE_PRIVATE)) {
            fos.write(toJson(students).getBytes(StandardCharsets.UTF_8));
        }
    }

    public static File exportToExternal(Context context, List<Student> students)
            throws IOException, JSONException {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            throw new IOException("External storage not available");
        }
        File file = new File(dir, EXPORT_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(toJson(students).getBytes(StandardCharsets.UTF_8));
        }
        return file;
    }

    public static List<Student> readBackup(Context context) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = context.openFileInput(BACKUP_FILE);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        JSONArray array = new JSONArray(sb.toString());
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            list.add(new Student(obj.getString("name"), obj.getString("department")));
        }
        return list;
    }
}
