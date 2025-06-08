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

public class SearchResultsActivity extends BaseActivity {

    private List<Crystal> crystalList = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();
    private CrystalAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private String queryFromIntent;
    private TextView noResultsMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
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

    private void initViews() {
        searchView = findViewById(R.id.searchView);
        TextView title = findViewById(R.id.tv_cart_title);
        noResultsMessage = findViewById(R.id.noResultsMessage);

        title.setText("Search Results");

        ImageView ivBtnBack = findViewById(R.id.btn_back);
        ivBtnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.searchRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        searchView.setQueryHint("Search for crystals...");
        searchView.setIconifiedByDefault(false);

    }

    private void fetchFavouritesThenCrystals(String userId) {
        FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    List<String> favs = (List<String>) userDoc.get("favourites");
                    if (favs != null) favouriteIds.addAll(favs);
                    fetchAllCrystals(queryFromIntent);
                });
    }

    private void fetchAllCrystals(String queryToFilter) {
        FirebaseFirestore.getInstance().collection("crystals")
                .get()
                .addOnSuccessListener(snapshot -> {
                    crystalList.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) crystalList.add(crystal);
                    }

                    setupAdapter();
                    if (queryToFilter != null && !queryToFilter.isEmpty()) {
                        searchView.setQuery(queryToFilter, false);
                        filterCrystals(queryToFilter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("SearchActivity", "Error fetching crystals", e));
    }

    private void setupAdapter() {
        adapter = new CrystalAdapter(this, crystalList, favouriteIds, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override public boolean onQueryTextChange(String newText) {
                filterCrystals(newText);
                return true;
            }
        });
    }

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
            noResultsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noResultsMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new CrystalAdapter(this, filtered, favouriteIds, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        }, null);
        recyclerView.setAdapter(adapter);

        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fade_in);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

}
