package com.example.schoolrunner.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.schoolrunner.R;
import com.example.schoolrunner.ui.fragment.LoginFragment;
import com.example.schoolrunner.ui.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    private TextView tvLogin;
    private TextView tvRegister;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    private Fragment currentFragment;
    
    private static final String LOGIN_FRAGMENT_TAG = "login_fragment";
    private static final String REGISTER_FRAGMENT_TAG = "register_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();

        if (savedInstanceState == null) {
            // Initialize Fragment when Activity is created for the first time
            initFragment();
        }
    }

    private void initView() {
        tvLogin = findViewById(R.id.tv_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void initListener() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to login Fragment
                switchFragment(loginFragment);
                // Update UI state
                tvLogin.setSelected(true);
                tvRegister.setSelected(false);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to register Fragment
                switchFragment(registerFragment);
                // Update UI state
                tvLogin.setSelected(false);
                tvRegister.setSelected(true);
            }
        });
    }

    private void initFragment() {
        // Initialize Fragments
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        // Add all Fragments, but only show login Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_container, loginFragment, LOGIN_FRAGMENT_TAG);
        transaction.add(R.id.fl_container, registerFragment, REGISTER_FRAGMENT_TAG);
        transaction.hide(registerFragment);
        transaction.commit();

        // Set the currently displayed Fragment
        currentFragment = loginFragment;
        
        // Set login tab as selected
        tvLogin.setSelected(true);
    }

    /**
     * Switch Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        if (currentFragment != targetFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Hide current Fragment, show target Fragment
            transaction.hide(currentFragment);
            transaction.show(targetFragment);
            transaction.commit();

            // Update current Fragment
            currentFragment = targetFragment;
        }
    }

    /**
     * After successful registration, return to login page and set account and password
     */
    public void backToLoginWithAccount(String studentNo, String password) {
        // Switch to login Fragment
        switchFragment(loginFragment);
        // Update UI state
        tvLogin.setSelected(true);
        tvRegister.setSelected(false);

        // Set account info
        loginFragment.setAccountInfo(studentNo, password);
    }
}
