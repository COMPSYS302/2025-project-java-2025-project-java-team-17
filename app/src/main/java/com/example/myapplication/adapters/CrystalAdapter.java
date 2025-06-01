package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CrystalAdapter extends RecyclerView.Adapter<CrystalAdapter.ViewHolder> {

    private Context context;
    private List<Crystal> crystalList;
    private List<String> userFavourites;

    private boolean isFavouritesView;

    public CrystalAdapter(Context context, List<Crystal> crystalList, List<String> userFavourites, boolean isFavouritesView) {
        this.context = context;
        this.crystalList = crystalList;
        this.userFavourites = userFavourites;
        this.isFavouritesView = isFavouritesView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView crystalImage;
        TextView crystalName;

        TextView crystalPrice;

        ImageView wishlistIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            crystalImage = itemView.findViewById(R.id.crystalImage);
            crystalName = itemView.findViewById(R.id.crystalName);
            crystalPrice = itemView.findViewById(R.id.crystalPrice);
            wishlistIcon = itemView.findViewById(R.id.wishlistIcon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                isFavouritesView ? R.layout.item_favourite_crystal : R.layout.crystal_item,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Crystal crystal = crystalList.get(position);

        holder.crystalName.setText(crystal.getName());
        holder.crystalPrice.setText("NZD " + (int) crystal.getPrice());
        Glide.with(context)
                .load(crystal.getImageUrls().get(0))
                .placeholder(R.drawable.crystal)
                .into(holder.crystalImage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (isFavouritesView) {
            holder.wishlistIcon.setImageResource(R.drawable.close_button);
            holder.wishlistIcon.setOnClickListener(v -> {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    removeFromFavourites(userId, crystal.getId());
                    userFavourites.remove(crystal.getId());
                    crystalList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            boolean isFavourited = userFavourites != null && userFavourites.contains(crystal.getId());
            holder.wishlistIcon.setImageResource(
                    isFavourited ? R.drawable.favourite_filled : R.drawable.favourite_icon
            );

            holder.wishlistIcon.setOnClickListener(v -> {
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    boolean isNowFavourited = userFavourites.contains(crystal.getId());

                    if (isNowFavourited) {
                        removeFromFavourites(userId, crystal.getId());
                        userFavourites.remove(crystal.getId());
                        holder.wishlistIcon.setImageResource(R.drawable.favourite_icon);
                        Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                    } else {
                        addToFavourites(userId, crystal.getId());
                        userFavourites.add(crystal.getId());
                        holder.wishlistIcon.setImageResource(R.drawable.favourite_filled);
                        Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                    }

                    notifyItemChanged(holder.getAdapterPosition());
                } else {
                    Toast.makeText(context, "Please log in to use favourites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void addToFavourites(String userId, String crystalId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("favourites", FieldValue.arrayUnion(crystalId))
                .addOnSuccessListener(aVoid ->
                        Log.d("FAV", "Added to favourites: " + crystalId))
                .addOnFailureListener(e ->
                        Log.e("FAV", "Error adding to favourites", e));
    }

    private void removeFromFavourites(String userId, String crystalId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("favourites", FieldValue.arrayRemove(crystalId))
                .addOnSuccessListener(aVoid ->
                        Log.d("FAV", "Removed from favourites: " + crystalId))
                .addOnFailureListener(e ->
                        Log.e("FAV", "Error removing from favourites", e));
    }

    @Override
    public int getItemCount() {
        return crystalList.size();
    }
}
