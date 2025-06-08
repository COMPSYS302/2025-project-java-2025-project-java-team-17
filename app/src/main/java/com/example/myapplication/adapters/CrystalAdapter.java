package com.example.myapplication.adapters;

import android.content.Context;
import android.content.res.Resources;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.models.Crystal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter for displaying a list of {@link Crystal} objects in a RecyclerView.
 * This adapter is versatile and can be used in different contexts, such as:
 * - A general list of crystals where users can view details and add to wishlist/cart.
 * - A user's "Favourites" screen where they can manage their wishlisted items.
 * - A horizontal grid display, potentially for a "Similar Products" section.
 *
 * It handles interactions like clicking on a crystal to view its details,
 * adding/removing crystals from favourites (wishlist), and adding crystals to the cart.
 * Interactions with Firebase Firestore are used for managing favourites.
 */
public class CrystalAdapter extends RecyclerView.Adapter<CrystalAdapter.ViewHolder> {

    // Context from which the adapter is instantiated. Used for various Android operations
    // like layout inflation, accessing resources, showing Toasts, and Glide.
    private final Context context;
    // The list of Crystal objects to be displayed by the RecyclerView.
    private final List<Crystal> crystalList;
    // A list of crystal IDs that the current user has marked as favourites.
    // This is used to determine the initial state of the wishlist icon for each crystal.
    private final List<String> userFavourites;
    // Flag to indicate if the adapter is being used in a "Favourites" view context.
    // This affects the appearance and behavior of the wishlist icon (e.g., changes to a "remove" icon).
    private final boolean isFavouritesView;
    // Flag to indicate if the RecyclerView items should be sized for a horizontal grid
    // (e.g., each item taking half the screen width).
    private final boolean isHorizontalGrid;
    // Listener for handling clicks on individual crystal items to view their details.
    private final onClickListener listener;
    // Listener for handling clicks on the "Add to Cart" button.
    private final OnCartClickListener cartClickListener;

    /**
     * Interface definition for a callback to be invoked when a crystal item is clicked.
     */
    public interface onClickListener {
        /**
         * Called when a crystal item view has been clicked.
         *
         * @param crystal The {@link Crystal} object that was clicked.
         */
        void onCrystalClick(Crystal crystal);
    }

    /**
     * Interface definition for a callback to be invoked when the "Add to Cart"
     * action is triggered for a crystal.
     */
    public interface OnCartClickListener {
        /**
         * Called when the "Add to Cart" button for a crystal is clicked.
         *
         * @param crystal The {@link Crystal} object to be added to the cart.
         */
        void onAddToCartClicked(Crystal crystal);
    }

    /**
     * Constructs a new {@code CrystalAdapter}.
     *
     * @param context            The current context.
     * @param crystalList        The list of {@link Crystal} objects to display.
     * @param userFavourites     A list of IDs of crystals favourited by the user. Can be null.
     * @param isFavouritesView   True if this adapter is for a "Favourites" screen, false otherwise.
     * @param isHorizontalGrid   True if items should be sized for a horizontal grid, false otherwise.
     * @param listener           The listener for crystal item click events.
     * @param cartClickListener  The listener for "Add to Cart" click events.
     */
    public CrystalAdapter(Context context, List<Crystal> crystalList, List<String> userFavourites,
                          boolean isFavouritesView, boolean isHorizontalGrid,
                          onClickListener listener,
                          OnCartClickListener cartClickListener) {
        this.context = context;
        this.crystalList = crystalList;
        this.userFavourites = userFavourites; // This can be null, handle checks accordingly
        this.isFavouritesView = isFavouritesView;
        this.isHorizontalGrid = isHorizontalGrid;
        this.listener = listener;
        this.cartClickListener = cartClickListener;
        // Improves performance if item IDs are stable and unique.
        // Here, we use the hashcode of the crystal's ID.
        setHasStableIds(true);
    }

