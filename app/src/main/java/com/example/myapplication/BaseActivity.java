package com.example.myapplication;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * BaseActivity provides common functionality that can be shared across multiple activities,
 * such as setting up a bottom navigation view. Activities that require this common
 * functionality can extend BaseActivity instead of AppCompatActivity directly.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * Sets up the BottomNavigationView for the activity.
     * It initializes the bottom navigation view, highlights the selected item,
     * and sets up an item selected listener to handle navigation between different activities.
     *
     * This method assumes that the layout of the activity extending BaseActivity
     * contains a BottomNavigationView with the ID {@code R.id.bottomNavigationView}.
     *
     * @param selectedItemId The resource ID of the menu item that should be
     *                       marked as selected in the BottomNavigationView. This is typically
     *                       the menu item corresponding to the current activity.
     */
    protected void setupBottomNavigation(int selectedItemId) {
        // Find the BottomNavigationView in the current activity's layout.
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Programmatically set the current selected item. This is useful to ensure
        // the correct tab is highlighted when the activity starts.
        bottomNav.setSelectedItemId(selectedItemId);

        // Set a listener to handle clicks on the bottom navigation items.
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the ID of the clicked item.

            // Handle navigation to MainActivity (Home).
            // It checks if the current activity is NOT already MainActivity to prevent
            // restarting the same activity.
            if (itemId == R.id.nav_home && !(this instanceof MainActivity)) {
                startActivity(new Intent(this, MainActivity.class));
                return true; // Event consumed.
            }
            // Handle navigation to CartActivity.
            else if (itemId == R.id.nav_cart && !(this instanceof CartActivity)) {
                startActivity(new Intent(this, CartActivity.class));
                return true; // Event consumed.
            }
            // Handle navigation to ProfileActivity.
            else if (itemId == R.id.nav_profile && !(this instanceof ProfileActivity)) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true; // Event consumed.
            }

            // If the clicked item corresponds to the current activity or is an unknown item,
            // do nothing and indicate that the event was not consumed (or handled by default).
            return false;
        });
    }
}