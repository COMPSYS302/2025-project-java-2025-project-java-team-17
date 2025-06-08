package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.databinding.ActivityProfileBinding;
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

    private ActivityProfileBinding binding;

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
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the bottom navigation, highlighting the "profile" tab.
        setupBottomNavigation(R.id.nav_profile);

        // Set the title of the screen.
        binding.includeTopBar.tvCartTitle.setText("Profile");
        // Set up back navigation
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());

        // Set up click listeners for navigation TextViews
        binding.tvFavourites.setOnClickListener(v -> launchActivity(FavouritesActivity.class));
        binding.tvTerms.setOnClickListener(v -> launchActivity(TermsActivity.class));
        binding.tvPrivacy.setOnClickListener(v -> launchActivity(PrivacyPolicyActivity.class));
        binding.tvCart.setOnClickListener(v -> launchActivity(CartActivity.class));
        binding.tvLogout.setOnClickListener(v -> logout()); // Handle logout process

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
                            binding.tvUsername.setText(username != null ? username : "Username");
                            // Display fetched email or default if null
                            binding.tvEmail.setText(email != null ? email : "Email");
                        } else {
                            // Document doesn't exist, use default values
                            binding.tvUsername.setText("Username");
                            binding.tvEmail.setText("Email");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure to fetch user data, e.g., log error or show default
                        binding.tvUsername.setText("Username");
                        binding.tvEmail.setText("Email");
                    });
        } else {
            // No user is logged in, display default values
            binding.tvUsername.setText("Username");
            binding.tvEmail.setText("Email");
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
