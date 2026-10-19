package com.example.studentdirectory;

import android.database.Cursor;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Student student);

    @Insert
    List<Long> insertAll(List<Student> students);

    @Update
    int update(Student student);

    @Delete
    int delete(Student student);

    @Query("SELECT * FROM students ORDER BY id ASC")
    List<Student> getAll();

    @Query("SELECT * FROM students ORDER BY name COLLATE NOCASE ASC")
    List<Student> getAllSortedByName();

    @Query("SELECT * FROM students ORDER BY id ASC")
    Cursor getAllCursor();

    @Query("SELECT * FROM students WHERE id = :id")
    Cursor getByIdCursor(int id);

    @Query("DELETE FROM students WHERE id = :id")
    int deleteById(int id);

    @Query("DELETE FROM students")
    int deleteAll();

    @Query("UPDATE students SET name = :name, department = :dept WHERE id = :id")
    int updateById(int id, String name, String dept);
}
