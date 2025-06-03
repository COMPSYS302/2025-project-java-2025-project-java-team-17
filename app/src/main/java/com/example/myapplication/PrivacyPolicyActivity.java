package com.example.myapplication;

import android.os.Bundle;

public class PrivacyPolicyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        setupBottomNavigation(R.id.nav_profile);
    }
}