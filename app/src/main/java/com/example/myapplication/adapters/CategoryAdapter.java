package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.Category;

import java.util.List;

/**
 * Adapter for displaying a list of {@link Category} objects in a RecyclerView.
 * Each item in the RecyclerView will represent a single category, showing its
 * title and image.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    // Context from which the adapter is instantiated, used for layout inflation.
    private final Context context;
    // The list of Category objects to be displayed by the RecyclerView.
    private final List<Category> categoryList;
    // Click listener for handling item clicks within the RecyclerView.
    private final View.OnClickListener clickListener;

    /**
     * Constructs a new {@code CategoryAdapter}.
     *
     * @param context The current context, used to inflate layouts.
     * @param categoryList The list of categories to display.
     * @param clickListener The listener to be invoked when an item in the RecyclerView is clicked.
     */
    public CategoryAdapter(Context context, List<Category> categoryList, View.OnClickListener clickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.clickListener = clickListener;
    }

    /**
     * ViewHolder class for the category items.
     * Holds references to the UI elements (TextView for title, ImageView for image)
     * within each item view of the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TextView to display the title of the category.
        final TextView categoryTitle;
        // ImageView to display the image associated with the category.
        final ImageView categoryImage;

        /**
         * Constructs a new ViewHolder.
         *
         * @param itemView The view of the individual item in the RecyclerView (e.g., a card).
         */
        public ViewHolder(View itemView) {
            super(itemView);
            // Initialize the category title TextView from the item view.
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            // Initialize the category image ImageView from the item view.
            categoryImage = itemView.findViewById(R.id.categoryImage);
        }
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for individual category items.
        // R.layout.category_card_item is the XML layout file for each category card.
        View view = LayoutInflater.from(context).inflate(R.layout.category_card_item, parent, false);
        // Set the click listener (passed from the constructor) on the item view.
        view.setOnClickListener(clickListener);
        // Create and return a new ViewHolder instance with the inflated view.
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the {@link ViewHolder#itemView} to reflect
     * the item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the Category object at the current position.
        Category category = categoryList.get(position);
        // Set the category title in the ViewHolder's TextView.
        holder.categoryTitle.setText(category.getName());
        // Set the category image in the ViewHolder's ImageView using its resource ID.
        // Glide could also be used here if images were URLs or needed complex loading.
        holder.categoryImage.setImageResource(category.getImageResId());

        // Example of using Glide if category.getImageResId() was a URL or needed advanced loading:
        // Glide.with(context)
        //      .load(category.getImageResId()) // or category.getImageUrl() if it were a URL
        //      .placeholder(R.drawable.default_placeholder) // Optional placeholder
        //      .error(R.drawable.default_error) // Optional error image
        //      .into(holder.categoryImage);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        // Return the size of the category list.
        return categoryList.size();
    }
}