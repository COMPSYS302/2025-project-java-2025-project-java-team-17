package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapters.CategoryAdapter;
import com.example.myapplication.models.Category;
import com.example.myapplication.utils.CrystalSeeder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private FirebaseAuth mAuth;
  private FirebaseUser currentUser;
  private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(
        findViewById(R.id.main),
        (v, insets) -> {
          Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
          v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
          return insets;
        });

    CrystalSeeder.seedCrystalsToFirestore();

    //  Define category list
    List<Category> categories =
        Arrays.asList(
            new Category("Calm & Stress Relief", R.drawable.cat_1),
            new Category("Success", R.drawable.cat_2),
            new Category("Meditation", R.drawable.cat_3));

    // ✅ Setup RecyclerView
    RecyclerView recyclerView = findViewById(R.id.categoryRecyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // ✅ Set adapter with click listener
    CategoryAdapter adapter =
        new CategoryAdapter(
            this,
            categories,
            view -> {
              int position = recyclerView.getChildAdapterPosition(view);
              String selectedCategory = categories.get(position).getName();
              Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
              intent.putExtra("categoryName", selectedCategory);
              startActivity(intent);
            });

    recyclerView.setAdapter(adapter);
  }

  @Override
  protected void onStart() {
    Log.d("MainActivity", "onStart() called");
    super.onStart();

    currentUser = mAuth.getCurrentUser();
    updateUI(currentUser);
  }

  private void updateUI(FirebaseUser user) {
    Log.d("MainActivity", "updateUI() called with user: " + user);


    if (user != null) {
      Log.d("MainActivity", "User is not null");
      db.collection("users")
          .document(user.getUid())
          .get()
          .addOnSuccessListener(
              documentSnapshot -> {
                if (documentSnapshot.exists()) {
                  String username = documentSnapshot.getString("username");
                  Log.d("MainActivity", "Username: " + username);
                }
              });
      
    }
  }
}
