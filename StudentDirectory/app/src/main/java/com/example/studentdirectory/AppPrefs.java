package com.example.studentdirectory;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {

    private static final String FILE_NAME = "student_directory_prefs";

    private static final String KEY_LAUNCH_COUNT = "launch_count";
    private static final String KEY_LAST_DEPT    = "last_department";
    private static final String KEY_SORT_BY_NAME = "sort_by_name";
    private static final String KEY_DB_SEEDED    = "db_seeded";

    private final SharedPreferences sp;

    public AppPrefs(Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public int incrementLaunchCount() {
        int count = sp.getInt(KEY_LAUNCH_COUNT, 0) + 1;
        sp.edit().putInt(KEY_LAUNCH_COUNT, count).apply();   // apply() = async save
        return count;
    }

    public String getLastDepartment() { return sp.getString(KEY_LAST_DEPT, ""); }
    public void setLastDepartment(String dept) { sp.edit().putString(KEY_LAST_DEPT, dept).apply(); }

    public boolean isSortByName() { return sp.getBoolean(KEY_SORT_BY_NAME, false); }
    public void setSortByName(boolean v) { sp.edit().putBoolean(KEY_SORT_BY_NAME, v).apply(); }

    public boolean isSeeded() { return sp.getBoolean(KEY_DB_SEEDED, false); }
    public void setSeeded(boolean v) { sp.edit().putBoolean(KEY_DB_SEEDED, v).apply(); }

    public void resetSettings() {
        sp.edit()
                .remove(KEY_LAUNCH_COUNT)
                .remove(KEY_LAST_DEPT)
                .remove(KEY_SORT_BY_NAME)
                .apply();
    }
}
