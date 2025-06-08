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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CartActivity extends BaseActivity implements CartAdapter.CartItemClickListener {

    private List<CartItem> cartItems;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ImageView ivBtnBack;
    private ImageButton clearCart;
    private TextView tvTotalItems;
    private TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CartActivity", "onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cartItems = new ArrayList<>();
        currentUser = mAuth.getCurrentUser();

        ivBtnBack = findViewById(R.id.btn_back);
        clearCart = findViewById(R.id.btn_clear_cart);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tvPrice = findViewById(R.id.tv_total_price);

        clearCart.setImageResource(R.drawable.ic_trash);

        setupBottomNavigation(R.id.nav_cart);
        setupRecyclerView();
        setupListeners();

        Log.d("CartActivity", "onCreate completed, currentUser: " + (currentUser != null ? "logged in" : "null"));
    }

    private void setupRecyclerView() {
        RecyclerView recyclerCartItems = findViewById(R.id.recycler_cart_items);
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerCartItems.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 16;
                }
            }
        });
    }

    private void setupListeners() {
        ivBtnBack.setOnClickListener(v -> finish());
        clearCart.setOnClickListener(v -> clearCartData());
    }

    @Override
    protected void onResume() {
        Log.d("CartActivity", "onResume started");
        super.onResume();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadCartData();
        }
    }

    @Override
    public void onDeleteItem(CartItem item, int position) {
        if (currentUser == null) return;
        db.collection("users")
                .document(currentUser.getUid())
                .collection("cart")
                .document(item.getCrystal().getId())
                .delete()
                .addOnSuccessListener(aVoid -> loadCartData());
    }

    @Override
    public void onQuantityChanged(CartItem item, int change) {
        if (currentUser == null) return;

        int newQuantity = item.getQuantity() + change;
        String docId = item.getCrystal().getId();

        if (newQuantity <= 0) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("cart")
                    .document(docId)
                    .delete()
                    .addOnSuccessListener(aVoid -> loadCartData());
        } else {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("cart")
                    .document(docId)
                    .update("quantity", newQuantity)
                    .addOnSuccessListener(aVoid -> loadCartData());
        }
    }

    private void clearCartData() {
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    AtomicInteger remaining = new AtomicInteger(querySnapshot.size());
                    for (DocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                        if (remaining.decrementAndGet() == 0) {
                            loadCartData();
                        }
                    }
                });
    }

    private void loadCartData() {
        Log.d("CartActivity", "loadCartData started");

        cartItems.clear();
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    AtomicInteger pending = new AtomicInteger(docs.size());
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0.0);
                    AtomicInteger totalItems = new AtomicInteger(0);

                    if (docs.isEmpty()) {
                        tvTotalItems.setText("No items in cart");
                        tvPrice.setText("NZD 0.00");
                        populateItems();
                        return;
                    }

                    for (DocumentSnapshot doc : docs) {
                        String crystalId = doc.getId();

                        db.collection("crystals").document(crystalId).get()
                                .addOnSuccessListener(snapshot -> {
                                    Crystal crystal = snapshot.toObject(Crystal.class);
                                    if (crystal != null) {
                                        int quantity = doc.getLong("quantity") != null ? doc.getLong("quantity").intValue() : 1;
                                        totalPrice.updateAndGet(p -> p + crystal.getPrice() * quantity);
                                        totalItems.addAndGet(quantity);
                                        cartItems.add(new CartItem(crystal, quantity));
                                    }

                                    if (pending.decrementAndGet() == 0) {
                                        tvTotalItems.setText("Total Items: " + totalItems.get());
                                        tvPrice.setText("NZD " + String.format("%.2f", totalPrice.get()));
                                        populateItems();
                                    }
                                });
                    }
                });
    }

    private void populateItems() {
        Log.d("CartActivity", "Populate Items Entered");

        try {
            CartAdapter cartAdapter = new CartAdapter(this, cartItems, this);
            RecyclerView recyclerCartItems = findViewById(R.id.recycler_cart_items);

            recyclerCartItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerCartItems.setAdapter(cartAdapter);

            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
            recyclerCartItems.setLayoutAnimation(animation);
            recyclerCartItems.scheduleLayoutAnimation();

        } catch (Exception e) {
            Log.e("CartActivity", "Error in populateItems", e);
        }
    }
}
