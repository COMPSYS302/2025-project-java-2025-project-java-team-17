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
import com.example.myapplication.databinding.ActivityFavouritesBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of crystals the user has marked as favourites.
 * Users can also add these items to their cart or clear all favourites.
 */
public class FavouritesActivity extends BaseActivity {


    private CrystalAdapter adapter;

    private List<Crystal> favouriteCrystals = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private ActivityFavouritesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set bottom nav state
        setupBottomNavigation(R.id.nav_profile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initViews();
        setupRecyclerView();
        loadFavouritesFromFirestore();
    }

    /**
     * Initializes toolbar buttons and empty message placeholder.
     */
    private void initViews() {

        binding.includeTopBar.tvCartTitle.setText("Favourites");


        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());


        binding.includeTopBar.btnClearCart.setImageResource(R.drawable.ic_trash);
        binding.includeTopBar.btnClearCart.setOnClickListener(v -> clearFavourites());


        binding.emptyMessage.setVisibility(View.GONE);
    }

    /**
     * Configures the favourites RecyclerView with a grid layout and adapter.
     */
    private void setupRecyclerView() {

        binding.favouritesRecycler.setLayoutManager(new GridLayoutManager(this, 1));

        adapter = new CrystalAdapter(
                this,
                favouriteCrystals,
                favouriteIds,
                true,  // This is a favourite view
                false, // Not for main home view
                crystal -> {},  // No click action defined
                this::addToCart // Add to cart from favourite
        );

        binding.favouritesRecycler.setAdapter(adapter);
    }

    /**
     * Loads the list of favourite crystal IDs from Firestore.
     */
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

    /**
     * Fetches full crystal data from Firestore for the given list of IDs.
     */
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

    /**
     * Updates the RecyclerView with new data and triggers animations.
     */
    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();

        // Apply layout animation for a smoother entry
        LayoutAnimationController animation =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        binding.favouritesRecycler.setLayoutAnimation(animation);
        binding.favouritesRecycler.scheduleLayoutAnimation();

        binding.favouritesRecycler.setVisibility(View.VISIBLE);
        binding.emptyMessage.setVisibility(View.GONE);

        // Add spacing between items
        binding.favouritesRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 16; // 16 pixels of spacing
                }
            }
        });
    }

    /**
     * Shows the empty state message if no favourites exist.
     */
    private void showEmptyState() {
        binding.emptyMessage.setVisibility(View.VISIBLE);
        binding.favouritesRecycler.setVisibility(View.GONE);
    }

    /**
     * Clears the entire list of favourites for the current user.
     */
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

    /**
     * Adds a given crystal to the user's cart, incrementing quantity if it already exists.
     *
     * @param crystal The crystal to add to cart
     */
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
