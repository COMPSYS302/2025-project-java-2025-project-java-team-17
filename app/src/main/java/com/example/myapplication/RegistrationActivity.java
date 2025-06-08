package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for user registration.
 * This activity allows new users to create an account using their email, username, and password.
 * It validates the input fields and interacts with Firebase Authentication and Firestore
 * to register the user and store their information.
 */
public class RegistrationActivity extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private FirebaseFirestore db;
  private Toast currentToast; // To manage and dismiss ongoing toasts
  private Handler toastHandler = new Handler(); // Handler to dismiss toast after a delay

  // UI Elements
  private ImageView ivBtnBack;
  private Button btnRegister;
  private EditText etUsername, etEmail, etPassword, etConfirmPassword;

  /**
   * Called when the activity is first created.
   * <p>
   * This method initializes the activity, sets up the layout, Firebase instances,
   * UI elements, and click listeners for registration and navigation.
   *
   * @param savedInstanceState If the activity is being re-initialized after
   *                           previously being shut down then this Bundle contains the data it most
   *                           recently supplied in {@link #onSaveInstanceState}.
   *                           <b><i>Note: Otherwise it is null.</i></b>
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e("RegistrationActivity", "=== ACTIVITY STARTED ==="); // Log activity start
    setContentView(R.layout.activity_register);

    // Initialize Firebase Auth and Firestore
    mAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    // Initialize UI elements
    ivBtnBack = findViewById(R.id.btn_back);
    btnRegister = findViewById(R.id.btnRegister);
    etUsername = findViewById(R.id.etUserName);
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    etConfirmPassword = findViewById(R.id.etConfirmPassword);

    // Set the screen title
    TextView title = findViewById(R.id.tv_cart_title);
    title.setText("Register");

    // Set click listener for the back button to finish the activity
    ivBtnBack.setOnClickListener(v -> finish());

    // Set click listener for the registration button
    btnRegister.setOnClickListener(v -> {
      String username = etUsername.getText().toString().trim();
      String email = etEmail.getText().toString().trim();
      String password = etPassword.getText().toString();
      String confirmPassword = etConfirmPassword.getText().toString();

      // Validate inputs before attempting registration
      if (validateInputs(username, email, password, confirmPassword)) {
        btnRegister.setEnabled(false); // Disable button to prevent multiple clicks
        registerUser(username, email, password);
      }
    });
  }

  /**
   * Validates the user input fields for registration.
   * Checks for empty username, valid email format, password length, and matching passwords.
   * Shows a toast message for the first validation error encountered.
   *
   * @param username        The username entered by the user.
   * @param email           The email entered by the user.
   * @param password        The password entered by the user.
   * @param confirmPassword The confirmed password entered by the user.
   * @return {@code true} if all inputs are valid, {@code false} otherwise.
   */
  private boolean validateInputs(String username, String email, String password, String confirmPassword) {
    if (username.isEmpty()) {
      showToast("Username Cannot Be Empty");
      return false;
    }

    if (!email.contains("@")) { // Basic email validation
      showToast("Please Enter A Valid Email");
      return false;
    }

    if (password.length() < 6) {
      showToast("Password must be at least 6 characters");
      return false;
    }

    if (!password.equals(confirmPassword)) {
      showToast("Passwords do not match");
      return false;
    }

    return true; // All validations passed
  }

  /**
   * Registers a new user with Firebase Authentication using their email and password.
   * On successful registration, it saves the user's details to Firestore.
   * On failure, it calls {@link #handleRegistrationFailure(Exception)}.
   *
   * @param username The username for the new user.
   * @param email    The email for the new user.
   * @param password The password for the new user.
   */
  private void registerUser(String username, String email, String password) {
    Log.d("RegistrationActivity", "=== REGISTER USER FUNCTION ENTERED ===");

    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                  String userId = firebaseUser.getUid();
                  Log.d("RegistrationActivity", "Registration successful. Saving to Firestore.");
                  saveUserToFirestore(userId, username, email);
                } else {
                  // This case should ideally not happen if task is successful
                  Log.e("RegistrationActivity", "Registration successful but FirebaseUser is null.");
                  showToast("Registration failed: User data not available.");
                  reEnableRegisterButton();
                }
              } else {
                // Handle registration failure (e.g., email already exists, weak password)
                handleRegistrationFailure(task.getException());
              }
            });
  }

  /**
   * Handles failures during the Firebase user registration process.
   * Re-enables the register button and shows an appropriate toast message to the user
   * based on the type of exception.
   *
   * @param exception The exception that occurred during registration.
   */
  private void handleRegistrationFailure(Exception exception) {
    reEnableRegisterButton(); // Allow user to try again

    Log.w("RegistrationActivity", "Registration failed", exception); // Log the exception

    // Show specific error messages based on the exception type
    if (exception instanceof FirebaseAuthUserCollisionException) {
      showToast("Email already registered. Please use a different email.");
    } else if (exception instanceof FirebaseAuthWeakPasswordException) {
      showToast("Password is too weak.");
    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
      // This can be due to malformed email or other credential issues
      showToast("Invalid email format or credentials.");
    } else {
      showToast("Registration failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
    }
  }

  /**
   * Displays a toast message to the user.
   * If a toast is already showing, it cancels the current one and shows the new message.
   * The toast is automatically dismissed after a short duration (3.5 seconds).
   *
   * @param message The message to be displayed in the toast.
   */
  private void showToast(String message) {
    // Cancel any existing toast to prevent them from stacking up
    if (currentToast != null) {
      currentToast.cancel();
    }
    currentToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
    currentToast.show();

    // Nullify the currentToast reference after the toast's duration to allow new toasts
    // or to ensure it doesn't hold a reference unnecessarily.
    toastHandler.removeCallbacksAndMessages(null); // Remove any pending dismissals
    toastHandler.postDelayed(() -> currentToast = null, 3500); // Standard LONG_DELAY is 3.5s
  }

  /**
   * Re-enables the registration button.
   * This is typically called after a registration attempt (successful or failed)
   * or if validation fails, allowing the user to try again.
   */
  private void reEnableRegisterButton() {
    if (btnRegister != null) {
      btnRegister.setEnabled(true);
    }
  }

  /**
   * Saves the new user's information (username, email, creation timestamp, empty favourites list)
   * to the "users" collection in Firestore, using the Firebase Auth UID as the document ID.
   * After successfully saving, it navigates the user to the {@link MainActivity}.
   *
   * @param userId   The unique ID of the registered user (from Firebase Auth).
   * @param username The username of the user.
   * @param email    The email of the user.
   */
  private void saveUserToFirestore(String userId, String username, String email) {
    Log.d("RegistrationActivity", "Saving user to Firestore: " + userId);

    // Ensure userId is valid before proceeding
    if (userId == null || userId.isEmpty()) {
      Log.e("RegistrationActivity", "Invalid userId. Aborting Firestore save.");
      showToast("Error: User ID is invalid. Cannot save user data.");
      reEnableRegisterButton(); // Re-enable button as save failed
      return;
    }

    // Create a new user map with user details
    Map<String, Object> user = new HashMap<>();
    user.put("username", username);
    user.put("email", email);
    user.put("createdAt", System.currentTimeMillis()); // Store creation timestamp
    user.put("favourites", new ArrayList<String>()); // Initialize an empty list for favourites

    // Add a new document with the generated userId
    db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener(aVoid -> {
              Log.d("RegistrationActivity", "User data successfully written to Firestore for UID: " + userId);
              // Navigate to MainActivity after successful registration and data save
              Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
              // Clear the task stack and start MainActivity as a new task
              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
              finish(); // Finish RegistrationActivity so user can't navigate back to it
            })
            .addOnFailureListener(e -> {
              Log.e("RegistrationActivity", "Error writing user data to Firestore for UID: " + userId, e);
              showToast("Failed to save user data. Please try again.");
              reEnableRegisterButton(); // Re-enable button as save failed
              // Optional: Consider deleting the Firebase Auth user if Firestore save fails to keep data consistent
              // FirebaseUser firebaseUser = mAuth.getCurrentUser();
              // if (firebaseUser != null) {
              // firebaseUser.delete().addOnCompleteListener(...);
              // }
            });
  }
}