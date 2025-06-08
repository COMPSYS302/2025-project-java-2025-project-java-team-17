package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity is the first screen displayed when the application is launched.
 * It shows a splash screen for a defined duration and then navigates to the
 * {@link AuthActivity}.
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * The delay in milliseconds before transitioning from the splash screen
     * to the main content of the application.
     */
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    /**
     * Called when the activity is first created.
     * <p>
     * This method sets the content view for the splash screen and then
     * uses a {@link Handler} to delay the transition to the {@link AuthActivity}
     * by the duration specified in {@link #SPLASH_DELAY}. After the delay,
     * it starts the {@link AuthActivity} and finishes the current {@link SplashActivity}
     * so that it's not shown again when the user presses the back button.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the splash screen.
        setContentView(R.layout.activity_splash);

        // Use a Handler to delay the execution of the navigation logic.
        new Handler().postDelayed(() -> {
            // Create an Intent to start the AuthActivity.
            Intent intent = new Intent(SplashActivity.this, AuthActivity.class);
            // Start the AuthActivity.
            startActivity(intent);
            // Finish the SplashActivity so it's removed from the back stack.
            finish();
        }, SPLASH_DELAY); // Post the runnable with the defined delay.
    }
}