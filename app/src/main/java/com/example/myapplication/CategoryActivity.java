package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity  {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);  // make sure this matches your XML filename

        // Connect the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the toolbar title (this overrides android:title in XML)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Category"); // You can make this dynamic later
        }

        // Optional: handle back button
        toolbar.setNavigationOnClickListener(v -> finish());
    }








}
