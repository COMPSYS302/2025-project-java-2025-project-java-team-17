package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display crystals belonging to a specific category.
 * Users can view crystals, search for specific crystals within the category,
 * and navigate to the detail view of a crystal.
 * Favourite crystals are indicated and fetched for the logged-in user.
 */
public class CategoryActivity extends BaseActivity {

    private static final String TAG = "CategoryActivity"; // Tag for logging

    // Lists to hold crystal data and user's favourite crystal IDs
    private final List<Crystal> crystalList = new ArrayList<>();
    private final List<String> favouriteIds = new ArrayList<>();

    // UI elements
    private CrystalAdapter adapter;
    private RecyclerView crystalGrid;
    private TextView noResultsMessage;
    private String categoryName; // Name of the category being displayed

    /**
     * Called when the activity is first created.
     * Initializes the UI, sets up the search view, and fetches crystal data.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setupBottomNavigation(R.id.nav_home); // Setup bottom navigation from BaseActivity

        // Get the category name passed from the previous activity
        categoryName = getIntent().getStringExtra("categoryName");

        setupUI();
        setupSearchView();

        // Fetch user's favourites and then the crystals for the category
        fetchFavouritesAndCrystals();
    }

    /**
     * Called when the activity will start interacting with the user.
     * Re-fetches favourites and crystals to ensure data is up-to-date,
     * for example, if a user favourites/unfavourites a crystal in the DetailActivity
     * and returns.
     */
    @Override
    protected void onResume() {
        super.onResume();
        fetchFavouritesAndCrystals(); // Re-fetch on return to update favourites
    }

    /**
     * Initializes the UI components of the activity.
     * Sets the category title, back button functionality, and RecyclerView.
     */
    private void setupUI() {
        TextView title = findViewById(R.id.tv_cart_title);
        title.setText(categoryName); // Set the category name as the title

        ImageView ivBtnBack = findViewById(R.id.btn_back);
        ivBtnBack.setOnClickListener(v -> finish()); // Handle back button click

        noResultsMessage = findViewById(R.id.noResultsMessage); // TextView to show when no search results
        crystalGrid = findViewById(R.id.crystalGrid);
        crystalGrid.setLayoutManager(new GridLayoutManager(this, 2)); // Display crystals in a 2-column grid
    }

    /**
     * Sets up the SearchView for filtering crystals.
     * Configures query hints, icon behavior, and query text listeners.
     */
    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search for crystals..."); // Set placeholder text
        searchView.setIconified(false); // Keep the search view expanded by default
        searchView.clearFocus(); // Remove focus initially

        // Expand and request focus when the search view is clicked
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do nothing on submit, filtering happens on text change
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the crystal list as the user types
                filterCrystals(newText);
                return true;
            }
        });
    }

    /**
     * Fetches the current user's favourite crystal IDs from Firestore.
     * After fetching favourites, it proceeds to fetch the crystals for the category.
     * If there is no logged-in user, it directly fetches the crystals.
     */
    private void fetchFavouritesAndCrystals() {
        favouriteIds.clear(); // Clear previous favourite IDs
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // If user is logged in, fetch their favourites
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        List<String> ids = (List<String>) document.get("favourites");
                        if (ids != null) {
                            favouriteIds.addAll(ids);
                        }
                        fetchCrystals(db); // Proceed to fetch crystals
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch favourites", e);
                        fetchCrystals(db); // Still fetch crystals even if favourites fail
                    });
        } else {
            // If no user is logged in, directly fetch crystals
            fetchCrystals(db);
        }
    }

    /**
     * Fetches crystals from Firestore that belong to the current {@link #categoryName}.
     * Updates the {@link #crystalList} and sets up the adapter.
     *
     * @param db The {@link FirebaseFirestore} instance.
     */
    private void fetchCrystals(FirebaseFirestore db) {
        db.collection("crystals")
                .whereEqualTo("category", categoryName) // Filter by category
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    crystalList.clear(); // Clear previous crystal data
                    for (DocumentSnapshot doc : querySnapshot) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) {
                            crystalList.add(crystal);
                        }
                    }
                    noResultsMessage.setVisibility(View.GONE); // Hide no results message
                    setupAdapter(crystalList); // Setup adapter with fetched crystals
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch crystals", e));
    }

    /**
     * Initializes and sets the {@link CrystalAdapter} for the RecyclerView.
     *
     * @param displayList The list of {@link Crystal} objects to display.
     */
    private void setupAdapter(List<Crystal> displayList) {
        adapter = new CrystalAdapter(
                this,
                displayList,
                favouriteIds, // Pass the list of favourite IDs to the adapter
                false, // isFavouritesScreen - false as this is a category screen
                false, // isSearchScreen - false
                crystal -> { // Lambda for item click listener
                    Log.d(TAG, "Launching DetailActivity for crystal: " + crystal.getId());
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra("crystalId", crystal.getId()); // Pass crystal ID to DetailActivity
                    startActivity(intent);
                },
                null // No explicit favourite toggle listener needed here, handled by adapter's default
        );

        crystalGrid.setAdapter(adapter);
        // Apply layout animation for a fade-in effect when items are added
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        crystalGrid.setLayoutAnimation(animation);
        crystalGrid.scheduleLayoutAnimation();
    }

    /**
     * Filters the {@link #crystalList} based on the user's search query.
     * The filter matches against crystal names and tags.
     * Updates the adapter with the filtered list.
     *
     * @param query The search query entered by the user.
     */
    private void filterCrystals(String query) {
        List<Crystal> filtered = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase(); // Convert query to lowercase for case-insensitive search

        for (Crystal crystal : crystalList) {
            // Check if crystal name contains the query
            boolean nameMatch = crystal.getName().toLowerCase().contains(lowerCaseQuery);
            // Check if any of the crystal tags contain the query
            boolean tagMatch = crystal.getTags() != null &&
                    crystal.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(lowerCaseQuery));

            if (nameMatch || tagMatch) {
                filtered.add(crystal);
            }
        }

        setupAdapter(filtered); // Update adapter with filtered results
        // Show or hide the "no results" message based on whether the filtered list is empty
        noResultsMessage.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}