package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CrystalAdapter extends RecyclerView.Adapter<CrystalAdapter.ViewHolder> {

    private final Context context;
    private final List<Crystal> crystalList;
    private final List<String> userFavourites;
    private final boolean isFavouritesView;
    private final onClickListener listener;
    private final OnCartClickListener cartClickListener;

    public interface onClickListener {
        void onCrystalClick(Crystal crystal);
    }

    public interface OnCartClickListener {
        void onAddToCartClicked(Crystal crystal);
    }

    public CrystalAdapter(Context context, List<Crystal> crystalList, List<String> userFavourites,
                          boolean isFavouritesView,
                          onClickListener listener,
                          OnCartClickListener cartClickListener) {
        this.context = context;
        this.crystalList = crystalList;
        this.userFavourites = userFavourites;
        this.isFavouritesView = isFavouritesView;
        this.listener = listener;
        this.cartClickListener = cartClickListener;
        setHasStableIds(true);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView crystalImage, wishlistIcon, addToCart;
        TextView crystalName, crystalPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            crystalImage = itemView.findViewById(R.id.crystalImage);
            crystalName = itemView.findViewById(R.id.crystalName);
            crystalPrice = itemView.findViewById(R.id.crystalPrice);
            wishlistIcon = itemView.findViewById(R.id.wishlistIcon);
            addToCart = itemView.findViewById(R.id.addToCart);
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

        Glide.with(context).load(crystal.getImageUrls()).apply(new RequestOptions()).fitCenter()
                        .into(holder.crystalImage);

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
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        String userId = currentUser.getUid();
                        removeFromFavourites(userId, crystal.getId());
                        userFavourites.remove(crystal.getId());
                        crystalList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                        holder.wishlistIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_pulse));
                    }
                }
            });

            if (holder.addToCart != null) {
                holder.addToCart.setVisibility(View.VISIBLE);
                holder.addToCart.setOnClickListener(v -> {
                    if (cartClickListener != null) {
                        cartClickListener.onAddToCartClicked(crystal);
                        holder.addToCart.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop));
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            boolean isFavourited = userFavourites != null && userFavourites.contains(crystal.getId());
            holder.wishlistIcon.setImageResource(isFavourited ? R.drawable.purple_heart : R.drawable.heart_outline);

            holder.wishlistIcon.setOnClickListener(v -> {
                if (currentUser != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        String userId = currentUser.getUid();
                        boolean isNowFavourited = userFavourites.contains(crystal.getId());

                        if (isNowFavourited) {
                            removeFromFavourites(userId, crystal.getId());
                            userFavourites.remove(crystal.getId());
                            holder.wishlistIcon.setImageResource(R.drawable.heart_outline);
                            Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                            holder.wishlistIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_pulse));
                        } else {
                            addToFavourites(userId, crystal.getId());
                            userFavourites.add(crystal.getId());
                            holder.wishlistIcon.setImageResource(R.drawable.purple_heart);
                            Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                            holder.wishlistIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop));
                        }

                        notifyItemChanged(adapterPosition);
                    }
                } else {
                    Toast.makeText(context, "Please log in to use favourites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCrystalClick(crystal);
            }
        });
    }

    private void addToFavourites(String userId, String crystalId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("favourites", FieldValue.arrayUnion(crystalId))
                .addOnSuccessListener(aVoid -> Log.d("FAV", "Added to favourites: " + crystalId))
                .addOnFailureListener(e -> Log.e("FAV", "Error adding to favourites", e));
    }

    private void removeFromFavourites(String userId, String crystalId) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("favourites", FieldValue.arrayRemove(crystalId))
                .addOnSuccessListener(aVoid -> Log.d("FAV", "Removed from favourites: " + crystalId))
                .addOnFailureListener(e -> Log.e("FAV", "Error removing from favourites", e));
    }

    @Override
    public int getItemCount() {
        return crystalList.size();
    }

    @Override
    public long getItemId(int position) {
        return crystalList.get(position).getId().hashCode();
    }
}
