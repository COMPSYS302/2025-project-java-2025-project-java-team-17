package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    setupLoginButton();
    setupRegisterButton();

    CardView guidanceCard = findViewById(R.id.guidanceCard);
    CardView successCard = findViewById(R.id.successCard);
    CardView meditationCard = findViewById(R.id.meditationCard);

    View.OnClickListener goToCategoryActivity = new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        startActivity(intent);
      }
    };
    guidanceCard.setOnClickListener(goToCategoryActivity);
    successCard.setOnClickListener(goToCategoryActivity);
    meditationCard.setOnClickListener(goToCategoryActivity);
  }

  private void setupLoginButton() {
    Button loginButton = findViewById(R.id.loginButton);
    loginButton.setOnClickListener(v -> navigateToLogin());
  }

  private void navigateToLogin() {
    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
    startActivity(intent);
  }

  private void setupRegisterButton() {
    Button registerButton = findViewById(R.id.registerButton);
    registerButton.setOnClickListener(v -> navigateToRegister());
  }

  private void navigateToRegister() {
    Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
    startActivity(intent);
  }
}
