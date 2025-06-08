package com.example.myapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.CartItem;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CrystalAdapter adapter;
    private TextView emptyMessage;
    private List<Crystal> favouriteCrystals = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setupBottomNavigation(R.id.nav_profile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initViews();
        setupRecyclerView();
        loadFavouritesFromFirestore();
    }

    private void initViews() {
        TextView title = findViewById(R.id.tv_cart_title);
        title.setText("Favourites");

        ImageView ivBtnBack = findViewById(R.id.btn_back);
        ivBtnBack.setOnClickListener(v -> finish());

        ImageButton clearCart = findViewById(R.id.btn_clear_cart);
        clearCart.setImageResource(R.drawable.ic_trash);
        clearCart.setOnClickListener(v -> clearFavourites());

        emptyMessage = findViewById(R.id.emptyMessage);
        emptyMessage.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.favouritesRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        adapter = new CrystalAdapter(
                this,
                favouriteCrystals,
                favouriteIds,
                true,
                crystal -> {},  // No click for now
                this::addToCart
        );

        recyclerView.setAdapter(adapter);
    }

    private void loadFavouritesFromFirestore() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    List<String> ids = (List<String>) document.get("favourites");

                    if (ids != null && !ids.isEmpty()) {
                        favouriteIds.addAll(ids);
                        fetchCrystalsByIds(ids);
                    } else {
                        showEmptyState();
                    }
                })
                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to load favourites", e));
    }

    private void fetchCrystalsByIds(List<String> ids) {
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

                    if (favouriteCrystals.isEmpty()) {
                        showEmptyState();
                    } else {
                        updateRecyclerView();
                    }
                })
                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to load crystals", e));
    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();

        LayoutAnimationController animation =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        recyclerView.setLayoutAnimation(animation);
        recyclerView.scheduleLayoutAnimation();

        recyclerView.setVisibility(View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 16; // Spacing in pixels
                }
            }
        });
    }

    private void showEmptyState() {
        emptyMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void clearFavourites() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .update("favourites", new ArrayList<>())
                .addOnSuccessListener(aVoid -> {
                    favouriteIds.clear();
                    favouriteCrystals.clear();
                    adapter.notifyDataSetChanged();
                    showEmptyState();
                })
                .addOnFailureListener(e -> Log.e("FavouritesActivity", "Failed to clear favourites", e));
    }

    private void addToCart(Crystal crystal) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(crystal.getId())
                .get()
                .addOnSuccessListener(doc -> {
                    int newQuantity = 1;
                    if (doc.exists()) {
                        Long existingQty = doc.getLong("quantity");
                        if (existingQty != null) {
                            newQuantity = existingQty.intValue() + 1;
                        }
                        db.collection("users")
                                .document(userId)
                                .collection("cart")
                                .document(crystal.getId())
                                .update("quantity", newQuantity);
                    } else {
                        db.collection("users")
                                .document(userId)
                                .collection("cart")
                                .document(crystal.getId())
                                .set(new CartItem(crystal, newQuantity));
                    }
                });
    }
}
