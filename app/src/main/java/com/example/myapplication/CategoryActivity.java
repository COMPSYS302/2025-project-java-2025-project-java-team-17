package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.Crystal;
import com.example.myapplication.utils.CrystalSeeder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends BaseActivity {

    private CrystalAdapter adapter;
    private final List<Crystal> crystalList = new ArrayList<>();  // Moved outside to share across methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);
        setupBottomNavigation(R.id.nav_home);
        String categoryName = getIntent().getStringExtra("categoryName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView crystalGrid = findViewById(R.id.crystalGrid);
        crystalGrid.setLayoutManager(new GridLayoutManager(this, 2));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        List<String> favourites = (List<String>) documentSnapshot.get("favourites");
                        if (favourites == null) favourites = new ArrayList<>();

                        adapter = new CrystalAdapter(this, crystalList, favourites, false,  crystal -> {
                            Intent intent = new Intent(this, DetailActivity.class);
                            intent.putExtra("crystalId", crystal.getId());
                            startActivity(intent);
                        });
                        crystalGrid.setAdapter(adapter);

                        fetchCrystalsByCategory(db, categoryName);
                    })
                    .addOnFailureListener(e -> {
                        adapter = new CrystalAdapter(this, crystalList, new ArrayList<>(),false,  crystal -> {
                            Intent intent = new Intent(this, DetailActivity.class);
                            intent.putExtra("crystalId", crystal.getId());
                            startActivity(intent);
                        });
                        crystalGrid.setAdapter(adapter);
                        fetchCrystalsByCategory(db, categoryName);
                    });
        } else {
            adapter = new CrystalAdapter(this, crystalList, new ArrayList<>(), false,  crystal -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("crystalId", crystal.getId());
                startActivity(intent);
            });
            crystalGrid.setAdapter(adapter);
            fetchCrystalsByCategory(db, categoryName);
        }
    }

    private void fetchCrystalsByCategory(FirebaseFirestore db, String categoryName) {
        db.collection("crystals")
                .whereEqualTo("category", categoryName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        crystalList.add(crystal);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error fetching crystals", e)
                );
    }
}

