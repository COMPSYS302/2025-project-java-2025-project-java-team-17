package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);

    setupLoginButton();
    setupRegisterButton();
  }

  private void setupLoginButton() {
    Button loginButton = findViewById(R.id.loginButton);
    loginButton.setOnClickListener(v -> navigateToLogin());
  }

  private void navigateToLogin() {
    Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  private void setupRegisterButton() {
    Button registerButton = findViewById(R.id.registerButton);
    registerButton.setOnClickListener(v -> navigateToRegister());
  }

  private void navigateToRegister() {
    Intent intent = new Intent(AuthActivity.this, RegistrationActivity.class);
    startActivity(intent);
  }
}
