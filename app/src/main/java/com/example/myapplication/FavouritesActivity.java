package com.example.myapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ImageView ivBtnBack;
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

        loadFavouritesFromFirestore();
        emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setVisibility(View.GONE);

        TextView title = findViewById(R.id.tv_cart_title);

        title.setText("Favourites");

        ivBtnBack = findViewById(R.id.btn_back);

        ivBtnBack.setOnClickListener(
                v -> {
                    finish();
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
                                        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                                            @Override
                                            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                                                super.getItemOffsets(outRect, view, parent, state);

                                                // Add bottom spacing between items (e.g., 16 pixels)
                                                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                                                    outRect.bottom = 16; // change this value to control spacing (in pixels)
                                                }
                                            }
                                        });
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
