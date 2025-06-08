package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.databinding.ActivitySearchResultsBinding;
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
    private String queryFromIntent;

    private ActivitySearchResultsBinding binding;

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
        binding = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the bottom navigation, highlighting the "home" tab.
        setupBottomNavigation(R.id.nav_home);

        initViews();
        queryFromIntent = getIntent().getStringExtra("query");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            fetchFavouritesThenCrystals(currentUser.getUid());
        } else {
            fetchAllCrystals(queryFromIntent);
        }

        setupSearchListener();
    }

    /**
     * Initializes the views used in this activity.
     * This includes the SearchView, title TextView, RecyclerView, and back button.
     */
    private void initViews() {
        binding.includeTopBar.tvCartTitle.setText("Search Results");
        binding.includeTopBar.btnBack.setOnClickListener(v -> finish());

        binding.searchRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        binding.searchView.setQueryHint("Search for crystals...");
        binding.searchView.setIconifiedByDefault(false);
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
                    fetchAllCrystals(queryFromIntent);
                })
                .addOnFailureListener(e -> Log.e("SearchActivity", "Error fetching user favourites", e));
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
                    crystalList.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) {
                            crystalList.add(crystal);
                        }
                    }

                    setupAdapter();

                    if (queryToFilter != null && !queryToFilter.isEmpty()) {
                        binding.searchView.setQuery(queryToFilter, false);
                        filterCrystals(queryToFilter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("SearchActivity", "Error fetching crystals", e));
    }

    /**
     * Sets up the RecyclerView adapter ({@link CrystalAdapter}) with the current list of crystals
     * and favourite IDs.
     * It defines the click listener for each crystal item to navigate to {@link DetailActivity}.
     */
    private void setupAdapter() {
        adapter = new CrystalAdapter(this, crystalList, favouriteIds, false, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null);
        binding.searchRecycler.setAdapter(adapter);
    }

    /**
     * Sets up the listener for the SearchView to filter crystals as the user types.
     */
    private void setupSearchListener() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
        for (Crystal crystal : crystalList) {
            if (crystal.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (crystal.getTags() != null &&
                            crystal.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))) {
                filtered.add(crystal);
            }
        }

        if (filtered.isEmpty()) {
            binding.noResultsMessage.setVisibility(View.VISIBLE);
            binding.searchRecycler.setVisibility(View.GONE);
        } else {
            binding.noResultsMessage.setVisibility(View.GONE);
            binding.searchRecycler.setVisibility(View.VISIBLE);
        }

        adapter = new CrystalAdapter(this, filtered, favouriteIds, false, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null);
        binding.searchRecycler.setAdapter(adapter);

        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        binding.searchRecycler.setLayoutAnimation(controller);
        binding.searchRecycler.scheduleLayoutAnimation();
    }
}
