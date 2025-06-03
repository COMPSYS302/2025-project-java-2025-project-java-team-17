package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.Crystal;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends BaseActivity {

    private List<Crystal> crystalList = new ArrayList<>();
    private List<String> favouriteIds = new ArrayList<>();
    private CrystalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        setupBottomNavigation(R.id.nav_home);

        RecyclerView recyclerView = findViewById(R.id.searchRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CrystalAdapter(this, crystalList, favouriteIds, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search crystals...");
        searchView.setIconifiedByDefault(false);

        // Get query passed from MainActivity
        String queryFromIntent = getIntent().getStringExtra("query");
        if (queryFromIntent != null && !queryFromIntent.isEmpty()) {
            searchView.setQuery(queryFromIntent, false);  // display the query
        }

        // Set listener for user typing again
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCrystals(newText);
                return true;
            }
        });

        //Fetch and filter AFTER we have data
        fetchAllCrystals(queryFromIntent);
    }


    private void fetchAllCrystals(String queryToFilter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("crystals")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    crystalList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        if (crystal != null) crystalList.add(crystal);
                    }

                    // Apply initial query filter
                    if (queryToFilter != null && !queryToFilter.isEmpty()) {
                        filterCrystals(queryToFilter);
                    } else {
                        adapter.notifyDataSetChanged(); // show full list
                    }
                })
                .addOnFailureListener(e -> Log.e("SearchActivity", "Error fetching crystals", e));
    }

    private void filterCrystals(String query) {
        List<Crystal> filtered = new ArrayList<>();
        for (Crystal crystal : crystalList) {
            if (crystal.getName().toLowerCase().contains(query.toLowerCase()) ||
                    (crystal.getTags() != null && crystal.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))) {
                filtered.add(crystal);
            }
        }
        adapter = new CrystalAdapter(this, filtered, favouriteIds, false, crystal -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("crystalId", crystal.getId());
            startActivity(intent);
        });
        RecyclerView recyclerView = findViewById(R.id.searchRecycler);
        recyclerView.setAdapter(adapter);
    }
}
