package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalAdapter;
import com.example.myapplication.models.Crystal;
import com.example.myapplication.utils.CrystalSeeder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // ✅ Retrieve category name passed from MainActivity
        String categoryName = getIntent().getStringExtra("categoryName");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName); // dynamic title
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Crystal> crystalList = new ArrayList<>();

        RecyclerView crystalGrid = findViewById(R.id.crystalGrid);
        CrystalAdapter adapter = new CrystalAdapter(this, crystalList, crystal ->{
            Log.d("CategoryActivity", "Launching DetailActivity for crystal: " + crystal.getId());
            Intent intent = new Intent(CategoryActivity.this, DetailActivity.class);
            intent.putExtra("crystalId",crystal.getId());
            startActivity(intent);
        });

        crystalGrid.setLayoutManager(new GridLayoutManager(this, 2));
        crystalGrid.setAdapter(adapter);

        // ✅ Use categoryName in the query
        db.collection("crystals")
                .whereEqualTo("category", categoryName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Crystal crystal = doc.toObject(Crystal.class);
                        crystalList.add(crystal);
                    }
                    adapter.notifyDataSetChanged(); // refresh RecyclerView
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error fetching crystals", e)
                );
    }
}

