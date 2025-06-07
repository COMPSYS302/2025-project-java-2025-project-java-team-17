package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends BaseActivity {

    private final List<Crystal> crystalList = new ArrayList<>();
    private final List<String> favouriteIds = new ArrayList<>();

    private CrystalAdapter adapter;
    private RecyclerView crystalGrid;
    private TextView noResultsMessage;
    private String categoryName;
    private ImageView ivBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setupBottomNavigation(R.id.nav_home);
        TextView title = findViewById(R.id.tv_cart_title);

        categoryName = getIntent().getStringExtra("categoryName");

        title.setText(categoryName);


        crystalGrid = findViewById(R.id.crystalGrid);
        crystalGrid.setLayoutManager(new GridLayoutManager(this, 2));

        ivBtnBack = findViewById(R.id.btn_back);

        ivBtnBack.setOnClickListener(
                v -> {
                    finish();
                });

        noResultsMessage = findViewById(R.id.noResultsMessage);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search for crystals...");
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCrystals(newText);
                return true;
            }
        });

        fetchFavouritesAndCrystals();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchFavouritesAndCrystals(); // re-fetch on resume
    }

    private void fetchFavouritesAndCrystals() {
        favouriteIds.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(document -> {
                        List<String> ids = (List<String>) document.get("favourites");
                        if (ids != null) favouriteIds.addAll(ids);
                        fetchCrystals(db);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CategoryActivity", "Failed to fetch favourites", e);
                        fetchCrystals(db);
                    });
        } else {
            fetchCrystals(db);
        }
    }

    private void fetchCrystals(FirebaseFirestore db) {
        db.collection("crystals")
                .whereEqualTo("category", categoryName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    crystalList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) {
                            crystalList.add(crystal);
                        }
                    }
                    noResultsMessage.setVisibility(View.GONE);
                    setupAdapter(crystalList);
                })
                .addOnFailureListener(e -> Log.e("CategoryActivity", "Failed to fetch crystals", e));
    }

    private void setupAdapter(List<Crystal> displayList) {
        adapter = new CrystalAdapter(this, displayList, favouriteIds, false, crystal -> {
            Log.d("CategoryActivity", "Launching DetailActivity for crystal: " + crystal.getId());
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        });
        crystalGrid.setAdapter(adapter);
    }

    private void filterCrystals(String query) {
        List<Crystal> filtered = new ArrayList<>();
        for (Crystal crystal : crystalList) {
            String name = crystal.getName().toLowerCase();
            List<String> tags = crystal.getTags();

            if (name.contains(query.toLowerCase()) ||
                    (tags != null && tags.stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))) {
                filtered.add(crystal);
            }
        }

        if (filtered.isEmpty()) {
            noResultsMessage.setVisibility(View.VISIBLE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
        }

        setupAdapter(filtered);
    }
}
