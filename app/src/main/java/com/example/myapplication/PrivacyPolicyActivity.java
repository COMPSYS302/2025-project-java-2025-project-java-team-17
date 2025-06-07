package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PrivacyPolicyActivity extends BaseActivity {

    private ImageView ivBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        setupBottomNavigation(R.id.nav_profile);
        ivBtnBack = findViewById(R.id.btn_back);

        TextView title = findViewById(R.id.tv_cart_title);
        title.setText("Privacy Policy");

        ivBtnBack.setOnClickListener(
                v -> {
                    finish();
                });
    }

}

