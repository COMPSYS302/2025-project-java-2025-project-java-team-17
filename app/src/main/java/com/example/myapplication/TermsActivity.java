package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TermsActivity extends BaseActivity {

    private ImageView ivBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        setupBottomNavigation(R.id.nav_profile);

        TextView title = findViewById(R.id.tv_cart_title);
        title.setText("Terms and Conditions");


        ivBtnBack = findViewById(R.id.btn_back);

        ivBtnBack.setOnClickListener(
                v -> {
                    finish();
                });
    }
}