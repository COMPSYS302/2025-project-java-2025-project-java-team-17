package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.databinding.ActivityLoginBinding;

import com.google.firebase.auth.FirebaseAuth;

/**
 * LoginActivity provides an interface for users to sign in to the application
 * using their email and password. It interacts with Firebase Authentication
 * to verify credentials.
 */
public class LoginActivity extends AppCompatActivity {

    // Firebase Authentication instance for handling user sign-in.
    private FirebaseAuth mAuth;

    //binding initialisation
    private ActivityLoginBinding binding;

    /**
     * Called when the activity is first created.
     * This method initializes the activity's UI components, Firebase Authentication,
     * and sets up listeners for user interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}. Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the user interface layout for this Activity.
        // The layout file is defined in res/layout/activity_login.xml
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize all the view components from the layout.
        initViews();
        // Set up click listeners for interactive elements like buttons.
        setupListeners();
    }

    /**
     * Initializes all the views used in this activity by finding them by their ID
     * from the layout. Also initializes the Firebase Authentication instance.
     */
    private void initViews() {
        // Get the shared instance of the FirebaseAuth object.
        mAuth = FirebaseAuth.getInstance();


             // The title TextView in the custom toolbar


        // Set the title of the activity in the custom toolbar.
        binding.includeTopBar.tvCartTitle.setText("Login");
    }

    /**
     * Sets up click listeners for interactive UI elements in this activity.
     * This includes the back button and the login button.
     */
    private void setupListeners() {
        // Set a click listener for the back button.
        // When clicked, it finishes the current activity, returning to the previous one.
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());

        // Set a click listener for the login button.
        binding.btnLogin.setOnClickListener(v -> {
            // Retrieve email and password from EditText fields, trimming whitespace.
            String email = binding.etLoginEmail.getText().toString().trim();
            String password = binding.etLoginPassword.getText().toString().trim();

            // Flag to track if input is valid.
            boolean valid = true;

            // Validate the email field.
            if (email.isEmpty()) {
                binding.etLoginEmail.setError("Email is required"); // Show error on the EditText.
                valid = false;
            }

            // Validate the password field.
            if (password.isEmpty()) {
                binding.etLoginPassword.setError("Password is required"); // Show error on the EditText.
                valid = false;
            }

            // If both email and password are provided (valid is true).
            if (valid) {
                // Attempt to log in the user with the provided credentials.
                loginUser(email, password);
            }
        });
    }

    /**
     * Attempts to sign in the user with the provided email and password using Firebase Authentication.
     * Handles the success and failure cases of the sign-in attempt.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     */
    private void loginUser(String email, String password) {
        // Use Firebase Authentication to sign in with email and password.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success: Navigate to the MainActivity.
                        // Create an Intent to start MainActivity.
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        // Finish LoginActivity so the user cannot navigate back to it using the back button.
                        finish();
                    } else {
                        // Sign in failed: Display an error message.
                        showLoginError();
                    }
                });
    }

    /**
     * Displays a login error message in the {@code tvLoginError} TextView for a short duration.
     * The error message is shown for 5 seconds and then automatically hidden.
     */
    private void showLoginError() {
        // Make the error TextView visible.
        binding.tvLoginError.setVisibility(View.VISIBLE);
        // Use a Handler to delay hiding the error message.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Ensure tvLoginError is still not null (e.g., activity not destroyed).
            if (binding.tvLoginError != null) {
                // Hide the error TextView after 5 seconds.
                binding.tvLoginError.setVisibility(View.GONE);
            }
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}