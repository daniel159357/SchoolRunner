package com.example.schoolrunner.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.StudentDao;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.utils.CurrentStudentUtils;

/**
 * Personal Information Edit Page
 */
public class UserInfoActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvSave;
    private EditText etName;
    private EditText etStudentNo;

    private Student currentStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        tvSave = findViewById(R.id.tv_save);
        etName = findViewById(R.id.et_name);
        etStudentNo = findViewById(R.id.et_student_no);
    }

    private void initData() {
        try {
            // Get the currently logged-in student information
            currentStudent = CurrentStudentUtils.getCurrentStudent();

            // Set initial values for the edit boxes
            if (currentStudent != null) {
                etName.setText(currentStudent.getName());
                etStudentNo.setText(currentStudent.getStudentNo());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to get user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initListener() {
        // Back button click event
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Save button click event
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });
    }

    /**
     * Save user information
     */
    private void saveUserInfo() {
        String name = etName.getText().toString().trim();
        String studentNo = etStudentNo.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(studentNo)) {
            Toast.makeText(this, "Student number cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update student information
        if (currentStudent != null) {
            currentStudent.setName(name);
            currentStudent.setStudentNo(studentNo);

            Result<Student> result = StudentDao.updateStudentInfo(currentStudent);
            if (result.isSuccess()) {
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();

                // Update current student ID
                CurrentStudentUtils.setCurrentStudentId(result.getData().getId());

                // Return to previous page
                finish();
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
} 