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
 * Activity to display search results for crystals.
 * Users can search for crystals by name or tags.
 * This activity fetches crystal data from Firestore and allows users to view details of a crystal.
 * It inherits from {@link BaseActivity} for common functionalities like bottom navigation.
 */
public class SearchResultsActivity extends BaseActivity {

    private List<Crystal> crystalList = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();
    private CrystalAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private String queryFromIntent;
    private TextView noResultsMessage;


    /**
     * Called when the activity is first created.
     * <p>
     * Initializes the views, retrieves the search query from the intent,
     * fetches user's favourites (if logged in) and then all crystals,
     * and sets up the search listener.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // Initialize the bottom navigation, highlighting the "home" tab.
        setupBottomNavigation(R.id.nav_home);

        initViews();
        // Get the search query passed from the previous activity.
        queryFromIntent = getIntent().getStringExtra("query");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If user is logged in, fetch their favourites first, then all crystals.
            fetchFavouritesThenCrystals(currentUser.getUid());
        } else {
            // If user is not logged in, fetch all crystals directly.
            fetchAllCrystals(queryFromIntent);
        }

        setupSearchListener();
    }

    /**
     * Initializes the views used in this activity.
     * This includes the SearchView, title TextView, RecyclerView, and back button.
     */
    private void initViews() {
        searchView = findViewById(R.id.searchView);
        TextView title = findViewById(R.id.tv_cart_title);
        noResultsMessage = findViewById(R.id.noResultsMessage); // TextView to show when no results are found.

        title.setText("Search Results");

        ImageView ivBtnBack = findViewById(R.id.btn_back);
        ivBtnBack.setOnClickListener(v -> finish()); // Finish activity when back button is pressed.

        recyclerView = findViewById(R.id.searchRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Display items in a 2-column grid.
        searchView.setQueryHint("Search for crystals...");
        searchView.setIconifiedByDefault(false); // Expand the search view by default.

    }

    /**
     * Fetches the list of favourite crystal IDs for the given user from Firestore.
     * After successfully fetching favourites, it proceeds to fetch all crystals.
     *
     * @param userId The ID of the currently logged-in user.
     */
    private void fetchFavouritesThenCrystals(String userId) {
        FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    List<String> favs = (List<String>) userDoc.get("favourites");
                    if (favs != null) {
                        favouriteIds.addAll(favs);
                    }
                    // After fetching favourites, fetch all crystals and apply the initial query.
                    fetchAllCrystals(queryFromIntent);
                })
                .addOnFailureListener(e -> Log.e("SearchActivity", "Error fetching user favourites", e)); // Log error if fetching favourites fails.
    }

    /**
     * Fetches all crystals from the "crystals" collection in Firestore.
     * If a {@code queryToFilter} is provided, it filters the results immediately.
     * Otherwise, it displays all fetched crystals.
     *
     * @param queryToFilter The initial search query to filter crystals by. Can be null or empty.
     */
    private void fetchAllCrystals(String queryToFilter) {
        FirebaseFirestore.getInstance().collection("crystals")
                .get()
                .addOnSuccessListener(snapshot -> {
                    crystalList.clear(); // Clear existing list before adding new data.
                    for (DocumentSnapshot doc : snapshot) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) {
                            crystalList.add(crystal);
                        }
                    }

                    setupAdapter(); // Setup the RecyclerView adapter with the fetched data.
                    // If an initial query was passed, apply it.
                    if (queryToFilter != null && !queryToFilter.isEmpty()) {
                        searchView.setQuery(queryToFilter, false); // Set the query in the SearchView but don't submit.
                        filterCrystals(queryToFilter); // Apply the filter.
                    } else {
                        adapter.notifyDataSetChanged(); // If no initial query, just update the adapter.
                    }
                })
                .addOnFailureListener(e -> Log.e("SearchActivity", "Error fetching crystals", e)); // Log error if fetching crystals fails.
    }

    /**
     * Sets up the RecyclerView adapter ({@link CrystalAdapter}) with the current list of crystals
     * and favourite IDs.
     * It defines the click listener for each crystal item to navigate to {@link DetailActivity}.
     */
    private void setupAdapter() {
        adapter = new CrystalAdapter(this, crystalList, favouriteIds, false, false, crystal -> {
            // When a crystal is clicked, open DetailActivity with the crystal's ID.
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null); // The last parameter is for a favourite button click listener, null here.
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up the listener for the SearchView to filter crystals as the user types.
     */
    private void setupSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Submission is handled by onQueryTextChange, so return false.
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the crystal list based on the new text.
                filterCrystals(newText);
                return true;
            }
        });
    }

    /**
     * Filters the {@link #crystalList} based on the provided query.
     * The filter matches against crystal names and tags (case-insensitive).
     * Updates the RecyclerView adapter with the filtered list.
     * Shows a "no results" message if the filtered list is empty.
     *
     * @param query The search string to filter by.
     */
    private void filterCrystals(String query) {
        List<Crystal> filtered = new ArrayList<>();
        // Iterate through all crystals and add matching ones to the filtered list.
        for (Crystal crystal : crystalList) {
            if (crystal.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (crystal.getTags() != null &&
                            crystal.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))) {
                filtered.add(crystal);
            }
        }

        // Show or hide the "no results" message based on whether the filtered list is empty.
        if (filtered.isEmpty()) {
            noResultsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        // Create a new adapter with the filtered list and update the RecyclerView.
        // It's important to re-create the adapter here if its internal list doesn't update dynamically
        // or to have a method in the adapter to update its list and call notifyDataSetChanged.
        // For simplicity in this example, a new adapter is created.
        adapter = new CrystalAdapter(this, filtered, favouriteIds, false, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null);
        recyclerView.setAdapter(adapter);

        // Apply a layout animation for a smooth appearance of items.
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }
}