package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
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
    private Crystal[] topCrystals = new Crystal[3];
    private List<String> topImages = new ArrayList<>();
    private String[] crystalTitles = new String[3];
    private double[] crystalPrices = new double[3];

    private TextView crystalTitleBar;
    private TextView crystalPriceBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setupBottomNavigation(R.id.nav_home);
        CrystalSeeder.seedCrystalsToFirestore();

        crystalTitleBar = findViewById(R.id.crystalTitleBar);
        crystalPriceBar = findViewById(R.id.crystalPriceBar);
        RecyclerView crystalImages = findViewById(R.id.crystalImages);
        crystalImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        setupTopCrystals(crystalImages);
        setupCategories();
        setupSearchBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void setupTopCrystals(RecyclerView crystalImages) {
        pq = new PriorityQueue<>((a, b) -> Integer.compare(a.getViews(), b.getViews()));

        db.collection("crystals").get().addOnSuccessListener(query -> {
            for (DocumentSnapshot doc : query.getDocuments()) {
                Crystal crystal = doc.toObject(Crystal.class);
                if (pq.size() < 3) pq.add(crystal);
                else if (crystal.getViews() > pq.peek().getViews()) {
                    pq.poll();
                    pq.add(crystal);
                }
            }

            for (int i = 2; i >= 0; i--) {
                Crystal crystal = pq.poll();
                topCrystals[i] = crystal;
                topImages.add(0, crystal.getImageUrls().get(0));
                crystalTitles[i] = crystal.getName();
                crystalPrices[i] = crystal.getPrice();
            }

            if (!topImages.isEmpty()) {
                CrystalImageAdapter adapter = new CrystalImageAdapter(this, topImages, true, position -> {
                    if (position < topCrystals.length) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("crystalId", topCrystals[position].getId());
                        startActivity(intent);
                    }
                });
                crystalImages.setAdapter(adapter);
                setupDotsIndicator(topImages.size(), crystalImages);
            }
        }).addOnFailureListener(e -> Log.e("MainActivity", "Error loading crystals", e));
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
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int pos = layoutManager.findFirstVisibleItemPosition();

                if (pos >= 0 && pos < crystalTitles.length) {
                    crystalTitleBar.setText(crystalTitles[pos]);
                    crystalPriceBar.setText(String.format("%.2f /kg", crystalPrices[pos]));
                }

                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                            i == pos ? R.drawable.dots_active : R.drawable.dots_inactive));
                }
            }
        });

        if (crystalTitles.length > 0) {
            crystalTitleBar.setText(crystalTitles[0]);
            crystalPriceBar.setText(String.format("%.2f /kg", crystalPrices[0]));
        }
    }

    private void setupCategories() {
        List<Category> categories = Arrays.asList(
                new Category("Calm & Stress Relief", R.drawable.cat_1),
                new Category("Success", R.drawable.cat_2),
                new Category("Meditation", R.drawable.cat_3));

        RecyclerView recyclerView = findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CategoryAdapter adapter = new CategoryAdapter(this, categories, view -> {
            int position = recyclerView.getChildAdapterPosition(view);
            String selectedCategory = categories.get(position).getName();
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.putExtra("categoryName", selectedCategory);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.scheduleLayoutAnimation();
    }

    private void setupSearchBar() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search crystals...");
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra("query", query.trim());
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                String username = document.getString("username");
                Log.d("MainActivity", "Username: " + username);
            });
        }
    }
}
