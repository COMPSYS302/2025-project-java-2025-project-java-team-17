package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

public class FavouritesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CrystalAdapter adapter;
    private List<Crystal> favouriteCrystals = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setupBottomNavigation(R.id.nav_profile); // Highlights the Profile icon

        recyclerView = findViewById(R.id.favouritesRecycler);
        adapter = new CrystalAdapter(this, favouriteCrystals, favouriteIds, true, null);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);

        loadFavouritesFromFirestore();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // <-- this sets the toolbar as the app bar

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            finish(); // go back to previous screen (e.g., Profile)
        });
        Objects.requireNonNull(getSupportActionBar()).setTitle("Favourites");



    }

    private void loadFavouritesFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    List<String> ids = (List<String>) document.get("favourites");
                    if (ids != null) {
                        favouriteIds.addAll(ids);

                        // Now fetch crystal details
                        db.collection("crystals")
                                .whereIn("id", ids)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot doc : querySnapshot) {
                                        Crystal crystal = doc.toObject(Crystal.class);
                                        if (crystal != null) {
                                            favouriteCrystals.add(crystal);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to load crystals", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to load favourites", e));
    }
}
