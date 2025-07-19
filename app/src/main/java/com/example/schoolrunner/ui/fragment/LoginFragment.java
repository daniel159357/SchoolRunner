package com.example.schoolrunner.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.schoolrunner.ui.activity.MainActivity;
import com.example.schoolrunner.utils.CurrentStudentUtils;

public class LoginFragment extends Fragment {

    private EditText etStudentNo;
    private EditText etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private ImageView ivPasswordToggle;
    private boolean isPasswordVisible = false;

    private static final String REMEMBER_PREF = "remember_pref";
    private static final String KEY_STUDENT_NO = "student_no";
    private static final String KEY_REMEMBER = "remember";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        loadRememberedAccount();
    }

    private void initView(View view) {
        etStudentNo = view.findViewById(R.id.et_student_no);
        etPassword = view.findViewById(R.id.et_password);
        cbRemember = view.findViewById(R.id.cb_remember);
        btnLogin = view.findViewById(R.id.btn_login);
        ivPasswordToggle = view.findViewById(R.id.iv_password_toggle);
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

        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentNo = etStudentNo.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Simple input validation
                if (studentNo.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter student number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Implement login logic
                Result<Student> loginResult = StudentDao.login(studentNo, password);
                if (loginResult.isSuccess()) {
                    // Save current logged-in student information
                    CurrentStudentUtils.setCurrentStudentId(loginResult.getData().getId());

                    // Save remembered account
                    saveRememberedAccount(studentNo);

                    Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), loginResult.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Save remembered account
     */
    private void saveRememberedAccount(String studentNo) {
        if (getActivity() == null) return;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(REMEMBER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (cbRemember.isChecked()) {
            editor.putString(KEY_STUDENT_NO, studentNo);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.remove(KEY_STUDENT_NO);
            editor.putBoolean(KEY_REMEMBER, false);
        }

        editor.apply();
    }

    /**
     * Load remembered account
     */
    private void loadRememberedAccount() {
        if (getActivity() == null) return;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(REMEMBER_PREF, Context.MODE_PRIVATE);
        boolean isRemember = sharedPreferences.getBoolean(KEY_REMEMBER, false);

        if (isRemember) {
            String studentNo = sharedPreferences.getString(KEY_STUDENT_NO, "");
            etStudentNo.setText(studentNo);
            cbRemember.setChecked(true);
        }
    }

    /**
     * After successful registration, set account and password
     */
    public void setAccountInfo(String studentNo, String password) {
        if (etStudentNo != null && etPassword != null) {
            etStudentNo.setText(studentNo);
            etPassword.setText(password);
        }
    }
} 