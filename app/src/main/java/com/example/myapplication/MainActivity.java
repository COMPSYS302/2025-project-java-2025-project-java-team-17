package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapters.CategoryAdapter;
import com.example.myapplication.adapters.CrystalImageAdapter;
import com.example.myapplication.models.Category;
import com.example.myapplication.models.Crystal;
import com.example.myapplication.utils.CrystalSeeder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class MainActivity extends BaseActivity {

  private FirebaseAuth mAuth;
  private FirebaseUser currentUser;
  private FirebaseFirestore db;

  private PriorityQueue<Crystal> pq;

  private Crystal[] topCrystals;
  private List<String> topImages;

  private TextView crystalTitleBar;
  private TextView crystalPriceBar;

  private String[] crystalTitles = new String[3];
  private double[] crystalPrices = new double[3];



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
    setupBottomNavigation(R.id.nav_home);

    CrystalSeeder.seedCrystalsToFirestore();

    RecyclerView crystalImages = findViewById(R.id.crystalImages);

    crystalTitleBar = findViewById(R.id.crystalTitleBar);
    crystalPriceBar = findViewById(R.id.crystalPriceBar);


    crystalImages.setLayoutManager(new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false));

    pq = new PriorityQueue<>(
            (a, b) -> Integer.compare(a.getViews(), b.getViews())
          );

    db.collection("crystals")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
              for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                Crystal crystal = doc.toObject(Crystal.class);
                if(pq.size() < 3){
                  pq.add(crystal);
                }else{
                  int lowest = pq.peek().getViews();
                  if(crystal.getViews() > lowest){
                    pq.poll();
                    pq.add(crystal);
                  }

                }
              }

              topCrystals = new Crystal[3];
              topImages = new ArrayList<>();
              for(int i=2;i>=0;i--){
                Crystal crystal = pq.poll();
                topCrystals[i] = crystal;
                topImages.add(0,crystal.getImageUrls().get(0));
                crystalTitles[i] = crystal.getName();
                crystalPrices[i] = crystal.getPrice();
              }

              if (topImages != null && !topImages.isEmpty()) {

                  CrystalImageAdapter imageAdapter = new CrystalImageAdapter(
                          this,
                          topImages,
                          true,
                          position -> {
                              if (position < topCrystals.length) {
                                  Crystal crystal = topCrystals[position];
                                  Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                  intent.putExtra("crystalId", crystal.getId());
                                  startActivity(intent);
                              }
                          }
                  );

                  crystalImages.setAdapter(imageAdapter);
                  setupDotsIndicator(topImages.size(), crystalImages);
              }
            }).addOnFailureListener(e -> {
                Log.e("MainActivity", "Error loading crystals", e);
            });

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

  private void setupDotsIndicator(int count, RecyclerView recyclerView) {
    LinearLayout dotsLayout = findViewById(R.id.dotsLayout);
    dotsLayout.removeAllViews();

    ImageView[] dots = new ImageView[count];
    for (int i = 0; i < count; i++) {
      dots[i] = new ImageView(this);
      dots[i].setImageDrawable(ContextCompat.getDrawable(this,
              i == 0 ? R.drawable.dots_active : R.drawable.dots_inactive));

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.WRAP_CONTENT,
              LinearLayout.LayoutParams.WRAP_CONTENT
      );
      params.setMargins(8, 0, 8, 0);
      dotsLayout.addView(dots[i], params);
    }

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

          if (firstVisibleItemPosition >= 0 && firstVisibleItemPosition < crystalTitles.length) {
              crystalTitleBar.setText(crystalTitles[firstVisibleItemPosition]);
              crystalPriceBar.setText(String.format("%.2f /kg",crystalPrices[firstVisibleItemPosition]));
          }

        for (int i = 0; i < dots.length; i++) {
          dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                  i == firstVisibleItemPosition ? R.drawable.dots_active : R.drawable.dots_inactive));
        }
      }
    });

    if (crystalTitles.length > 0) {
        crystalTitleBar.setText(crystalTitles[0]);
        crystalPriceBar.setText(String.format("%.2f /kg",crystalPrices[0]));

    }
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
