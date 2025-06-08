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

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvLoginError, title;
    private ImageView ivBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginError = findViewById(R.id.tvLoginError);
        title = findViewById(R.id.tv_cart_title);
        ivBtnBack = findViewById(R.id.btn_back);

        title.setText("Login");
    }

    private void setupListeners() {
        ivBtnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            boolean valid = true;

            if (email.isEmpty()) {
                etLoginEmail.setError("Email is required");
                valid = false;
            }

            if (password.isEmpty()) {
                etLoginPassword.setError("Password is required");
                valid = false;
            }

            if (valid) {
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showLoginError();
                    }
                });
    }

    private void showLoginError() {
        tvLoginError.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (tvLoginError != null) {
                tvLoginError.setVisibility(View.GONE);
            }
        }, 5000);
    }
}
