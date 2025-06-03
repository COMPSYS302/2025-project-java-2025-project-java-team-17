package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
  private static final String TAG = "LoginActivity";
  private FirebaseAuth mAuth;
  private EditText etLoginEmail;
  private EditText etLoginPassword;
  private Button btnLogin;
  private TextView tvLoginError;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAuth = FirebaseAuth.getInstance();

    etLoginEmail = findViewById(R.id.etLoginEmail);
    etLoginPassword = findViewById(R.id.etLoginPassword);
    btnLogin = findViewById(R.id.btnLogin);
    tvLoginError = findViewById(R.id.tvLoginError);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle("Crysta");
    }

    toolbar.setNavigationOnClickListener(v -> finish());

    btnLogin.setOnClickListener(
        v -> {
          String email = etLoginEmail.getText().toString().trim();
          String password = etLoginPassword.getText().toString().trim();

          if (email.isEmpty()) {
            etLoginEmail.setError("Email is Required");
          }

          if (password.isEmpty()) {
            etLoginEmail.setError("Password is Required");
          }

          if (!password.isEmpty() && !email.isEmpty()) {

            loginUser(email, password);
          }
        });
  }

  private void loginUser(String email, String password) {
    mAuth
        .signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            this,
            task -> {
              if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
              } else {
                tvLoginError.setVisibility(View.VISIBLE);

                new Handler(Looper.getMainLooper())
                    .postDelayed(
                        new Runnable() {
                          @Override
                          public void run() {
                            if (tvLoginError != null) {
                              tvLoginError.setVisibility(View.GONE);
                            }
                          }
                        },
                        5000);
              }
            });
  }
}
