package com.example.schoolrunner.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.StudentDao;
import com.example.schoolrunner.model.entity.Student;
import com.gzone.university.utils.AppUtils;

public class CurrentStudentUtils {

    private static final String PREF_NAME = "student_pref";
    private static final String KEY_STUDENT_ID = "current_student_id";

    /**
     * Get current logged-in student information
     *
     * @return Current student object, if not logged in then throw exception
     */
    public static Student getCurrentStudent() {
        SharedPreferences sharedPreferences = AppUtils.getApplication()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long studentId = sharedPreferences.getLong(KEY_STUDENT_ID, -1);

        if (studentId == -1) {
            throw new IllegalStateException("Please log in first");
        }

        Result<Student> result = StudentDao.getByStudentId(studentId);
        if (result.isSuccess()) {
            return result.getData();
        }

        throw new IllegalStateException("Failed to get student information");
    }

    /**
     * Set current logged-in student ID
     *
     * @param studentId Student ID
     */
    public static void setCurrentStudentId(long studentId) {
        SharedPreferences sharedPreferences = AppUtils.getApplication()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_STUDENT_ID, studentId);
        editor.apply();
    }

    /**
     * Clear current logged-in student information (use when logging out)
     */
    public static void clearCurrentStudent() {
        SharedPreferences sharedPreferences = AppUtils.getApplication()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_STUDENT_ID);
        editor.apply();
    }
}