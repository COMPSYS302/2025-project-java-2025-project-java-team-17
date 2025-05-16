package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
  private FirebaseAuth mAuth;
  private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    mAuth = FirebaseAuth.getInstance(); // Obtain a singleton instance of FirebaseAuth
    db = FirebaseFirestore.getInstance();

    EditText etUsername = findViewById(R.id.etUserName);
    EditText etEmail = findViewById(R.id.etEmail);
    EditText etPassword = findViewById(R.id.etPassword);
    EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
    Button btnRegister = findViewById(R.id.btnRegister);

    btnRegister.setOnClickListener(
        v -> {
          String username = etUsername.getText().toString().trim();
          String email = etEmail.getText().toString().trim();
          String password = etPassword.getText().toString();
          String confirmPassword = etConfirmPassword.getText().toString();

          if (validateInputs(username, email, password, confirmPassword)) {
            registerUser(username, email, password);
          }
        });
  }

  private boolean validateInputs(
      String username, String email, String password, String confirmPassword) {
    if (username.isEmpty()) {
      showToast("Username Cannot Be Empty");
      return false;
    }

    if (email.isEmpty() || !email.contains("@")) {
      showToast("Please Enter A Valid Email");
      return false;
    }

    if (password.isEmpty() || password.length() < 6) {
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

    mAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            this,
            task -> {
              if (task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();
                saveUserToFirestore(userId, username, email);
              } else {
                Exception exception = task.getException();

                if (exception instanceof FirebaseAuthUserCollisionException) {
                  showToast("Email already registered. Please use a different email.");
                } else if (exception instanceof FirebaseAuthWeakPasswordException) {
                  showToast("Password is too weak: " + exception.getMessage());
                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                  showToast("Invalid email format.");
                } else {
                  showToast("Registration failed: " + exception.getMessage());
                }
              }
            });
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void saveUserToFirestore(String userId, String username, String email) {
    Map<String, Object> user = new HashMap<>();

    user.put("username", username);
    user.put("email", email);
    user.put("createdAt", System.currentTimeMillis());

    db.collection("users")
        .document(userId)
        .set(user)
        .addOnSuccessListener(
            aVoid -> {
              showToast("Registration Successful!");

              Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
              startActivity(intent);
              finish();
            })
        .addOnFailureListener(
            e -> {
              showToast("Error Saving User Data: " + e.getMessage());
            });
  }
}
