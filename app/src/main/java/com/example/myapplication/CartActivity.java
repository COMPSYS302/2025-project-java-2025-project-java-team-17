package com.example.myapplication;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    setupBottomNavigation(R.id.nav_cart);

      RecyclerView recyclerCartItems = findViewById(R.id.recycler_cart_items);
      recyclerCartItems.setLayoutManager(
              new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

      recyclerCartItems.addItemDecoration(new RecyclerView.ItemDecoration() {
          @Override
          public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
              super.getItemOffsets(outRect, view, parent, state);
              if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                  outRect.bottom = 16;
              }
          }
      });


      ivBtnBack.setOnClickListener(
        v -> {
          finish();
        });

    clearCart.setOnClickListener(
        v -> {
          clearCartData();
        });

    Log.d(
        "CartActivity",
        "onCreate completed, currentUser: " + (currentUser != null ? "logged in" : "null"));
  }

  @Override
  protected void onResume() {
    Log.d("CartActivity", "onResume started");
    super.onResume();

    currentUser = mAuth.getCurrentUser();
    Log.d(
        "CartActivity",
        "Current user in onResume: " + (currentUser != null ? "logged in" : "null"));
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
        .addOnSuccessListener(
            aVoid -> {
              loadCartData();
            });
  }

  @Override
  public void onQuantityChanged(CartItem item, int change) {
    if (currentUser == null) return;

    int newQuantity = item.getQuantity() + change;

    db.collection("users")
        .document(currentUser.getUid())
        .collection("cart")
        .document(item.getCrystal().getId())
        .update("quantity", newQuantity)
        .addOnSuccessListener(
            aVoid -> {
              loadCartData();
            });
  }

  private void clearCartData() {
    db.collection("users")
        .document(currentUser.getUid())
        .collection("cart")
        .get()
        .addOnSuccessListener(
            querySnapshot -> {
              AtomicInteger documentsLeft = new AtomicInteger(querySnapshot.getDocuments().size());
              for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                document.getReference().delete();
                if (documentsLeft.decrementAndGet() == 0) {
                  loadCartData();
                }
              }
            });
  }

  private void loadCartData() {
    Log.d("CartActivity", "loadCartData started");

    cartItems.clear();

    if (currentUser == null) return;

    Log.d("CartActivity", "Querying Firestore for user: " + currentUser.getUid());

    db.collection("users")
        .document(currentUser.getUid())
        .collection("cart")
        .get()
        .addOnSuccessListener(
            querySnapshot -> {
              List<DocumentSnapshot> documents = querySnapshot.getDocuments();
              AtomicInteger queriesLeft = new AtomicInteger(documents.size());
              AtomicReference<Double> price = new AtomicReference<>(0.0);
              AtomicInteger totalItems = new AtomicInteger(0);

              if (documents.size() == 0) {
                tvTotalItems.setText("No items in cart");
                tvPrice.setText("NZD 0.00");
                populateItems();
                return;
              }

              for (DocumentSnapshot document : documents) {
                String crystalId = document.getId();

                db.collection("crystals")
                    .document(crystalId)
                    .get()
                    .addOnSuccessListener(
                        documentSnapshot -> {
                          Crystal crystal = documentSnapshot.toObject(Crystal.class);
                          if (crystal == null) {
                            Log.e(
                                "CartActivity",
                                "Crystal is null for document: " + documentSnapshot.getId());
                            if (queriesLeft.decrementAndGet() == 0) {
                              populateItems();
                            }
                            return;
                          }

                          Long quantityLong = document.getLong("quantity");
                          int quantity = (quantityLong != null) ? quantityLong.intValue() : 1;
                          price.updateAndGet(
                              currentPrice -> currentPrice + (crystal.getPrice() * quantity));
                          totalItems.updateAndGet(currentItems -> currentItems + (quantity));
                          CartItem cartItem = new CartItem(crystal, quantity);
                          cartItems.add(cartItem);
                          Log.d("CartActivity", "Added cart item: " + crystal.getName());
                          if (queriesLeft.decrementAndGet() == 0) {
                            tvTotalItems.setText(
                                "Total Items: " + String.valueOf(totalItems.get()));
                            tvPrice.setText("NZD " + String.format("%.2f", price.get()));

                            Log.d("CartActivity", "Populate Items Called ");

                            populateItems();
                          }
                        });
              }
            });
  }

  private void populateItems() {
    Log.d("CartActivity", "Populate Items Entered ");

    try {
      CartAdapter cartAdapter = new CartAdapter(this, cartItems, this);
      Log.d("CartActivity", "CartAdapter created successfully");

      RecyclerView recyclerCartItems = findViewById(R.id.recycler_cart_items);
      if (recyclerCartItems == null) {
        Log.e("CartActivity", "RecyclerView is null!");
        return;
      }

      Log.d("CartActivity", "Setting layout manager");
      recyclerCartItems.setLayoutManager(
          new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

      Log.d("CartActivity", "Setting adapter with " + cartItems.size() + " items");
      recyclerCartItems.setAdapter(cartAdapter);

      Log.d("CartActivity", "Populate Items Exit - Success");

    } catch (Exception e) {
      Log.e("CartActivity", "Error in populateItems", e);
    }


  }
}
