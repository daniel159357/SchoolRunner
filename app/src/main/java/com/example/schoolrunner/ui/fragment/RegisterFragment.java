package com.example.schoolrunner.ui.fragment;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.StudentDao;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.ui.activity.LoginActivity;

public class RegisterFragment extends Fragment {

    private EditText etStudentNo;
    private EditText etName;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private ImageView ivPasswordToggle;
    private ImageView ivConfirmPasswordToggle;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
    }

    private void initView(View view) {
        etStudentNo = view.findViewById(R.id.et_student_no);
        etName = view.findViewById(R.id.et_name);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        ivPasswordToggle = view.findViewById(R.id.iv_password_toggle);
        ivConfirmPasswordToggle = view.findViewById(R.id.iv_confirm_password_toggle);
    }

    private void initListener() {
        // Password visibility toggle
        ivPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    // Show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivPasswordToggle.setImageResource(R.drawable.ic_eye_open);
                } else {
                    // Hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivPasswordToggle.setImageResource(R.drawable.ic_eye_close);
                }
                // Move cursor to the end of the text
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        // Confirm password visibility toggle
        ivConfirmPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    // Show password
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_open);
                } else {
                    // Hide password
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ivConfirmPasswordToggle.setImageResource(R.drawable.ic_eye_close);
                }
                // Move cursor to the end of the text
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            }
        });

        // Register button click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentNo = etStudentNo.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                // Simple input validation
                if (studentNo.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter student number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Please confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "The two passwords entered do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Implement registration logic
                Student student = new Student();
                student.setStudentNo(studentNo);
                student.setName(name);
                student.setPassword(password);

                Result<Void> registerResult = StudentDao.register(student);
                if (registerResult.isSuccess()) {
                    Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();

                    // Clear input boxes
                    etStudentNo.setText("");
                    etName.setText("");
                    etPassword.setText("");
                    etConfirmPassword.setText("");

                    // Return to login page and set student number and password
                    backToLoginWithAccount(studentNo, password);
                } else {
                    Toast.makeText(getActivity(), registerResult.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Return to login page and set student number and password
     */
    private void backToLoginWithAccount(String studentNo, String password) {
        if (getActivity() == null) return;

        // Call Activity method to return to login page and set account password
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).backToLoginWithAccount(studentNo, password);
        }
    }
} 