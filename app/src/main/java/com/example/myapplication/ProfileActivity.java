package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation(R.id.nav_profile);

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvFavourites = findViewById(R.id.tvFavourites);
        tvFavourites.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavouritesActivity.class);
            startActivity(intent);
        });

        TextView tvTerms = findViewById(R.id.tvTerms);
        tvTerms.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TermsActivity.class);
            startActivity(intent);
        });

        TextView tvPrivacy = findViewById(R.id.tvPrivacy);
        tvPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });


        TextView tvCart = findViewById(R.id.tvCart);
        tvCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            startActivity(intent);
        });

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

        TextView tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Sign out the user

            // Go back to login screen
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears back stack
            startActivity(intent);
            finish();  // Finish profile activity
        });

    }
}
