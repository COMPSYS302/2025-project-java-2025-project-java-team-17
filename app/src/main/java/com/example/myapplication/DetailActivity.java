package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapters.CrystalImageAdapter;
import com.example.myapplication.databinding.ActivityDetailBinding;
import com.example.myapplication.models.Crystal;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends BaseActivity {

    private ActivityDetailBinding binding;
    private boolean isFavorite = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String crystalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupFirebase();
        setupToolbar();
        setupBottomNavigation(R.id.nav_home);
        setupCrystalImagesRecyclerView();

        crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }

        setupClickListeners();
        loadFavoriteStatus();
        loadCrystalData();
        incrementCrystalViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        if (crystalId != null && currentUser != null) {
            checkCartStatus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void setupToolbar() {
        binding.includeTopBar.tvCartTitle.setText("");
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());
    }

    private void setupCrystalImagesRecyclerView() {
        binding.crystalImages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void setupClickListeners() {
        binding.wishlistButton.setOnClickListener(v -> toggleFavoriteStatus());
        binding.goToCartButton.setOnClickListener(v -> navigateToCart());
        binding.cartButton.setOnClickListener(v -> {
            if (currentUser != null) {
                addToCart();
            }
        });
        binding.decreaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(-1));
        binding.increaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(1));
    }

    private void loadFavoriteStatus() {
        if (currentUser == null) {
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> favourites = (List<String>) snapshot.get("favourites");
                    if (favourites != null && favourites.contains(crystalId)) {
                        isFavorite = true;
                        updateWishlistButton();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load favourites", Toast.LENGTH_SHORT).show());
    }

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

    private void navigateToCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    private void loadCrystalData() {
        db.collection("crystals").document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Crystal crystal = documentSnapshot.toObject(Crystal.class);
                    if (crystal != null) {
                        displayCrystalData(crystal);
                    }
                });
    }

    private void displayCrystalData(Crystal crystal) {
        List<String> imageUrls = crystal.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            CrystalImageAdapter imageAdapter =
                    new CrystalImageAdapter(this, imageUrls, false, null);
            binding.crystalImages.setAdapter(imageAdapter);
            setupDotsIndicator(imageUrls.size());
        }

        binding.crystalName.setText(crystal.getName());
        binding.crystalDescription.setText(crystal.getDescription());
        binding.crystalPrice.setText(String.format("NZD %.2f", crystal.getPrice()));
    }

    private void incrementCrystalViews() {
        db.collection("crystals").document(crystalId)
                .update("views", FieldValue.increment(1));
    }

    private void updateWishlistButton() {
        if (isFavorite) {
            binding.wishlistButton.setImageResource(R.drawable.purple_heart);
        } else {
            binding.wishlistButton.setImageResource(R.drawable.heart_outline);
        }
    }

    private void addToCart() {
        if (currentUser == null) {
            return;
        }

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("crystalId", crystalId);
        cartItem.put("quantity", 1);

        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .set(cartItem)
                .addOnSuccessListener(aVoid ->
                        Snackbar.make(binding.getRoot(), "Item added to cart", Snackbar.LENGTH_SHORT).show())
                .addOnFailureListener(aVoid ->
                        Snackbar.make(binding.getRoot(), "Failed to add item to cart", Snackbar.LENGTH_LONG)
                                .setAction("RETRY", v -> addToCart())
                                .show());

        checkCartStatus();
    }

    private void updateQuantityInCart(int change) {
        if (currentUser == null) {
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .get()
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

    private void removeFromCart() {
        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .delete()
                .addOnSuccessListener(aVoid -> updateCartButtonUI(0));
    }

    private void updateCartQuantity(int newQuantity) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);
        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .update(updates);
        updateCartButtonUI(newQuantity);
    }

    private void checkCartStatus() {
        if (currentUser == null) {
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    int quantity;
                    if (documentSnapshot.exists()) {
                        quantity = documentSnapshot.getLong("quantity").intValue();
                    } else {
                        quantity = 0;
                    }
                    updateCartButtonUI(quantity);
                })
                .addOnFailureListener(e -> updateCartButtonUI(0));
    }

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

    private void setupDotsIndicator(int count) {
        binding.dotsLayout.removeAllViews();
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            int dotResId;
            if (i == 0) {
                dotResId = R.drawable.dots_active;
            } else {
                dotResId = R.drawable.dots_inactive;
            }
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, dotResId));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            binding.dotsLayout.addView(dots[i], params);
        }

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

    private void updateDotsIndicator(int position, ImageView[] dots) {
        for (int i = 0; i < dots.length; i++) {
            int dotResId;
            if (i == position) {
                dotResId = R.drawable.dots_active;
            } else {
                dotResId = R.drawable.dots_inactive;
            }
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, dotResId));
        }
    }
}