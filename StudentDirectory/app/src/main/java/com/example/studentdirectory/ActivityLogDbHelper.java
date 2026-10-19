package com.example.studentdirectory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityLogDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "activity_log.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE       = "activity_log";
    private static final String COL_ID      = "id";
    private static final String COL_MESSAGE = "message";
    private static final String COL_TIME    = "created_at";

    public ActivityLogDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_MESSAGE + " TEXT NOT NULL, "
                + COL_TIME + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long addLog(String message) {
        ContentValues cv = new ContentValues();
        cv.put(COL_MESSAGE, message);
        cv.put(COL_TIME, System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE, null, cv);
    }

    public List<String> getAllLogs() {
        List<String> logs = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM, HH:mm:ss", Locale.getDefault());

        try (Cursor c = getReadableDatabase().query(TABLE, null, null, null,
                null, null, COL_ID + " DESC", "50")) {
            int msgIdx = c.getColumnIndexOrThrow(COL_MESSAGE);
            int timeIdx = c.getColumnIndexOrThrow(COL_TIME);
            while (c.moveToNext()) {
                logs.add(fmt.format(new Date(c.getLong(timeIdx))) + "  -  " + c.getString(msgIdx));
            }
        }
        return logs;
    }

    public int clearLogs() {
        return getWritableDatabase().delete(TABLE, null, null);
    }
}
