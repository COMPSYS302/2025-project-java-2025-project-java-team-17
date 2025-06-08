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

    initButtons();
  }

  private void initButtons() {
    Button loginButton = findViewById(R.id.loginButton);
    Button registerButton = findViewById(R.id.registerButton);

    loginButton.setOnClickListener(v -> openLogin());
    registerButton.setOnClickListener(v -> openRegister());
  }

  private void openLogin() {
    startActivity(new Intent(this, LoginActivity.class));
  }

  private void openRegister() {
    startActivity(new Intent(this, RegistrationActivity.class));
  }
}
