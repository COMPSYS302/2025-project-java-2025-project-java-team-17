package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Activity to display the Privacy Policy.
 *
 * This activity extends {@link BaseActivity} to inherit common functionality
 * like the bottom navigation.
 */
public class PrivacyPolicyActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     * <p>
     * This method initializes the layout, sets up the bottom navigation,
     * sets the title of the screen, and configures the back button.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        // Initialize the bottom navigation, highlighting the "profile" tab.
        setupBottomNavigation(R.id.nav_profile);

        // Set the title of the screen.
        ((TextView) findViewById(R.id.tv_cart_title)).setText("Privacy Policy");

        // Set up the back button to finish the activity when clicked.
        ImageView ivBtnBack = findViewById(R.id.btn_back);
        ivBtnBack.setOnClickListener(v -> finish());
    }
}