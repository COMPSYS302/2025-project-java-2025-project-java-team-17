package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

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
    showToast("Registration Successfull");
  }

  private void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
