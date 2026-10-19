package com.example.studentdirectory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StudentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.studentdirectory.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/students");

    private static final int STUDENTS   = 1;
    private static final int STUDENT_ID = 2;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, "students", STUDENTS);
        MATCHER.addURI(AUTHORITY, "students/#", STUDENT_ID);
    }

    private StudentDao dao;

    @Override
    public boolean onCreate() {
        dao = AppDatabase.getInstance(getContext()).studentDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        Cursor cursor;
        switch (MATCHER.match(uri)) {
            case STUDENTS:
                cursor = dao.getAllCursor();
                break;
            case STUDENT_ID:
                cursor = dao.getByIdCursor((int) ContentUris.parseId(uri));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case STUDENTS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".student";
            case STUDENT_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".student";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (MATCHER.match(uri) != STUDENTS || values == null) {
            throw new IllegalArgumentException("Bad insert URI: " + uri);
        }
        long id = dao.insert(new Student(
                values.getAsString("name"), values.getAsString("department")));
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int rows;
        switch (MATCHER.match(uri)) {
            case STUDENTS:
                rows = dao.deleteAll();
                break;
            case STUDENT_ID:
                rows = dao.deleteById((int) ContentUris.parseId(uri));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        if (MATCHER.match(uri) != STUDENT_ID || values == null) {
            throw new IllegalArgumentException("Bad update URI: " + uri);
        }
        int rows = dao.updateById((int) ContentUris.parseId(uri),
                values.getAsString("name"), values.getAsString("department"));
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return rows;
    }
}