    /**
     * ViewHolder class for crystal items.
     * Holds references to the UI elements within each item view of the RecyclerView,
     * such as the crystal's image, name, price, wishlist icon, and add-to-cart button.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ImageView to display the crystal's primary image.
        ImageView crystalImage;
        // ImageView acting as the wishlist/favourite icon.
        ImageView wishlistIcon;
        // ImageView acting as the "Add to Cart" button.
        ImageView addToCart; // This might be null if the layout doesn't always include it.
        // TextView to display the crystal's name.
        TextView crystalName;
        // TextView to display the crystal's price.
        TextView crystalPrice;

        /**
         * Constructs a new ViewHolder.
         *
         * @param itemView The view of the individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize UI elements from the item view.
            crystalImage = itemView.findViewById(R.id.crystalImage);
            crystalName = itemView.findViewById(R.id.crystalName);
            crystalPrice = itemView.findViewById(R.id.crystalPrice);
            wishlistIcon = itemView.findViewById(R.id.wishlistIcon);
            // addToCart might not be present in all item layouts (e.g., R.layout.crystal_item might not have it).
            // It's safer to check for its presence or ensure all layouts define it.
            addToCart = itemView.findViewById(R.id.addToCart);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * The layout inflated depends on whether the adapter is in {@code isFavouritesView} mode.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View. (Not used here as item types are uniform).
     * @return A new ViewHolder that holds a View for a crystal item.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the appropriate layout based on whether this is for the favourites screen or a general list.
        // R.layout.item_favourite_crystal for the favourites view.
        // R.layout.crystal_item for other views.
        View view = LayoutInflater.from(context).inflate(
                isFavouritesView ? R.layout.item_favourite_crystal : R.layout.crystal_item,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method populates the ViewHolder's views with data from the {@link Crystal}
     * at the given position. It also sets up click listeners for the item itself,
     * the wishlist icon, and the "Add to Cart" button.
     *
     * Adjusts item width if {@code isHorizontalGrid} is true.
     *
     * @param holder   The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Adjust item width if this adapter is used for a horizontal grid display.
        // Each item will take up half the screen width.
        if(isHorizontalGrid) {
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.width = screenWidth / 2; // Set item width to half of the screen width.
            holder.itemView.setLayoutParams(params);
        }

        // Get the Crystal object for the current position.
        Crystal crystal = crystalList.get(position);

        // Load the crystal's primary image using Glide.
        // Assumes crystal.getImageUrls() returns a list and takes the first one if available.
        if (crystal.getImageUrls() != null && !crystal.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(crystal.getImageUrls().get(0)) // Load the first image URL.
                    .apply(new RequestOptions().fitCenter()) // Options like fitCenter or centerCrop.
                    .placeholder(R.drawable.crystal) // Default placeholder image.
                    .into(holder.crystalImage);
        } else {
            // Fallback if no image URLs are available.
            holder.crystalImage.setImageResource(R.drawable.crystal); // Set a default placeholder.
        }

        // Set the crystal's name and price.
        holder.crystalName.setText(crystal.getName());
        holder.crystalPrice.setText("NZD " + (int) crystal.getPrice()); // Casting price to int.

        // Get the current Firebase user to handle user-specific actions like favourites.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // --- Wishlist Icon Logic ---
        if (isFavouritesView) {
            // In Favourites screen: Wishlist icon acts as a "remove from favourites" button.
            holder.wishlistIcon.setImageResource(R.drawable.close_button); // "Remove" icon.
            holder.wishlistIcon.setOnClickListener(v -> {
                if (currentUser != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    // Ensure the position is valid before attempting to remove.
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        String userId = currentUser.getUid();
                        removeFromFavourites(userId, crystal.getId()); // Firebase call
                        if (userFavourites != null) userFavourites.remove(crystal.getId()); // Update local list
                        crystalList.remove(adapterPosition);          // Remove from adapter's list
                        notifyItemRemoved(adapterPosition);           // Notify RecyclerView
                        Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                        // Apply animation for visual feedback.
                        holder.wishlistIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_pulse));
                    }
                }
            });

            // "Add to Cart" button logic specifically for the Favourites view.
            // Ensure addToCart button exists in the layout (item_favourite_crystal.xml)
            if (holder.addToCart != null) {
                holder.addToCart.setVisibility(View.VISIBLE); // Make sure it's visible
                holder.addToCart.setOnClickListener(v -> {
                    if (cartClickListener != null) {
                        cartClickListener.onAddToCartClicked(crystal); // Invoke callback
                        // Apply animation.
                        holder.addToCart.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop));
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            // In general list view: Wishlist icon toggles favourite status.
            // Determine if the crystal is currently favourited by the user.
            boolean isFavourited = userFavourites != null && userFavourites.contains(crystal.getId());
            holder.wishlistIcon.setImageResource(isFavourited ? R.drawable.purple_heart : R.drawable.heart_outline);

            holder.wishlistIcon.setOnClickListener(v -> {
                if (currentUser != null) { // User must be logged in to manage favourites.
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        String userId = currentUser.getUid();
                        // Check current favourite status again, in case it changed due to concurrent operations
                        // or if userFavourites list was modified elsewhere without notifying adapter.
                        // For simplicity, we re-check against the userFavourites list.
                        boolean isNowFavourited = userFavourites != null && userFavourites.contains(crystal.getId());

                        if (isNowFavourited) {
                            // If currently favourited, remove it.
                            removeFromFavourites(userId, crystal.getId());
                            if (userFavourites != null) userFavourites.remove(crystal.getId());
                            holder.wishlistIcon.setImageResource(R.drawable.heart_outline); // Update icon
                            Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show();
                            holder.wishlistIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_pulse));
                        } else {
                            // If not favourited, add it.
                            addToFavourites(userId, crystal.getId());
                            if (userFavourites != null) userFavourites.add(crystal.getId());
                            holder.wishlistIcon.setImageResource(R.drawable.purple_heart); // Update icon
                            Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                            holder.wishlistIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop));
                        }
                        // Notify that item data has changed to re-render the wishlist icon correctly,
                        // especially if other parts of the item view depend on favourite status.
                        notifyItemChanged(adapterPosition);
                    }
                } else {
                    // Prompt user to log in if they try to use favourites without being authenticated.
                    Toast.makeText(context, "Please log in to use favourites", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- Item Click Listener ---
        // Set a click listener for the entire item view.
        // When clicked, it invokes the onCrystalClick method of the provided listener.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCrystalClick(crystal);
            }
        });
    }

    /**
     * Adds a crystal to the user's favourites list in Firebase Firestore.
     *
     * @param userId    The ID of the current user.
     * @param crystalId The ID of the crystal to add to favourites.
     */
    private void addToFavourites(String userId, String crystalId) {
        FirebaseFirestore.getInstance()
                .collection("users") // Target the "users" collection.
                .document(userId)       // Target the document for the current user.
                .update("favourites", FieldValue.arrayUnion(crystalId)) // Add crystalId to the "favourites" array.
                .addOnSuccessListener(aVoid -> Log.d("FAV", "Added to favourites: " + crystalId))
                .addOnFailureListener(e -> Log.e("FAV", "Error adding to favourites", e));
    }

