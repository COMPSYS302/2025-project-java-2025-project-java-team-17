// Updated DetailActivity.java
package com.example.myapplication;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class DetailActivity extends BaseActivity {

    private ImageButton wishlistButton;
    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setupBottomNavigation(R.id.nav_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        wishlistButton = findViewById(R.id.wishlistButton);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        wishlistButton.setOnClickListener(v -> {
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String crystalId = getIntent().getStringExtra("crystalId");

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

        // Initialize the RecyclerView for images
        RecyclerView crystalImages = findViewById(R.id.crystalImages);
        crystalImages.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        String crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }
        // Fetch current user's favourite list and update the heart icon
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(snapshot -> {
                List<String> favourites = (List<String>) snapshot.get("favourites");
                if (favourites != null && favourites.contains(crystalId)) {
                    isFavorite = true;
                    updateWishlistButton(); // Sets to filled icon
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load favourites", Toast.LENGTH_SHORT).show();
            });
        }


        db.collection("crystals").document(crystalId).update("views", FieldValue.increment(1));
                db.collection("crystals")
                .document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Crystal crystal = documentSnapshot.toObject(Crystal.class);
                    if (crystal != null) {

                        List<String> imageUrls = crystal.getImageUrls();
                        // In your onSuccessListener after setting the adapter:
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            CrystalImageAdapter imageAdapter = new CrystalImageAdapter(this, imageUrls);
                            crystalImages.setAdapter(imageAdapter);
                            setupDotsIndicator(imageUrls.size(), crystalImages);
                        }

                        TextView nameText = findViewById(R.id.crystalName);
                        nameText.setText(crystal.getName());

                        TextView descriptionText = findViewById(R.id.crystalDescription);
                        descriptionText.setText(crystal.getDescription());

                        TextView priceText = findViewById(R.id.crystalPrice);
                        priceText.setText(String.format("%.2f $ / kg", crystal.getPrice()));
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateWishlistButton() {
        if (isFavorite) {
            wishlistButton.setImageResource(R.drawable.filled_favourite_icon);

        } else {
            wishlistButton.setImageResource(R.drawable.favourite_icon);

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
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
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