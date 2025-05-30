package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.models.Crystal;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        String crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }

        FirebaseFirestore.getInstance().collection("crystals")
                .document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Crystal crystal = documentSnapshot.toObject(Crystal.class);
                    if (crystal != null) {
                        TextView nameText = findViewById(R.id.crystalName);
                        nameText.setText(crystal.getName());

                        TextView descriptionText = findViewById(R.id.crystalDescription);
                        descriptionText.setText(crystal.getDescription());

                        TextView priceText = findViewById(R.id.crystalPrice);
                        priceText.setText(String.format("%.2f $ / kg", crystal.getPrice()));


                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
