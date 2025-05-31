// Updated DetailActivity.java
package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.CrystalImageAdapter;
import com.example.myapplication.models.Crystal;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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

        // Initialize the RecyclerView for images
        RecyclerView crystalImages = findViewById(R.id.crystalImages);
        crystalImages.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        String crystalId = getIntent().getStringExtra("crystalId");
        if (crystalId == null) {
            finish();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("crystals").document(crystalId).update("views", FieldValue.increment(1));
                db.collection("crystals")
                .document(crystalId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Crystal crystal = documentSnapshot.toObject(Crystal.class);
                    if (crystal != null) {

                        List<String> imageUrls = crystal.getImageUrls();
                        // In your onSuccessListener after setting the adapter:
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            CrystalImageAdapter imageAdapter = new CrystalImageAdapter(this, imageUrls);
                            crystalImages.setAdapter(imageAdapter);
                            setupDotsIndicator(imageUrls.size(), crystalImages);
                        }

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

    private void setupDotsIndicator(int count, RecyclerView recyclerView) {
        LinearLayout dotsLayout = findViewById(R.id.dotsLayout);
        dotsLayout.removeAllViews();

        ImageView[] dots = new ImageView[count];
        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this,
                    i == 0 ? R.drawable.dots_active : R.drawable.dots_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(DetailActivity.this,
                            i == firstVisibleItemPosition ? R.drawable.dots_active : R.drawable.dots_inactive));
                }
            }
        });
    }
}