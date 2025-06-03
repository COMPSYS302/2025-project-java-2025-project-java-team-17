package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalImageAdapter;
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

    private ImageButton wishlistButton;
    private LinearLayout cartButton;
    private boolean isFavorite = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private TextView tvCartButtonText;
    private LinearLayout quantityControlLayout;
    private TextView goToCartButton;
    private Button decreaseQuantityButton;
    private Button increaseQuantityButton;
    private TextView tvQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        setupBottomNavigation(R.id.nav_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        wishlistButton = findViewById(R.id.wishlistButton);
        tvCartButtonText = findViewById(R.id.cartButtonText);
        quantityControlLayout = findViewById(R.id.quantityControlLayout);
        goToCartButton = findViewById(R.id.goToCartButton);
        tvQuantityTextView = findViewById(R.id.quantityTextView);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);

        RecyclerView crystalImages = findViewById(R.id.crystalImages);
        crystalImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(snapshot -> {
                List<String> favourites = (List<String>) snapshot.get("favourites");
                if (favourites != null && favourites.contains(crystalId)) {
                    isFavorite = true;
                    updateWishlistButton();
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to load favourites", Toast.LENGTH_SHORT).show());
        }

        wishlistButton.setOnClickListener(v -> {
            if (currentUser != null) {
                String userId = currentUser.getUid();
                isFavorite = !isFavorite;
                updateWishlistButton();
                DocumentReference userRef = db.collection("users").document(userId);
                if (isFavorite) {
                    userRef.update("favourites", FieldValue.arrayUnion(crystalId));
                } else {
                    userRef.update("favourites", FieldValue.arrayRemove(crystalId));
                }
            } else {
                Toast.makeText(this, "Please log in to use favourites", Toast.LENGTH_SHORT).show();
            }
        });

        goToCartButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        db.collection("crystals").document(crystalId).update("views", FieldValue.increment(1));
        db.collection("crystals").document(crystalId).get().addOnSuccessListener(documentSnapshot -> {
            Crystal crystal = documentSnapshot.toObject(Crystal.class);
            if (crystal != null) {
                List<String> imageUrls = crystal.getImageUrls();
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    CrystalImageAdapter imageAdapter = new CrystalImageAdapter(this, imageUrls, false, null);
                    crystalImages.setAdapter(imageAdapter);
                    setupDotsIndicator(imageUrls.size(), crystalImages);
                }
                ((TextView) findViewById(R.id.crystalName)).setText(crystal.getName());
                ((TextView) findViewById(R.id.crystalDescription)).setText(crystal.getDescription());
                ((TextView) findViewById(R.id.crystalPrice)).setText(String.format("%.2f $ / kg", crystal.getPrice()));
            }
        });

        cartButton = findViewById(R.id.cartButton);
        checkCartStatus(crystalId);
        cartButton.setOnClickListener(v -> {
            if (currentUser != null) {
                addToCart(crystalId);
            }
        });

        decreaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(crystalId, -1));
        increaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(crystalId, 1));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateWishlistButton() {
        wishlistButton.setImageResource(isFavorite ? R.drawable.filled_favourite_icon : R.drawable.favourite_icon);
    }

    private void addToCart(String crystalId) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("crystalId", crystalId);
        cartItem.put("quantity", 1);

        db.collection("users").document(userId).collection("cart").document(crystalId)
                .set(cartItem)
                .addOnSuccessListener(aVoid -> Snackbar.make(cartButton, "Item added to cart", Snackbar.LENGTH_SHORT).show())
                .addOnFailureListener(aVoid ->
                        Snackbar.make(cartButton, "Failed to add item to cart", Snackbar.LENGTH_LONG)
                                .setAction("RETRY", v -> addToCart(crystalId)).show());

        checkCartStatus(crystalId);
    }

    private void updateQuantityInCart(String crystalId, int change) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        db.collection("users").document(userId).collection("cart").document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long quantityLong = documentSnapshot.getLong("quantity");
                        int quantity = quantityLong != null ? quantityLong.intValue() : 0;
                        int newQuantity = quantity + change;

                        if (newQuantity <= 0) {
                            db.collection("users").document(userId).collection("cart").document(crystalId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> updateCartButtonUI(0));
                        } else {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("quantity", newQuantity);
                            db.collection("users").document(userId).collection("cart").document(crystalId)
                                    .update(updates);
                            updateCartButtonUI(newQuantity);
                        }
                    }
                });
    }

    private void checkCartStatus(String crystalId) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        db.collection("users").document(userId).collection("cart").document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long quantityLong = documentSnapshot.getLong("quantity");
                        int quantity = quantityLong != null ? quantityLong.intValue() : 0;
                        updateCartButtonUI(quantity);
                    } else {
                        updateCartButtonUI(0);
                    }
                })
                .addOnFailureListener(e -> updateCartButtonUI(0));
    }

    private void updateCartButtonUI(int quantity) {
        if (quantity == 0) {
            cartButton.setVisibility(View.VISIBLE);
            quantityControlLayout.setVisibility(View.GONE);
        } else {
            cartButton.setVisibility(View.GONE);
            quantityControlLayout.setVisibility(View.VISIBLE);
            tvQuantityTextView.setText(String.valueOf(quantity));
        }
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
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(DetailActivity.this,
                            i == firstVisibleItemPosition ? R.drawable.dots_active : R.drawable.dots_inactive));
                }
            }
        });
    }
}
