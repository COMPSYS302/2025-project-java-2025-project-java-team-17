package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    private ImageView ivBtnBack;
    private TextView tvUsername, tvEmail, tvFavourites, tvTerms, tvPrivacy, tvCart, tvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation(R.id.nav_profile);

        TextView title = findViewById(R.id.tv_cart_title);
        title.setText("Profile");

        // Initialize views
        ivBtnBack     = findViewById(R.id.btn_back);
        tvUsername    = findViewById(R.id.tvUsername);
        tvEmail       = findViewById(R.id.tvEmail);
        tvFavourites  = findViewById(R.id.tvFavourites);
        tvTerms       = findViewById(R.id.tvTerms);
        tvPrivacy     = findViewById(R.id.tvPrivacy);
        tvCart        = findViewById(R.id.tvCart);
        tvLogout      = findViewById(R.id.tvLogout);

        // Back navigation
        ivBtnBack.setOnClickListener(v -> finish());

        // Set up click listeners
        tvFavourites.setOnClickListener(v -> launchActivity(FavouritesActivity.class));
        tvTerms.setOnClickListener(v -> launchActivity(TermsActivity.class));
        tvPrivacy.setOnClickListener(v -> launchActivity(PrivacyPolicyActivity.class));
        tvCart.setOnClickListener(v -> launchActivity(CartActivity.class));
        tvLogout.setOnClickListener(v -> logout());

        // Load and display user info
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        String username = document.getString("username");
                        String email = document.getString("email");

                        tvUsername.setText(username != null ? username : "Username");
                        tvEmail.setText(email != null ? email : "Email");
                    });
        }
    }

    private void launchActivity(Class<?> cls) {
        startActivity(new Intent(ProfileActivity.this, cls));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish();
    }
}
