package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PrivacyPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        setupBottomNavigation(R.id.nav_profile);

        ((TextView) findViewById(R.id.tv_cart_title)).setText("Privacy Policy");

        ImageView ivBtnBack = findViewById(R.id.btn_back);
        ivBtnBack.setOnClickListener(v -> finish());
    }
}
