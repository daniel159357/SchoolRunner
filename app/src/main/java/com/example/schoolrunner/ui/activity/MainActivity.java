package com.example.schoolrunner.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.schoolrunner.R;
import com.example.schoolrunner.ui.fragment.HomeFragment;
import com.example.schoolrunner.ui.fragment.OrderFragment;
import com.example.schoolrunner.ui.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private OrderFragment orderFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFragment();
        initListener();
    }

    private void initView() {
        navView = findViewById(R.id.nav_view);
        
        // Fix bottom navigation bar icon position issues
        navView.setItemIconTintList(null); // Allow icons to use their original colors
        navView.setItemHorizontalTranslationEnabled(false); // Disable horizontal translation to center icons
        
        // Apply additional spacing to improve icon vertical position
        ViewGroup bottomNavigationMenuView = (ViewGroup) navView.getChildAt(0);
        if (bottomNavigationMenuView != null) {
            bottomNavigationMenuView.setPadding(0, 4, 0, 0);
        }
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        orderFragment = new OrderFragment();
        profileFragment = new ProfileFragment();

        // Default select home page
        switchFragment(homeFragment);
        navView.setSelectedItemId(R.id.navigation_home);
    }

    private void initListener() {
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    switchFragment(homeFragment);
                    return true;
                } else if (itemId == R.id.navigation_order) {
                    switchFragment(orderFragment);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    switchFragment(profileFragment);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Switch Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Hide all Fragments first
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        // If the target Fragment is not added, add it to the container
        if (!targetFragment.isAdded()) {
            transaction.add(R.id.fl_container, targetFragment);
        } else {
            transaction.show(targetFragment);
        }

        transaction.commit();
        currentFragment = targetFragment;
    }
}