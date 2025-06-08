package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.adapters.CrystalImageAdapter;
import com.example.myapplication.databinding.ActivityDetailBinding;
import com.example.myapplication.models.Crystal;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity that shows detailed information about a specific crystal.
 * Includes images, description, price, cart functionality, favorites, and similar items.
 */
public class DetailActivity extends BaseActivity {

    private ActivityDetailBinding binding;
    private boolean isFavorite = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String crystalId;
    private List<Crystal> similarCrystals = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();
    private String currentCategory;
    private CrystalAdapter similarProductsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupFirebase();
        setupToolbar();
        setupBottomNavigation(R.id.nav_home);
        setupRecyclerViews();

        crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }

        setupClickListeners();
        loadFavoriteStatus();
        loadCrystalData();
        incrementCrystalViews();
        fetchUserFavourites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        if (crystalId != null && currentUser != null) {
            checkCartStatus();
        }
        fetchUserFavourites();
    }

    /**
     * Initializes Firebase Authentication and Firestore instances.
     */
    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Sets up toolbar back button and title.
     */
    private void setupToolbar() {
        binding.includeTopBar.tvCartTitle.setText("");
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Configures the layout managers for RecyclerViews in the activity.
     */
    private void setupRecyclerViews() {
        binding.crystalImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.similarProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    /**
     * Attaches click listeners to interactive buttons.
     */
    private void setupClickListeners() {
        binding.wishlistButton.setOnClickListener(v -> toggleFavoriteStatus());
        binding.goToCartButton.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        binding.cartButton.setOnClickListener(v -> {
            if (currentUser != null) {
                addToCart();
            }
        });
        binding.decreaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(-1));
        binding.increaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(1));
    }

    /**
     * Loads the current user's favorite status for this crystal.
     */
    private void loadFavoriteStatus() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(snapshot -> {
                    List<String> favourites = (List<String>) snapshot.get("favourites");
                    if (favourites != null && favourites.contains(crystalId)) {
                        isFavorite = true;
                        updateWishlistButton();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load favourites", Toast.LENGTH_SHORT).show());
    }

    /**
     * Toggles the crystal's favorite status for the current user.
     */
    private void toggleFavoriteStatus() {
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to use favourites", Toast.LENGTH_SHORT).show();
            return;
        }

        isFavorite = !isFavorite;
        updateWishlistButton();

        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        if (isFavorite) {
            userRef.update("favourites", FieldValue.arrayUnion(crystalId));
        } else {
            userRef.update("favourites", FieldValue.arrayRemove(crystalId));
        }
    }

    /**
     * Fetches the crystal data from Firestore and displays it.
     */
    private void loadCrystalData() {
        db.collection("crystals").document(crystalId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Crystal crystal = documentSnapshot.toObject(Crystal.class);
                    if (crystal != null) {
                        currentCategory = crystal.getCategory();
                        displayCrystalData(crystal);
                        loadSimilarProducts();
                    }
                });
    }

    /**
     * Displays all crystal data on the screen including images, description, and price.
     */
    private void displayCrystalData(Crystal crystal) {
        List<String> imageUrls = crystal.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            CrystalImageAdapter imageAdapter = new CrystalImageAdapter(this, imageUrls, false, null);
            binding.crystalImages.setAdapter(imageAdapter);

            // Attach snapping behavior for clean swiping
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(binding.crystalImages);

            setupDotsIndicator(imageUrls.size());
        }

        binding.crystalName.setText(crystal.getName());
        binding.crystalDescription.setText(crystal.getDescription());
        binding.crystalPrice.setText(String.format("NZD %.2f", crystal.getPrice()));
    }

    /**
     * Increments the view count of the crystal in Firestore.
     */
    private void incrementCrystalViews() {
        db.collection("crystals").document(crystalId).update("views", FieldValue.increment(1));
    }

    /**
     * Updates the wishlist heart icon and triggers animation.
     */
    private void updateWishlistButton() {
        int iconResId = isFavorite ? R.drawable.purple_heart : R.drawable.heart_outline;
        binding.wishlistButton.setImageResource(iconResId);

        Animation anim = AnimationUtils.loadAnimation(
                this, isFavorite ? R.anim.pop : R.anim.fade_pulse
        );
        binding.wishlistButton.startAnimation(anim);
    }

    /**
     * Adds the crystal to the user's cart in Firestore.
     */
    private void addToCart() {
        if (currentUser == null) return;

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("crystalId", crystalId);
        cartItem.put("quantity", 1);

        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .set(cartItem)
                .addOnSuccessListener(aVoid -> Snackbar.make(binding.getRoot(), "Item added to cart", Snackbar.LENGTH_SHORT).show())
                .addOnFailureListener(aVoid -> Snackbar.make(binding.getRoot(), "Failed to add item to cart", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", v -> addToCart()).show());

        checkCartStatus();
    }

    /**
     * Updates quantity of the item in cart. Removes it if quantity drops to 0.
     */
    private void updateQuantityInCart(int change) {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long quantityLong = documentSnapshot.getLong("quantity");
                        int quantity = quantityLong != null ? quantityLong.intValue() : 0;
                        int newQuantity = quantity + change;

                        if (newQuantity <= 0) {
                            removeFromCart();
                        } else {
                            updateCartQuantity(newQuantity);
                        }
                    }
                });
    }

    /**
     * Removes the crystal from the cart.
     */
    private void removeFromCart() {
        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .delete()
                .addOnSuccessListener(aVoid -> updateCartButtonUI(0));
    }

    /**
     * Updates cart document with new quantity.
     */
    private void updateCartQuantity(int newQuantity) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);
        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .update(updates);
        updateCartButtonUI(newQuantity);
    }

    /**
     * Checks if the item is in the user's cart and updates UI accordingly.
     */
    private void checkCartStatus() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    int quantity = documentSnapshot.exists() ? documentSnapshot.getLong("quantity").intValue() : 0;
                    updateCartButtonUI(quantity);
                })
                .addOnFailureListener(e -> updateCartButtonUI(0));
    }

    /**
     * Updates UI based on cart quantity (show "Add to cart" or +/- controls).
     */
    private void updateCartButtonUI(int quantity) {
        if (quantity == 0) {
            binding.cartButton.setVisibility(View.VISIBLE);
            binding.quantityControlLayout.setVisibility(View.GONE);
        } else {
            binding.cartButton.setVisibility(View.GONE);
            binding.quantityControlLayout.setVisibility(View.VISIBLE);
            binding.quantityTextView.setText(String.valueOf(quantity));
        }
    }

    /**
     * Displays indicator dots that sync with crystal image scroller.
     */
    private void setupDotsIndicator(int count) {
        binding.dotsLayout.removeAllViews();
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == 0 ? R.drawable.dots_active : R.drawable.dots_inactive));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            binding.dotsLayout.addView(dots[i], params);
        }

        // Sync active dot with scroll position
        binding.crystalImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    updateDotsIndicator(layoutManager.findFirstVisibleItemPosition(), dots);
                }
            }
        });
    }

    /**
     * Highlights the dot corresponding to the currently visible image.
     */
    private void updateDotsIndicator(int position, ImageView[] dots) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == position ? R.drawable.dots_active : R.drawable.dots_inactive));
        }
    }

    /**
     * Loads products from the same category as the current crystal.
     */
    private void loadSimilarProducts() {
        if (currentCategory == null) return;

        db.collection("crystals").whereEqualTo("category", currentCategory)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    similarCrystals.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (doc.getId().equals(crystalId)) continue;
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) {
                            similarCrystals.add(crystal);
                        }
                    }
                    setupSimilarProductsAdapter();
                })
                .addOnFailureListener(e -> Log.e("DetailActivity", "Error loading similar products", e));
    }

    /**
     * Sets up the adapter for similar products and binds them to the RecyclerView.
     */
    private void setupSimilarProductsAdapter() {
        similarProductsAdapter = new CrystalAdapter(this, similarCrystals, favouriteIds, false, true, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null);
        binding.similarProducts.setAdapter(similarProductsAdapter);
    }

    /**
     * Fetches the current user's favorite crystal IDs and updates similar product list.
     */
    private void fetchUserFavourites() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(document -> {
                    List<String> ids = (List<String>) document.get("favourites");
                    if (ids != null) {
                        favouriteIds.clear();
                        favouriteIds.addAll(ids);
                        if (similarProductsAdapter != null) {
                            similarProductsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
