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

public class RegistrationActivity extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private FirebaseFirestore db;
  private Toast currentToast;
  private Handler toastHandler = new Handler();

  private ImageView ivBtnBack;
  private Button btnRegister;
  private EditText etUsername, etEmail, etPassword, etConfirmPassword;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e("RegistrationActivity", "=== ACTIVITY STARTED ===");
    setContentView(R.layout.activity_register);

    mAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();

    ivBtnBack = findViewById(R.id.btn_back);
    btnRegister = findViewById(R.id.btnRegister);

    etUsername = findViewById(R.id.etUserName);
    etEmail = findViewById(R.id.etEmail);
    etPassword = findViewById(R.id.etPassword);
    etConfirmPassword = findViewById(R.id.etConfirmPassword);

    TextView title = findViewById(R.id.tv_cart_title);
    title.setText("Register");

    ivBtnBack.setOnClickListener(v -> finish());

    btnRegister.setOnClickListener(v -> {
      String username = etUsername.getText().toString().trim();
      String email = etEmail.getText().toString().trim();
      String password = etPassword.getText().toString();
      String confirmPassword = etConfirmPassword.getText().toString();

      if (validateInputs(username, email, password, confirmPassword)) {
        btnRegister.setEnabled(false);
        registerUser(username, email, password);
      }
    });
  }

  private boolean validateInputs(String username, String email, String password, String confirmPassword) {
    if (username.isEmpty()) {
      showToast("Username Cannot Be Empty");
      return false;
    }

    if (!email.contains("@")) {
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

    return true;
  }

  private void registerUser(String username, String email, String password) {
    Log.d("RegistrationActivity", "=== REGISTER USER FUNCTION ENTERED ===");

    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
              if (task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();
                Log.d("RegistrationActivity", "Registration successful. Saving to Firestore.");
                saveUserToFirestore(userId, username, email);
              } else {
                handleRegistrationFailure(task.getException());
              }
            });
  }

  private void handleRegistrationFailure(Exception exception) {
    reEnableRegisterButton();

    if (exception instanceof FirebaseAuthUserCollisionException) {
      showToast("Email already registered. Please use a different email.");
    } else if (exception instanceof FirebaseAuthWeakPasswordException) {
      showToast("Password is too weak.");
    } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
      showToast("Invalid email format.");
    } else {
      showToast("Registration failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
    }
  }

  private void showToast(String message) {
    if (currentToast != null) currentToast.cancel();
    currentToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
    currentToast.show();

    toastHandler.removeCallbacksAndMessages(null);
    toastHandler.postDelayed(() -> currentToast = null, 3500);
  }

  private void reEnableRegisterButton() {
    if (btnRegister != null) {
      btnRegister.setEnabled(true);
    }
  }

  private void saveUserToFirestore(String userId, String username, String email) {
    Log.d("RegistrationActivity", "Saving user to Firestore: " + userId);

    if (userId == null || userId.isEmpty()) {
      Log.e("RegistrationActivity", "Invalid userId. Aborting Firestore save.");
      showToast("Error: User ID is invalid.");
      reEnableRegisterButton();
      return;
    }

    Map<String, Object> user = new HashMap<>();
    user.put("username", username);
    user.put("email", email);
    user.put("createdAt", System.currentTimeMillis());
    user.put("favourites", new ArrayList<String>());

    db.collection("users").document(userId).set(user);

    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
    startActivity(intent);
    finish(); // optional, to prevent returning to register screen
  }
}
