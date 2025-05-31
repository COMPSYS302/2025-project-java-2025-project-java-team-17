package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CategoryAdapter;
import com.example.myapplication.models.Category;
import com.example.myapplication.utils.CrystalSeeder;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

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
    setupBottomNavigation(R.id.nav_home);


    CrystalSeeder.seedCrystalsToFirestore();

    //  Define category list
    List<Category> categories = Arrays.asList(
            new Category("Calm & Stress Relief", R.drawable.cat_1),
            new Category("Success", R.drawable.cat_2),
            new Category("Meditation", R.drawable.cat_3)
    );

    // ✅ Setup RecyclerView
    RecyclerView recyclerView = findViewById(R.id.categoryRecyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // ✅ Set adapter with click listener
    CategoryAdapter adapter = new CategoryAdapter(this, categories, view -> {
      int position = recyclerView.getChildAdapterPosition(view);
      String selectedCategory = categories.get(position).getName();
      Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
      intent.putExtra("categoryName", selectedCategory);
      startActivity(intent);
    });

    recyclerView.setAdapter(adapter);
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
