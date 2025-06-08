package com.example.myapplication;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class BaseActivity extends AppCompatActivity {
    protected void setupBottomNavigation(int selectedItemId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(selectedItemId); // Highlight current tab

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home && !(this instanceof MainActivity)) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart && !(this instanceof CartActivity)) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile && !(this instanceof ProfileActivity)) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return false; // Already on the correct screen or unknown item
        });
    }

}