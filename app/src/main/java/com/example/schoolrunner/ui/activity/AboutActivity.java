package com.example.schoolrunner.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolrunner.R;

/**
 * About Us Page
 */
public class AboutActivity extends AppCompatActivity {

    private ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
        initListener();
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
    }

    private void initListener() {
        // Back button click event
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
} 