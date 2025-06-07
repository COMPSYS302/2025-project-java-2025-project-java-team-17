package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavouritesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CrystalAdapter adapter;
    private List<Crystal> favouriteCrystals = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();

    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setupBottomNavigation(R.id.nav_profile); // Highlights the Profile icon

        recyclerView = findViewById(R.id.favouritesRecycler);
        adapter = new CrystalAdapter(this, favouriteCrystals, favouriteIds, true, null);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);

        adapter = new CrystalAdapter(this, favouriteCrystals, favouriteIds, true, new CrystalAdapter.OnCartClickListener() {
            @Override
            public void onCartClick(Crystal crystal) {
                addToCart(crystal.getId());
            }
        });

        loadFavouritesFromFirestore();
        emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setVisibility(View.GONE);

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

    private void addToCart(String crystalId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }
    
        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("crystalId", crystalId);
        cartItem.put("quantity", 1);
    
        db.collection("users")
            .document(userId)
            .collection("cart")
            .document(crystalId)
            .set(cartItem)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
            });
    }

    private void loadFavouritesFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(document -> {
                    List<String> ids = (List<String>) document.get("favourites");
                    if (ids != null && !ids.isEmpty()) {
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
                                    if (favouriteCrystals.isEmpty()) {
                                        emptyMessage.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        emptyMessage.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to load crystals", e));
                    } else{
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to load favourites", e));
    }
}
