package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for displaying and managing the user's profile.
 * This activity shows the user's username and email, and provides navigation
 * to other sections like Favourites, Cart, Terms, and Privacy Policy.
 * It also allows the user to log out.
 * Inherits from {@link BaseActivity} to include common bottom navigation.
 */
public class ProfileActivity extends BaseActivity {

    // UI Elements
    private ImageView ivBtnBack;
    private TextView tvUsername, tvEmail, tvFavourites, tvTerms, tvPrivacy, tvCart, tvLogout;

    /**
     * Called when the activity is first created.
     * <p>
     * Initializes the activity, sets up the layout, bottom navigation,
     * UI elements, and click listeners. It also loads and displays
     * the current user's information from Firebase.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Initialize the bottom navigation, highlighting the "profile" tab.
        setupBottomNavigation(R.id.nav_profile);

        // Set the title of the screen.
        TextView title = findViewById(R.id.tv_cart_title);
        title.setText("Profile");

        // Initialize UI views
        ivBtnBack = findViewById(R.id.btn_back);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvFavourites = findViewById(R.id.tvFavourites);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        tvCart = findViewById(R.id.tvCart);
        tvLogout = findViewById(R.id.tvLogout);

        // Set up back navigation
        ivBtnBack.setOnClickListener(v -> finish()); // Finish current activity to go back

        // Set up click listeners for navigation TextViews
        tvFavourites.setOnClickListener(v -> launchActivity(FavouritesActivity.class));
        tvTerms.setOnClickListener(v -> launchActivity(TermsActivity.class));
        tvPrivacy.setOnClickListener(v -> launchActivity(PrivacyPolicyActivity.class));
        tvCart.setOnClickListener(v -> launchActivity(CartActivity.class));
        tvLogout.setOnClickListener(v -> logout()); // Handle logout process

        // Load and display user information if a user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            // Fetch user details (username and email) from Firestore
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String username = document.getString("username");
                            String email = document.getString("email");

                            // Display fetched username or default if null
                            tvUsername.setText(username != null ? username : "Username");
                            // Display fetched email or default if null
                            tvEmail.setText(email != null ? email : "Email");
                        } else {
                            // Document doesn't exist, use default values
                            tvUsername.setText("Username");
                            tvEmail.setText("Email");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to fetch user data, e.g., log error or show default
                        tvUsername.setText("Username");
                        tvEmail.setText("Email");
                        // Log.e("ProfileActivity", "Error fetching user data", e);
                    });
        } else {
            // No user is logged in, display default values
            // This case might not be reached if the activity is protected and requires login
            tvUsername.setText("Username");
            tvEmail.setText("Email");
        }
    }

    /**
     * Launches the specified activity.
     * This is a helper method to simplify starting new activities.
     *
     * @param cls The class of the activity to launch.
     */
    private void launchActivity(Class<?> cls) {
        startActivity(new Intent(ProfileActivity.this, cls));
    }

    /**
     * Handles the user logout process.
     * Signs the user out from Firebase Authentication and navigates
     * them to the {@link LoginActivity}.
     * Clears the activity stack to prevent returning to the profile screen
     * after logging out.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase

        // Create an intent to navigate to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        // Clear the back stack so the user cannot navigate back to ProfileActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish the current ProfileActivity
    }
}