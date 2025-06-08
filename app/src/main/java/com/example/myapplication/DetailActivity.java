package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * DetailActivity displays detailed information about a crystal,
 * including its images, description, price, and options to add to cart or wishlist.
 */
public class DetailActivity extends BaseActivity {


    // Initializing variables
    private ActivityDetailBinding binding;
    private boolean isFavorite = false;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private String crystalId;
    private RecyclerView similarProductsRecyclerView;
    private CrystalAdapter similarProductsAdapter;
    private List<Crystal> similarCrystals = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();
    private String currentCategory;

    /**
     * onCreate initializes the activity, sets up Firebase, toolbar, bottom navigation, and the crystal images RecyclerView.
     * 
     * @param savedInstanceState Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

      // Call the superclass's onCreate method
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase and UI components
        setupFirebase();
        setupToolbar();
        setupBottomNavigation(R.id.nav_home);
        setupCrystalImagesRecyclerView();

        // Get the crystal ID from the intent
        crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }

        // Set up the RecyclerView for similar products and initialize the list
        setupClickListeners();
        loadFavoriteStatus();
        loadCrystalData();
        incrementCrystalViews();
        fetchUserFavourites();
    }

    /**
     * onResume is called when the activity becomes visible to the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
        currentUser = mAuth.getCurrentUser();

        // Check if the crystal ID and current user are not null before checking cart status
        if (crystalId != null && currentUser != null) {
            checkCartStatus();
        }
        fetchUserFavourites();
    }


    /**
     * setupFirebase initializes Firebase components such as FirebaseAuth and FirebaseFirestore.
     */
    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * setupToolbar configures the toolbar for the activity,
     */
    private void setupToolbar() {
        binding.includeTopBar.tvCartTitle.setText("");
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());
    }


    /** 
     * setupCrystalImagesRecyclerView sets up the RecyclerView for displaying crystal images
     */
    private void setupCrystalImagesRecyclerView() {
      // Set up the RecyclerView for crystal images
        binding.crystalImages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        // Set up the RecyclerView for similar products
        binding.similarProducts.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    /**
     * setupClickListeners sets up click listeners for various buttons in the activity.
     */
    private void setupClickListeners() {

      // Set up click listeners for buttons
        binding.wishlistButton.setOnClickListener(v -> toggleFavoriteStatus());
        binding.goToCartButton.setOnClickListener(v -> navigateToCart());

        // Check if the current user is logged in before setting up the cart button
        binding.cartButton.setOnClickListener(v -> {
            if (currentUser != null) {
                addToCart();
            }
        });

        // Set up click listeners for quantity buttons
        binding.decreaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(-1));
        binding.increaseQuantityButton.setOnClickListener(v -> updateQuantityInCart(1));
    }

    /**
     * loadFavoriteStatus checks if the current crystal is in the user's favorites.
     */
    private void loadFavoriteStatus() {
        if (currentUser == null) {
            return;
        }

        // Retrieve the user's favourites from Firestore
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

    /**
     * toggleFavoriteStatus toggles the favorite status of the current crystal.
     */
    private void toggleFavoriteStatus() {

      // Check if the user is logged in before toggling the favorite status
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to use favourites", Toast.LENGTH_SHORT).show();
            return;
        }

        // Toggle the favorite status
        isFavorite = !isFavorite;
        updateWishlistButton();

        // Update the Firestore database with the new favorite status
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        if (isFavorite) {
            userRef.update("favourites", FieldValue.arrayUnion(crystalId));
        } else {
            userRef.update("favourites", FieldValue.arrayRemove(crystalId));
        }
    }

    /**
     * navigateToCart starts the CartActivity.
     */
    private void navigateToCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    /**
     * loadCrystalData retrieves the crystal data from Firestore and displays it.
     */
    private void loadCrystalData() {

      // Retrieve the crystal data from Firestore using the crystalId
        db.collection("crystals").document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Crystal crystal = documentSnapshot.toObject(Crystal.class);
                    if (crystal != null) {
                        displayCrystalData(crystal);
                    }
                });

        // Retrieve the crystal data again to ensure the category is set before loading similar products
        db.collection("crystals").document(crystalId)
                .get()
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
     * displayCrystalData populates the UI with the crystal's data.
     */
    private void displayCrystalData(Crystal crystal) {
        List<String> imageUrls = crystal.getImageUrls();

        // Check if the crystal has images and set up the image adapter
        if (imageUrls != null && !imageUrls.isEmpty()) {
            CrystalImageAdapter imageAdapter =
                    new CrystalImageAdapter(this, imageUrls, false, null);
            binding.crystalImages.setAdapter(imageAdapter);
            setupDotsIndicator(imageUrls.size());
        }

        // Set the crystal details in the UI
        binding.crystalName.setText(crystal.getName());
        binding.crystalDescription.setText(crystal.getDescription());
        binding.crystalPrice.setText(String.format("NZD %.2f", crystal.getPrice()));
    }

    /**
     * incrementCrystalViews increments the view count of the crystal in Firestore.
     */
    private void incrementCrystalViews() {
        db.collection("crystals").document(crystalId)
                .update("views", FieldValue.increment(1));
    }

    /**
     * updateWishlistButton updates the wishlist button icon based on the favorite status.
     */
    private void updateWishlistButton() {

      // Update the wishlist button icon based on whether the crystal is a favorite
        if (isFavorite) {
            binding.wishlistButton.setImageResource(R.drawable.purple_heart);
        } else {
            binding.wishlistButton.setImageResource(R.drawable.heart_outline);
        }
    }

    /**
     * addToCart adds the current crystal to the user's cart.
     */
    private void addToCart() {
        if (currentUser == null) {
            return;
        }


        // Create a map to hold the cart item data
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("crystalId", crystalId);
        cartItem.put("quantity", 1);

        // Add the cart item to the user's cart in Firestore
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

    /**
     * updateQuantityInCart updates the quantity of the crystal in the user's cart.
     * @param change
     */
    private void updateQuantityInCart(int change) {
        if (currentUser == null) {
            return;
        }

        // Check if the crystal is already in the cart
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

    /**
     * removeFromCart removes the current crystal from the user's cart.
     */
    private void removeFromCart() {
        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .delete()
                .addOnSuccessListener(aVoid -> updateCartButtonUI(0));
    }

    /**
     * updateCartQuantity updates the quantity of the crystal in the user's cart.
     * @param newQuantity
     */
    private void updateCartQuantity(int newQuantity) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);

        // Update the quantity of the crystal in the user's cart in Firestore
        db.collection("users").document(currentUser.getUid())
                .collection("cart").document(crystalId)
                .update(updates);
        updateCartButtonUI(newQuantity);
    }

    /**
     * checkCartStatus checks the current status of the crystal in the user's cart.
     */
    private void checkCartStatus() {
        if (currentUser == null) {
            return;
        }

        // Check if the crystal is already in the user's cart
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

    /**
     * updateCartButtonUI updates the UI of the cart button and quantity control layout based on the quantity.
     * @param quantity
     */
    private void updateCartButtonUI(int quantity) {

      // Update the visibility of the cart button and quantity control layout based on the quantity
        if (quantity == 0) {
            binding.cartButton.setVisibility(View.VISIBLE);
            binding.quantityControlLayout.setVisibility(View.GONE);
        // Set the cart button to be visible and hide the quantity control layout
        } else {
            binding.cartButton.setVisibility(View.GONE);
            binding.quantityControlLayout.setVisibility(View.VISIBLE);
            binding.quantityTextView.setText(String.valueOf(quantity));
        }
    }

    /**
     * setupDotsIndicator creates and sets up the dots indicator for the crystal images.
     * @param count
     */
    private void setupDotsIndicator(int count) {
        binding.dotsLayout.removeAllViews();
        ImageView[] dots = new ImageView[count];

        // Create ImageView for each dot and set the initial state
        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            int dotResId;
            if (i == 0) {
                dotResId = R.drawable.dots_active;
            } else {
                dotResId = R.drawable.dots_inactive;
            }
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, dotResId));

            // Set layout parameters for the dots
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            binding.dotsLayout.addView(dots[i], params);
        }

        // Set the dots array to the binding for later use
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
     * updateDotsIndicator updates the dots indicator based on the current position in the crystal images.
     * @param position
     * @param dots
     */
    private void updateDotsIndicator(int position, ImageView[] dots) {
      // Update the dots based on the current position
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

    /**
     * loadSimilarProducts retrieves similar products based on the current crystal's category.
     */
    private void loadSimilarProducts() {
        if (currentCategory == null) return;

        // Query Firestore for crystals in the same category as the current crystal
        db.collection("crystals")
                .whereEqualTo("category", currentCategory)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    similarCrystals.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        // Skip the current crystal
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
     * setupSimilarProductsAdapter initializes the adapter for displaying similar products.
     */
    private void setupSimilarProductsAdapter() {
        similarProductsAdapter = new CrystalAdapter(
                this,
                similarCrystals,
                favouriteIds,
                false,
                crystal -> {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra("crystalId", crystal.getId());
                    startActivity(intent);
                }
        );
        binding.similarProducts.setAdapter(similarProductsAdapter);
    }


    /**
     * fetchUserFavourites retrieves the user's favourite crystals from Firestore.
     */
    private void fetchUserFavourites() {
        if (currentUser == null) return;

        // Retrieve the user's favourites from Firestore
        db.collection("users").document(currentUser.getUid())
                .get()
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