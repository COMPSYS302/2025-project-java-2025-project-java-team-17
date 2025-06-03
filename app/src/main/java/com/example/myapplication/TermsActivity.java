package com.example.myapplication;

import android.os.Bundle;

public class TermsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        setupBottomNavigation(R.id.nav_profile);
    }
}