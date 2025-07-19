package com.example.schoolrunner.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.OrderDao;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.ui.activity.AboutActivity;
import com.example.schoolrunner.ui.activity.LoginActivity;
import com.example.schoolrunner.ui.activity.UserInfoActivity;
import com.example.schoolrunner.utils.CurrentStudentUtils;

import java.util.List;

public class ProfileFragment extends Fragment {

    // Top area
    private TextView tvTitle;

    // User info
    private TextView tvStudentName;
    private TextView tvStudentNo;
    
    // Statistics info
    private TextView tvMyPickupOrderCount;
    private TextView tvMyPublishOrderCount;
    
    // Settings list
    private LinearLayout llUserInfo;
    private LinearLayout llAbout;

    // Logout button
    private Button btnLogout;

    // Current student info
    private Student currentStudent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Each time the page resumes, refresh data
        initData();
    }

    private void initView(View view) {
        // Top area
        tvTitle = view.findViewById(R.id.tv_title);

        // User info
        tvStudentName = view.findViewById(R.id.tv_student_name);
        tvStudentNo = view.findViewById(R.id.tv_student_no);
        
        // Statistics info
        tvMyPickupOrderCount = view.findViewById(R.id.tv_my_pickup_order_count);
        tvMyPublishOrderCount = view.findViewById(R.id.tv_my_publish_order_count);
        
        // Settings list
        llUserInfo = view.findViewById(R.id.ll_user_info);
        llAbout = view.findViewById(R.id.ll_about);

        // Logout button
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void initData() {
        try {
            // Get the currently logged-in student info
            currentStudent = CurrentStudentUtils.getCurrentStudent();

            // Set student info
            tvStudentName.setText(currentStudent.getName());
            tvStudentNo.setText("Student ID: " + currentStudent.getStudentNo());

            // Get and set my received order count
            Result<List<Order>> pickupOrderResult = OrderDao.getOrdersByRunnerId(currentStudent.getId());
            if (pickupOrderResult.isSuccess() && pickupOrderResult.getData() != null) {
                tvMyPickupOrderCount.setText(String.valueOf(pickupOrderResult.getData().size()));
            } else {
                tvMyPickupOrderCount.setText("0");
            }

            // Get and set my published order count
            Result<List<Order>> publishOrderResult = OrderDao.getOrdersByStudentId(currentStudent.getId());
            if (publishOrderResult.isSuccess() && publishOrderResult.getData() != null) {
                tvMyPublishOrderCount.setText(String.valueOf(publishOrderResult.getData().size()));
            } else {
                tvMyPublishOrderCount.setText("0");
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to get user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initListener() {
        // Personal info click event
        llUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jump to personal info page
                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
            }
        });

        // About us click event
        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jump to about us page
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        // Logout click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

    /**
     * Show logout dialog
     */
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Prompt");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Logout
     */
    private void logout() {
        // Clear current login info
        CurrentStudentUtils.clearCurrentStudent();

        // Jump to login page
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear other activities in the task stack to ensure the user cannot return to the main page via the back button
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Close current activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
} 