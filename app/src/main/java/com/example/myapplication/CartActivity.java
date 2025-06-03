package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
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

public class CartActivity extends BaseActivity {
  private List<CartItem> cartItems;
  private FirebaseAuth mAuth;
  private FirebaseUser currentUser;
  private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d("CartActivity", "onCreate started");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.shopping_cart);

    mAuth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    cartItems = new ArrayList<>();
    currentUser = mAuth.getCurrentUser();

    setupBottomNavigation(R.id.nav_cart);


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

                          CartItem cartItem = new CartItem(crystal, quantity);
                          cartItems.add(cartItem);
                          Log.d("CartActivity", "Added cart item: " + crystal.getName());
                          if (queriesLeft.decrementAndGet() == 0) {
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
      CartAdapter cartAdapter = new CartAdapter(this, cartItems);
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