    /**
     * Removes a crystal from the user's favourites list in Firebase Firestore.
     *
     * @param userId    The ID of the current user.
     * @param crystalId The ID of the crystal to remove from favourites.
     */
    private void removeFromFavourites(String userId, String crystalId) {
        FirebaseFirestore.getInstance()
                .collection("users") // Target the "users" collection.
                .document(userId)       // Target the document for the current user.
                .update("favourites", FieldValue.arrayRemove(crystalId)) // Remove crystalId from "favourites" array.
                .addOnSuccessListener(aVoid -> Log.d("FAV", "Removed from favourites: " + crystalId))
                .addOnFailureListener(e -> Log.e("FAV", "Error removing from favourites", e));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of crystals in this adapter.
     */
    @Override
    public int getItemCount() {
        // Return the size of the crystal list.
        // Add a null check for safety, though crystalList should ideally not be null.
        return crystalList != null ? crystalList.size() : 0;
    }

    /**
     * Returns the stable ID for the item at {@code position}.
     * If {@link #hasStableIds()} would return false this method should return
     * {@link RecyclerView#NO_ID}. The default implementation of this method returns
     * {@link RecyclerView#NO_ID}.
     *
     * Here, we use the hash code of the crystal's ID as its stable ID.
     *
     * @param position Adapter position to query.
     * @return The stable ID of the item at position.
     */
    @Override
    public long getItemId(int position) {
        // Use the hash code of the crystal's ID string as a long ID.
        // Ensure crystalList and the crystal at the position, and its ID are not null.
        if (crystalList != null && position >= 0 && position < crystalList.size() &&
                crystalList.get(position) != null && crystalList.get(position).getId() != null) {
            return crystalList.get(position).getId().hashCode();
        }
        return RecyclerView.NO_ID; // Fallback if data is not as expected.
    }
}