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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CartAdapter;
import com.example.myapplication.models.CartItem;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.databinding.ShoppingCartBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Activity to display and manage the user's shopping cart.
 * Users can view items added to their cart, update quantities, remove items,
 * and clear the entire cart. The total number of items and the total price
 * are calculated and displayed.
 * This activity interacts with Firebase Firestore to persist and retrieve cart data.
 * It extends {@link BaseActivity} to inherit common functionality like bottom navigation.
 * It implements {@link CartAdapter.CartItemClickListener} to handle actions on cart items.
 */
public class CartActivity extends BaseActivity implements CartAdapter.CartItemClickListener {

    private static final String TAG = "CartActivity"; // Tag for logging

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    // UI Elements
    private ShoppingCartBinding binding;

    // Data for RecyclerView
    private List<CartItem> cartItems; // List to hold cart item objects
    private CartAdapter cartAdapter; // Adapter for the cart items RecyclerView

    /**
     * Called when the activity is first created.
     * <p>
     * Initializes Firebase services, UI elements, sets up the bottom navigation,
     * RecyclerView, and listeners for UI interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        binding = ShoppingCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the layout for the cart screen

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser(); // Get the currently logged-in user

        // Initialize the list for cart items
        cartItems = new ArrayList<>();


        // Setup bottom navigation, highlighting the "cart" tab
        setupBottomNavigation(R.id.nav_cart);
        // Setup the RecyclerView for displaying cart items
        setupRecyclerView();
        // Setup listeners for various UI interactions (back button, clear cart)
        setupListeners();

        Log.d(TAG, "onCreate completed, currentUser: " + (currentUser != null ? currentUser.getUid() : "null"));
    }

    /**
     * Sets up the RecyclerView for displaying cart items.
     * Configures the LayoutManager and adds item decoration for spacing.
     */
    private void setupRecyclerView() {
        binding.recyclerCartItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // Add item decoration for spacing between cart items
        binding.recyclerCartItems.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                // Add bottom margin to all items except the last one
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 16; // 16 pixels bottom margin
                }
            }
        });
        // Initialize the CartAdapter
        cartAdapter = new CartAdapter(this, cartItems, this);
        binding.recyclerCartItems.setAdapter(cartAdapter);

        // Apply layout animation for items appearing in the RecyclerView
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        binding.recyclerCartItems.setLayoutAnimation(animation);
    }

    /**
     * Sets up click listeners for UI elements like the back button and clear cart button.
     */
    private void setupListeners() {
        // Set listener for the back button to finish the activity
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());
        // Set listener for the clear cart button to call clearCartData method
        binding.includeTopBar.btnClearCart.setOnClickListener(v -> clearCartData());
    }

    /**
     * Called when the activity will start interacting with the user.
     * Reloads the cart data if a user is logged in. This ensures the cart is
     * up-to-date when the activity becomes visible.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume started");
        super.onResume();
        currentUser = mAuth.getCurrentUser(); // Refresh current user status
        if (currentUser != null) {
            loadCartData(); // Load cart data for the logged-in user
        } else {
            // If no user is logged in, clear the display and show appropriate messages
            cartItems.clear();
            if (cartAdapter != null) {
                cartAdapter.notifyDataSetChanged();
            }
            binding.tvTotalItems.setText("No items in cart");
            binding.tvTotalPrice.setText("NZD 0.00");
            // Optionally, prompt user to log in or disable cart functionality
            Log.w(TAG, "No user logged in. Cart cannot be loaded.");
        }
    }

    /**
     * Callback method from {@link CartAdapter.CartItemClickListener}.
     * Handles the deletion of a specific item from the cart in Firestore.
     * After successful deletion, it reloads the cart data to update the UI.
     *
     * @param item     The {@link CartItem} to be deleted.
     * @param position The position of the item in the adapter (not directly used here but available).
     */
    @Override
    public void onDeleteItem(CartItem item, int position) {
        if (currentUser == null) {
            Log.w(TAG, "Cannot delete item: User not logged in.");
            return;
        }
        if (item == null || item.getCrystal() == null || item.getCrystal().getId() == null) {
            Log.e(TAG, "Cannot delete item: CartItem or Crystal data is invalid.");
            return;
        }

        Log.d(TAG, "Deleting item: " + item.getCrystal().getId() + " from cart for user: " + currentUser.getUid());
        db.collection("users")
                .document(currentUser.getUid())
                .collection("cart")
                .document(item.getCrystal().getId()) // Use crystal ID as document ID in cart
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Item " + item.getCrystal().getId() + " deleted successfully.");
                    loadCartData(); // Reload cart data to reflect changes
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting item " + item.getCrystal().getId(), e));
    }

    /**
     * Callback method from {@link CartAdapter.CartItemClickListener}.
     * Handles changing the quantity of a specific item in the cart.
     * If the new quantity is zero or less, the item is deleted. Otherwise, the quantity is updated in Firestore.
     * After the operation, it reloads the cart data.
     *
     * @param item   The {@link CartItem} whose quantity is to be changed.
     * @param change The change in quantity (e.g., +1 for increase, -1 for decrease).
     */
    @Override
    public void onQuantityChanged(CartItem item, int change) {
        if (currentUser == null) {
            Log.w(TAG, "Cannot change quantity: User not logged in.");
            return;
        }
        if (item == null || item.getCrystal() == null || item.getCrystal().getId() == null) {
            Log.e(TAG, "Cannot change quantity: CartItem or Crystal data is invalid.");
            return;
        }

        int newQuantity = item.getQuantity() + change;
        String docId = item.getCrystal().getId(); // Document ID in cart is the crystal's ID

        Log.d(TAG, "Changing quantity for item: " + docId + " to new quantity: " + newQuantity);

        if (newQuantity <= 0) {
            // If new quantity is 0 or less, delete the item from the cart
            Log.d(TAG, "New quantity is " + newQuantity + ", deleting item: " + docId);
            onDeleteItem(item, -1); // Reuse onDeleteItem logic (position not critical here)
        } else {
            // Update the quantity of the item in Firestore
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("cart")
                    .document(docId)
                    .update("quantity", newQuantity)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Quantity for item " + docId + " updated to " + newQuantity + ".");
                        loadCartData(); // Reload data to reflect changes
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating quantity for item " + docId, e));
        }
    }

    /**
     * Clears all items from the current user's cart in Firestore.
     * It iterates through all documents in the user's cart subcollection and deletes them.
     * After all items are deleted, it reloads the cart data.
     */
    private void clearCartData() {
        if (currentUser == null) {
            Log.w(TAG, "Cannot clear cart: User not logged in.");
            // Optionally, show a message to the user
            return;
        }

        Log.d(TAG, "Clearing all cart data for user: " + currentUser.getUid());
        db.collection("users")
                .document(currentUser.getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Log.d(TAG, "Cart is already empty. No items to clear.");
                        loadCartData(); // Still call loadCartData to update UI (e.g., "No items" message)
                        return;
                    }
                    // Use AtomicInteger for safe concurrent operations if batching or parallel deletes were used.
                    // Here, it's mainly for tracking completion of sequential deletes.
                    AtomicInteger remainingDeletes = new AtomicInteger(querySnapshot.size());
                    for (DocumentSnapshot doc : querySnapshot) {
                        Log.d(TAG, "Deleting document: " + doc.getId() + " from cart.");
                        doc.getReference().delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully deleted document: " + doc.getId());
                            } else {
                                Log.e(TAG, "Failed to delete document: " + doc.getId(), task.getException());
                            }
                            // When all delete operations are complete (successful or not), reload cart data
                            if (remainingDeletes.decrementAndGet() == 0) {
                                Log.d(TAG, "All cart item deletion attempts finished.");
                                loadCartData();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching cart items for clearing.", e);
                    // Even on failure to fetch, try to update UI, perhaps it's an intermittent issue
                    loadCartData();
                });
    }

    /**
     * Loads cart data for the current user from Firestore.
     * It first fetches the document IDs and quantities from the user's "cart" subcollection.
     * Then, for each cart item, it fetches the corresponding crystal details from the "crystals" collection.
     * Calculates total items and total price, and updates the UI.
     * Finally, it calls {@link #populateItems()} to refresh the RecyclerView.
     */
    private void loadCartData() {
        Log.d(TAG, "loadCartData started for user: " + (currentUser != null ? currentUser.getUid() : "null"));
        cartItems.clear(); // Clear the existing list before loading new data

        if (currentUser == null) {
            Log.w(TAG, "User not logged in, cannot load cart data.");
            // Update UI to reflect empty cart
            binding.tvTotalItems.setText("No items in cart");
            binding.tvTotalPrice.setText("NZD 0.00");
            if (cartAdapter != null) {
                cartAdapter.notifyDataSetChanged(); // Ensure RecyclerView is also cleared/updated
            }
            return;
        }

        // Path to the user's cart subcollection
        String userCartPath = "users/" + currentUser.getUid() + "/cart";
        Log.d(TAG, "Fetching cart items from path: " + userCartPath);

        db.collection(userCartPath)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DocumentSnapshot> cartItemDocs = querySnapshot.getDocuments();
                    Log.d(TAG, "Fetched " + cartItemDocs.size() + " documents from user's cart subcollection.");

                    // Atomic variables for thread-safe accumulation from multiple async calls
                    AtomicInteger pendingCrystalFetches = new AtomicInteger(cartItemDocs.size());
                    AtomicReference<Double> accumulatedTotalPrice = new AtomicReference<>(0.0);
                    AtomicInteger accumulatedTotalItems = new AtomicInteger(0);

                    if (cartItemDocs.isEmpty()) {
                        // If the cart is empty, update UI and return
                        Log.d(TAG, "User's cart is empty.");
                        binding.tvTotalItems.setText("No items in cart");
                        binding.tvTotalPrice.setText("NZD 0.00");
                        populateItems(); // Refresh RecyclerView (will be empty)
                        return;
                    }

                    // For each document in the cart (representing a crystal and its quantity)
                    for (DocumentSnapshot cartDoc : cartItemDocs) {
                        String crystalId = cartDoc.getId(); // The document ID is the crystal's ID
                        Long quantityLong = cartDoc.getLong("quantity");
                        int quantity = (quantityLong != null) ? quantityLong.intValue() : 0;

                        if (quantity <= 0) {
                            // If quantity is invalid, skip this item or consider deleting it
                            Log.w(TAG, "Skipping cart item with invalid quantity: " + crystalId + ", quantity: " + quantity);
                            if (pendingCrystalFetches.decrementAndGet() == 0) {
                                // If this was the last item and it was invalid, finalize totals
                                binding.tvTotalItems.setText("Total Items: " + accumulatedTotalItems.get());
                                binding.tvTotalPrice.setText("NZD " + String.format("%.2f", accumulatedTotalPrice.get()));
                                populateItems();
                            }
                            continue; // Skip fetching crystal details for this item
                        }

                        Log.d(TAG, "Fetching details for crystal ID: " + crystalId + " with quantity: " + quantity);
                        // Fetch the details of the crystal from the "crystals" collection
                        db.collection("crystals").document(crystalId).get()
                                .addOnSuccessListener(crystalSnapshot -> {
                                    Crystal crystal = crystalSnapshot.toObject(Crystal.class);
                                    if (crystal != null) {
                                        // Successfully fetched crystal details
                                        Log.d(TAG, "Successfully fetched details for crystal: " + crystal.getName());
                                        // Update accumulated totals
                                        accumulatedTotalPrice.updateAndGet(currentTotal -> currentTotal + (crystal.getPrice() * quantity));
                                        accumulatedTotalItems.addAndGet(quantity);
                                        // Add the fully formed CartItem to the list
                                        cartItems.add(new CartItem(crystal, quantity));
                                    } else {
                                        // Crystal details not found (e.g., item deleted from main collection)
                                        Log.w(TAG, "Crystal details not found for ID: " + crystalId + ". This item might be outdated or removed.");
                                        // Consider how to handle this case: remove from cart, show error, etc.
                                    }

                                    // Check if all crystal details have been fetched
                                    if (pendingCrystalFetches.decrementAndGet() == 0) {
                                        Log.d(TAG, "All crystal details fetched. Finalizing totals.");
                                        // All asynchronous fetches are complete, update the UI totals
                                        binding.tvTotalItems.setText("Total Items: " + accumulatedTotalItems.get());
                                        binding.tvTotalItems.setText("NZD " + String.format("%.2f", accumulatedTotalPrice.get()));
                                        populateItems(); // Refresh the RecyclerView with all loaded items
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error fetching details for crystal ID: " + crystalId, e);
                                    // Decrement pending count even on failure to avoid deadlock
                                    if (pendingCrystalFetches.decrementAndGet() == 0) {
                                        Log.d(TAG, "All crystal details fetched (some with errors). Finalizing totals.");
                                        binding.tvTotalItems.setText("Total Items: " + accumulatedTotalItems.get());
                                        binding.tvTotalItems.setText("NZD " + String.format("%.2f", accumulatedTotalPrice.get()));
                                        populateItems();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching cart items from " + userCartPath, e);
                    // Handle failure to load cart (e.g., show error message)
                    binding.tvTotalItems.setText("Error loading cart");
                    binding.tvTotalPrice.setText("NZD ---");
                    cartItems.clear(); // Ensure list is empty on error
                    populateItems(); // Refresh RecyclerView
                });
    }

    /**
     * Populates the RecyclerView with the current list of {@link #cartItems}.
     * It ensures the adapter is set and refreshes its data.
     * Also, schedules a layout animation for the RecyclerView.
     */
    private void populateItems() {
        Log.d(TAG, "populateItems started. Number of items to populate: " + cartItems.size());

        try {
            // Ensure adapter is initialized (should be by setupRecyclerView)
            if (cartAdapter == null) {
                Log.w(TAG, "CartAdapter was null in populateItems. Re-initializing.");
                // This is a fallback, ideally adapter is set up in onCreate/setupRecyclerView
                cartAdapter = new CartAdapter(this, cartItems, this);
                binding.recyclerCartItems.setAdapter(cartAdapter);
            } else {
                // If adapter exists, notify it that the underlying data set has changed.
                // The CartAdapter should be designed to handle updates to its list.
                // If it takes a new list directly, you might need to re-create or call a specific update method.
                // For this structure, assuming notifyDataSetChanged is sufficient after `cartItems` list is modified.
                cartAdapter.notifyDataSetChanged();
            }

            // Schedule the layout animation to run for the updated items
            if (binding.recyclerCartItems.getLayoutAnimation() == null) {
                // If animation controller was not set or got removed, re-apply
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
                binding.recyclerCartItems.setLayoutAnimation(animation);
            }
            binding.recyclerCartItems.scheduleLayoutAnimation();
            Log.d(TAG, "RecyclerView populated and animation scheduled.");

        } catch (Exception e) {
            // Catch any unexpected errors during UI update
            Log.e(TAG, "Error in populateItems", e);
        }
    }
}