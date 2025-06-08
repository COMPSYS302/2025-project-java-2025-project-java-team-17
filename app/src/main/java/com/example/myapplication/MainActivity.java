package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.example.myapplication.adapters.CategoryAdapter;
import com.example.myapplication.adapters.CrystalImageAdapter;
import com.example.myapplication.models.Category;
import com.example.myapplication.models.Crystal;
import com.example.myapplication.utils.CrystalSeeder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The main activity of the application, serving as the home screen.
 * It displays top-rated crystals, categories, and a search bar.
 * This activity interacts with Firebase for authentication and data retrieval from Firestore.
 * It extends {@link BaseActivity} to inherit common functionality like bottom navigation.
 */
public class MainActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private ActivityMainBinding binding;

    // Data structures for managing top crystals
    private PriorityQueue<Crystal> pq; // Priority queue to find top 3 crystals by views
    private Crystal[] topCrystals = new Crystal[3]; // Array to store the top 3 Crystal objects
    private List<String> topImages = new ArrayList<>(); // List of image URLs for the top crystals
    private String[] crystalTitles = new String[3]; // Titles of the top crystals
    private double[] crystalPrices = new double[3]; // Prices of the top crystals






    /**
     * Called when the activity is first created.
     * <p>
     * Initializes Firebase services, sets up the bottom navigation, seeds initial crystal data (if needed),
     * initializes UI elements, and calls methods to set up the top crystals display, categories, and search bar.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Setup bottom navigation, highlighting the "home" tab as active
        setupBottomNavigation(R.id.nav_home);
        // Seed initial crystal data to Firestore (typically run once or for development)
        CrystalSeeder.seedCrystals();


        // Configure RecyclerView for horizontal image scrolling
        binding.crystalImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.crystalImages.setHorizontalScrollBarEnabled(false); // Hide the default horizontal scrollbar

        // Setup the different sections of the main screen
        setupTopCrystals(binding.crystalImages);
        setupCategories();
        setupSearchBar();
    }

    /**
     * Called when the activity is becoming visible to the user.
     * Checks the current Firebase user and updates the UI accordingly (e.g., fetch user-specific data).
     */
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser); // Update UI based on the logged-in user
    }

    /**
     * Fetches and sets up the display for the top 3 most viewed crystals.
     * Uses a PriorityQueue to efficiently find the crystals with the highest view counts
     * from the "crystals" collection in Firestore.
     *
     * @param crystalImages RecyclerView to display the images of the top crystals.
     */
    private void setupTopCrystals(RecyclerView crystalImages) {
        // PriorityQueue to sort crystals by views in ascending order (smallest views at the head)
        pq = new PriorityQueue<>((a, b) -> Integer.compare(a.getViews(), b.getViews()));

        db.collection("crystals").get().addOnSuccessListener(query -> {
            for (DocumentSnapshot doc : query.getDocuments()) {
                Crystal crystal = doc.toObject(Crystal.class);
                if (crystal == null) continue; // Skip if conversion fails

                // Maintain a queue of size 3 with the highest viewed crystals
                if (pq.size() < 3) {
                    pq.add(crystal);
                } else if (crystal.getViews() > pq.peek().getViews()) {
                    pq.poll(); // Remove the crystal with the fewest views among the current top 3
                    pq.add(crystal); // Add the new higher-viewed crystal
                }
            }

            // Extract the top 3 crystals from the priority queue
            // Polling gives elements in ascending order, so store them in reverse
            for (int i = 2; i >= 0; i--) {
                Crystal crystal = pq.poll();
                if (crystal == null) continue; // Should not happen if queue had 3 elements
                topCrystals[i] = crystal;
                topImages.add(0, crystal.getImageUrls().get(0)); // Add first image URL
                crystalTitles[i] = crystal.getName();
                crystalPrices[i] = crystal.getPrice();
            }

            // If top images are found, set up the adapter and dot indicators
            if (!topImages.isEmpty()) {
                CrystalImageAdapter adapter = new CrystalImageAdapter(this, topImages, true, position -> {
                    // Handle click on a top crystal image: navigate to DetailActivity
                    if (position < topCrystals.length && topCrystals[position] != null) {
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("crystalId", topCrystals[position].getId());
                        startActivity(intent);
                    }
                });
                crystalImages.setAdapter(adapter);
                // SnapHelper for pager-like scrolling behavior (one item at a time)
                SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(crystalImages);
                // Setup the dot indicators for the image slider
                setupDotsIndicator(topImages.size(), crystalImages);
            }
        }).addOnFailureListener(e -> Log.e("MainActivity", "Error loading top crystals", e));
    }

    /**
     * Sets up the dot indicators for the top crystals image slider.
     * The active dot changes as the user scrolls through the images.
     * Also updates the crystal title and price displayed below the slider.
     *
     * @param count        The number of images (and therefore dots) to display.
     * @param recyclerView The RecyclerView used for the image slider.
     */
    private void setupDotsIndicator(int count, RecyclerView recyclerView) {

        binding.dotsLayout.removeAllViews(); // Clear any existing dots

        ImageView[] dots = new ImageView[count];
        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            // Set the drawable for active or inactive dot
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == 0 ? R.drawable.dots_active : R.drawable.dots_inactive));

            // Define layout parameters for each dot
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0); // Margin between dots
            binding.dotsLayout.addView(dots[i], params);
        }

        // Add a scroll listener to update dots and info based on the visible item
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int pos = layoutManager.findFirstVisibleItemPosition(); // Get current visible item position

                // Update crystal title and price if the position is valid
                if (pos >= 0 && pos < crystalTitles.length && crystalTitles[pos] != null) {
                    binding.crystalTitleBar.setText(crystalTitles[pos]);
                    binding.crystalPriceBar.setText(String.format("%.2f /kg", crystalPrices[pos]));
                }

                // Update the active dot
                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                            i == pos ? R.drawable.dots_active : R.drawable.dots_inactive));
                }
            }
        });

        // Initialize the title and price display with the first crystal's info
        if (crystalTitles.length > 0 && crystalTitles[0] != null) {
            binding.crystalTitleBar.setText(crystalTitles[0]);
            binding.crystalPriceBar.setText(String.format("%.2f /kg", crystalPrices[0]));
        }
    }

    /**
     * Sets up the display of crystal categories.
     * Creates a list of predefined categories and displays them using a RecyclerView
     * with a {@link CategoryAdapter}.
     * Clicking a category navigates to {@link CategoryActivity} with the selected category name.
     */
    private void setupCategories() {
        // Define a list of categories with names and associated drawable resources
        List<Category> categories = Arrays.asList(
                new Category("Calm & Stress Relief", R.drawable.cat_1),
                new Category("Success", R.drawable.cat_2),
                new Category("Meditation", R.drawable.cat_3));


        binding.categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Vertical list layout

        // Adapter for displaying categories
        CategoryAdapter adapter = new CategoryAdapter(this, categories, view -> {
            int position = binding.categoryRecyclerView.getChildAdapterPosition(view); // Get clicked item's position
            String selectedCategory = categories.get(position).getName();
            // Navigate to CategoryActivity with the selected category name
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            intent.putExtra("categoryName", selectedCategory);
            startActivity(intent);
        });

        binding.categoryRecyclerView.setAdapter(adapter);
        // Apply a layout animation for items fading in
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        binding.categoryRecyclerView.setLayoutAnimation(animation);
        binding.categoryRecyclerView.scheduleLayoutAnimation(); // Start the animation
    }

    /**
     * Sets up the search bar functionality.
     * Initializes the SearchView with a hint and configures its query text listener.
     * When a query is submitted, it navigates to {@link SearchResultsActivity}.
     */
    private void setupSearchBar() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search for crystals..."); // Set placeholder text
        searchView.setIconifiedByDefault(false); // Make the search bar expanded by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // If the query is not empty, trim it and start SearchResultsActivity
                if (!query.trim().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra("query", query.trim());
                    startActivity(intent);
                }
                return true; // Indicate that the query has been handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Not handling live search filtering here, only on submission
                return false;
            }
        });
    }

    /**
     * Updates the UI based on the Firebase user's authentication state.
     * Currently, it fetches and logs the username if the user is logged in.
     *
     * @param user The current {@link FirebaseUser}, or null if no user is logged in.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // If user is logged in, fetch their username from Firestore
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(document -> {
                if (document.exists()) {
                    String username = document.getString("username");
                    Log.d("MainActivity", "Username: " + username);
                } else {
                    Log.d("MainActivity", "User document does not exist for UID: " + user.getUid());
                }
            }).addOnFailureListener(e -> Log.e("MainActivity", "Error fetching user data for UI update", e));
        } else {
            // User is not logged in, handle accordingly (e.g., show login prompts, disable features)
            Log.d("MainActivity", "No user logged in for UI update.");
        }
    }
}