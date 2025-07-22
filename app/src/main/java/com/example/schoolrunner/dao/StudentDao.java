package com.example.schoolrunner.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.utils.SqliteUtils;

/**
 * Student data access object
 */
@SuppressLint("Range")
public class StudentDao {

    /**
     * Student registration
     */
    public static Result<Void> register(Student student) {
        if (student == null || TextUtils.isEmpty(student.getStudentNo()) || TextUtils.isEmpty(student.getPassword())) {
            return new Result<>(false, "Student number or password cannot be null", null);
        }

        Result<Student> getByStudentNoResult = getByStudentNo(student.getStudentNo());
        if (getByStudentNoResult.isSuccess()) {
            return new Result<>(false, "Student number already exists", null);
        }

        ContentValues values = new ContentValues();
        values.put("student_no", student.getStudentNo());
        values.put("password", student.getPassword());
        values.put("name", student.getName());
        long i = SqliteUtils.getInstance().getWritableDatabase().insert("tb_student", null, values);
        if (i > 0) {
            return new Result<>(true, "Registration successful", null);
        }
        return new Result<>(false, "Registration failed", null);
    }

    /**
     * Student login
     */
    public static Result<Student> login(String studentNo, String password) {
        if (TextUtils.isEmpty(studentNo) || TextUtils.isEmpty(password)) {
            return new Result<>(false, "Student number or password cannot be null", null);
        }
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query("tb_student", null, "student_no=? and password=?", new String[]{studentNo, password}, null, null, null);
        if (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getLong(cursor.getColumnIndex("id")));
            student.setStudentNo(cursor.getString(cursor.getColumnIndex("student_no")));
            student.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            student.setName(cursor.getString(cursor.getColumnIndex("name")));
            cursor.close();
            return new Result<>(true, "Login successful", student);
        }
        cursor.close();
        return new Result<>(false, "Student number or password incorrect", null);
    }

    /**
     * Query student by student number
     */
    public static Result<Student> getByStudentNo(String studentNo) {
        if (TextUtils.isEmpty(studentNo)) {
            return new Result<>(false, "Student number cannot be null", null);
        }
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query("tb_student", null, "student_no=?", new String[]{studentNo}, null, null, null);
        if (cursor.moveToNext()) {
            Student student = new Student();
            student.setId(cursor.getLong(cursor.getColumnIndex("id")));
            student.setStudentNo(cursor.getString(cursor.getColumnIndex("student_no")));
            student.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            student.setName(cursor.getString(cursor.getColumnIndex("name")));
            cursor.close();
            return new Result<>(true, "Query successful", student);
        }
        cursor.close();
        return new Result<>(false, "Account does not exist", null);
    }

    /**
     * Query student by student ID
     */
    public static Result<Student> getByStudentId(Long studentId) {
        if (studentId == null) {
            return new Result<>(false, "Student ID cannot be null", null);
        }
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "tb_student", null, "id=?", new String[]{String.valueOf(studentId)}, null, null, null);
        if (cursor.moveToFirst()) {
            Student student = new Student();
            student.setId(cursor.getLong(cursor.getColumnIndex("id")));
            student.setStudentNo(cursor.getString(cursor.getColumnIndex("student_no")));
            student.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            student.setName(cursor.getString(cursor.getColumnIndex("name")));
            // 关键：赋值平均分
            Double pubAvg = getAveragePublisherScore(studentId);
            Double runAvg = getAverageRunnerScore(studentId);
            android.util.Log.d("StudentDao", "getByStudentId pubAvg=" + pubAvg + ", runAvg=" + runAvg);
            student.setAveragePublisherScore(pubAvg);
            student.setAverageRunnerScore(runAvg);
            cursor.close();
            return new Result<>(true, "Query successful", student);
        }
        cursor.close();
        return new Result<>(false, "Student does not exist", null);
    }

    /**
     * Update student info
     *
     * @param student Student object
     * @return Update result
     */
    public static Result<Student> updateStudentInfo(Student student) {
        if (student == null || student.getId() == null) {
            return new Result<>(false, "Student information is incomplete", null);
        }

        // Check if student number is already in use (excluding self)
        if (!TextUtils.isEmpty(student.getStudentNo())) {
            Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                    "tb_student",
                    null,
                    "student_no=? AND id!=?",
                    new String[]{student.getStudentNo(), String.valueOf(student.getId())},
                    null, null, null);
            boolean exists = cursor.moveToNext();
            cursor.close();

            if (exists) {
                return new Result<>(false, "This student number is already used by another user", null);
            }
        } else {
            return new Result<>(false, "Student number cannot be null", null);
        }

        // Check name
        if (TextUtils.isEmpty(student.getName())) {
            return new Result<>(false, "Name cannot be null", null);
        }

        ContentValues values = new ContentValues();
        values.put("student_no", student.getStudentNo());
        values.put("name", student.getName());

        int rowsAffected = SqliteUtils.getInstance().getWritableDatabase().update(
                "tb_student",
                values,
                "id=?",
                new String[]{String.valueOf(student.getId())});

        if (rowsAffected > 0) {
            return new Result<>(true, "Update successful", student);
        }

        return new Result<>(false, "Update failed", null);
    }

    public static Double getAveragePublisherScore(Long studentId) {
        if (studentId == null) return null;
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().rawQuery(
            "SELECT AVG(runner_score) FROM tb_order WHERE student_id = ? AND runner_score IS NOT NULL",
            new String[]{String.valueOf(studentId)});
        Double avg = null;
        if (cursor.moveToFirst()) {
            avg = cursor.isNull(0) ? null : cursor.getDouble(0);
        }
        android.util.Log.d("StudentDao", "getAveragePublisherScore (被评分) studentId=" + studentId + ", avg=" + avg);
        cursor.close();
        return avg;
    }

    public static Double getAverageRunnerScore(Long studentId) {
        if (studentId == null) return null;
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().rawQuery(
            "SELECT AVG(publisher_score) FROM tb_order WHERE runner_id = ? AND publisher_score IS NOT NULL",
            new String[]{String.valueOf(studentId)});
        Double avg = null;
        if (cursor.moveToFirst()) {
            avg = cursor.isNull(0) ? null : cursor.getDouble(0);
        }
        android.util.Log.d("StudentDao", "getAverageRunnerScore (被评分) studentId=" + studentId + ", avg=" + avg);
        cursor.close();
        return avg;
    }
}
