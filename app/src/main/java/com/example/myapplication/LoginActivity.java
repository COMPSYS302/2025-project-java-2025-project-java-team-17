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

import com.google.firebase.auth.FirebaseAuth;

/**
 * LoginActivity provides an interface for users to sign in to the application
 * using their email and password. It interacts with Firebase Authentication
 * to verify credentials.
 */
public class LoginActivity extends AppCompatActivity {

    // Firebase Authentication instance for handling user sign-in.
    private FirebaseAuth mAuth;
    // EditText field for the user to enter their email address.
    private EditText etLoginEmail;
    // EditText field for the user to enter their password.
    private EditText etLoginPassword;
    // Button to trigger the login process.
    private Button btnLogin;
    // TextView to display login error messages to the user.
    private TextView tvLoginError;
    // TextView for the title of the activity (e.g., "Login").
    private TextView title;
    // ImageView that acts as a back button to navigate to the previous screen.
    private ImageView ivBtnBack;

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
        setContentView(R.layout.activity_login);

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

        // Find and assign UI elements from the layout to their respective variables.
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginError = findViewById(R.id.tvLoginError); // For displaying login errors
        title = findViewById(R.id.tv_cart_title);     // The title TextView in the custom toolbar
        ivBtnBack = findViewById(R.id.btn_back);        // The back button ImageView in the custom toolbar

        // Set the title of the activity in the custom toolbar.
        title.setText("Login");
    }

    /**
     * Sets up click listeners for interactive UI elements in this activity.
     * This includes the back button and the login button.
     */
    private void setupListeners() {
        // Set a click listener for the back button.
        // When clicked, it finishes the current activity, returning to the previous one.
        ivBtnBack.setOnClickListener(v -> finish());

        // Set a click listener for the login button.
        btnLogin.setOnClickListener(v -> {
            // Retrieve email and password from EditText fields, trimming whitespace.
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            // Flag to track if input is valid.
            boolean valid = true;

            // Validate the email field.
            if (email.isEmpty()) {
                etLoginEmail.setError("Email is required"); // Show error on the EditText.
                valid = false;
            }

            // Validate the password field.
            if (password.isEmpty()) {
                etLoginPassword.setError("Password is required"); // Show error on the EditText.
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
        tvLoginError.setVisibility(View.VISIBLE);
        // Use a Handler to delay hiding the error message.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Ensure tvLoginError is still not null (e.g., activity not destroyed).
            if (tvLoginError != null) {
                // Hide the error TextView after 5 seconds.
                tvLoginError.setVisibility(View.GONE);
            }
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}