package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

/**
 * Adapter for displaying a list of crystal image URLs in a RecyclerView.
 * Each item in the RecyclerView will represent a single image of a crystal,
 * loaded using the Glide library. It can optionally handle clicks on images.
 */
public class CrystalImageAdapter extends RecyclerView.Adapter<CrystalImageAdapter.ViewHolder> {

    // Context from which the adapter is instantiated, used for layout inflation and Glide.
    private final Context context;
    // The list of image URL strings to be displayed by the RecyclerView.
    private final List<String> imageUrls;
    // Flag to determine if the images should respond to click events.
    private final boolean isClickable;
    // Listener for handling click events on the images if isClickable is true.
    private final onClickListener listener;

    /**
     * Interface definition for a callback to be invoked when an image is clicked.
     */
    public interface onClickListener {
        /**
         * Called when an image view has been clicked.
         *
         * @param position The position of the clicked item in the adapter.
         */
        void onClick(int position);
    }

    /**
     * Constructs a new {@code CrystalImageAdapter}.
     *
     * @param context     The current context.
     * @param imageUrls   The list of image URLs to display.
     * @param isClickable A boolean indicating whether items are clickable.
     * @param listener    The listener to be invoked when an item is clicked (can be null if not clickable).
     */
    public CrystalImageAdapter(Context context, List<String> imageUrls, boolean isClickable, onClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isClickable = isClickable;
        this.listener = listener;
    }

    /**
     * ViewHolder class for the crystal image items.
     * Holds a reference to the ImageView within each item view of the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ImageView to display the crystal image.
        ImageView imageView;

        /**
         * Constructs a new ViewHolder.
         *
         * @param itemView The view of the individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the ImageView from the item view's layout.
            imageView = itemView.findViewById(R.id.crystalImageItem);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View for the crystal image.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual crystal image items.
        // R.layout.crystal_image_item is the XML layout file for each image.
        View view = LayoutInflater.from(context).inflate(R.layout.crystal_image_item, parent, false);
        // Create and return a new ViewHolder instance with the inflated view.
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method loads the crystal image into the ImageView using Glide and sets up
     * a click listener if the item is designated as clickable.
     *
     * @param holder   The ViewHolder which should be updated.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Load the image from the URL into the ImageView using Glide.
        Glide.with(context)
                .load(imageUrls.get(position)) // Get the image URL at the current position.
                .placeholder(R.drawable.crystal) // Show a placeholder image while loading.
                // Replace R.drawable.crystal with your actual placeholder.
                .into(holder.imageView); // The target ImageView.

        // Check if the items are meant to be clickable.
        if (isClickable) {
            // Set an OnClickListener on the item view.
            holder.itemView.setOnClickListener(v -> {
                // Ensure the listener is not null before invoking its onClick method.
                if (listener != null) {
                    listener.onClick(holder.getAdapterPosition()); // Pass the adapter position of the clicked item.
                }
            });
        } else {
            // If not clickable, ensure no listener is set and the view is not clickable.
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of image URLs in this adapter.
     */
    @Override
    public int getItemCount() {
        // Return the size of the image URLs list.
        // Add a null check for safety, though imageUrls should ideally not be null.
        return imageUrls != null ? imageUrls.size() : 0;
    }
}