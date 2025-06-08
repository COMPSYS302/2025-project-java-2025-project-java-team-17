package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.CartItem;

import java.util.List;

/**
 * Adapter for displaying a list of {@link CartItem} objects in a RecyclerView.
 * Each item in the RecyclerView represents a product in the shopping cart,
 * showing its image, name, price, and quantity, along with controls
 * to change the quantity or remove the item from the cart.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
  // Tag for logging purposes, helps in debugging.
  private static final String TAG = "CartAdapter";

  // The list of CartItem objects to be displayed by the RecyclerView.
  private final List<CartItem> cartList;
  // Context from which the adapter is instantiated, used for layout inflation and Glide.
  private final Context context;
  // Listener for handling actions performed on cart items, such as deletion or quantity change.
  private final CartItemClickListener listener;

  /**
   * Interface definition for a callback to be invoked when actions are performed
   * on a cart item, such as deleting it or changing its quantity.
   */
  public interface CartItemClickListener {
    /**
     * Called when the delete button for a cart item is clicked.
     *
     * @param item The {@link CartItem} that is to be deleted.
     * @param position The position of the item in the adapter's data set.
     */
    void onDeleteItem(CartItem item, int position);

    /**
     * Called when the quantity of a cart item is changed (increased or decreased).
     *
     * @param item The {@link CartItem} whose quantity is being changed.
     * @param change The change in quantity (e.g., +1 for increase, -1 for decrease).
     */
    void onQuantityChanged(CartItem item, int change);
  }

  /**
   * Constructs a new {@code CartAdapter}.
   *
   * @param context   The current context.
   * @param cartList  The list of {@link CartItem}s to display.
   * @param listener  The listener for item actions (delete, quantity change).
   */
  public CartAdapter(Context context, List<CartItem> cartList, CartItemClickListener listener) {
    this.context = context;
    this.cartList = cartList;
    this.listener = listener;

    // Defensive check: Log an error if any CartItem or its underlying Crystal is null.
    // This helps in diagnosing issues with data integrity early on.
    for (int i = 0; i < cartList.size(); i++) {
      CartItem item = cartList.get(i);
      if (item == null) {
        Log.e(TAG, "CartItem at position " + i + " is null!");
      } else if (item.getCrystal() == null) {
        Log.e(TAG, "Crystal within CartItem at position " + i + " is null!");
      }
    }
  }

  /**
   * ViewHolder class for the cart items.
   * Holds references to all UI elements within each cart item view,
   * such as product image, name, price, quantity, and action buttons.
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {
    // ImageView to display the product's image.
    final ImageView ivProductImage;
    // TextView to display the product's name.
    final TextView tvProductName;
    // TextView to display the product's price.
    final TextView tvProductPrice;
    // ImageButton to delete the item from the cart.
    final ImageButton btnDelete;
    // ImageButton to decrease the item's quantity.
    final ImageButton btnDecrease;
    // TextView to display the current quantity of the item.
    final TextView tvQuantity;
    // ImageButton to increase the item's quantity.
    final ImageButton btnIncrease;

    /**
     * Constructs a new ViewHolder.
     *
     * @param itemView The view of the individual item in the RecyclerView.
     */
    public ViewHolder(View itemView) {
      super(itemView);
      // Initialize all UI elements from the item view layout.
      ivProductImage = itemView.findViewById(R.id.img_product);
      tvProductName = itemView.findViewById(R.id.tv_product_name);
      tvProductPrice = itemView.findViewById(R.id.tv_product_price);
      btnDelete = itemView.findViewById(R.id.btn_delete);
      btnDecrease = itemView.findViewById(R.id.btn_decrease);
      tvQuantity = itemView.findViewById(R.id.tv_quantity);
      btnIncrease = itemView.findViewById(R.id.btn_increase);
    }
  }

  /**
   * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
   * an item.
   *
   * @param parent   The ViewGroup into which the new View will be added.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View for a cart item.
   */
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // Inflate the layout for individual cart items.
    // R.layout.item_cart is the XML layout file for each item in the cart.
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
    // Create and return a new ViewHolder instance with the inflated view.
    return new ViewHolder(view);
  }

  /**
   * Called by RecyclerView to display the data at the specified position.
   * This method populates the ViewHolder's views with data from the {@link CartItem}
   * at the given position and sets up click listeners for action buttons.
   *
   * @param holder   The ViewHolder which should be updated.
   * @param position The position of the item within the adapter's data set.
   */
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    try {
      // Get the CartItem at the current position.
      final CartItem cartItem = cartList.get(position);

      // Basic null check for cartItem and its underlying Crystal to prevent crashes.
      if (cartItem == null || cartItem.getCrystal() == null) {
        Log.e(TAG, "Null CartItem or Crystal at position: " + position + " in onBindViewHolder. Skipping bind.");
        // Optionally, you could hide the holder.itemView or show an error state.
        return;
      }

      // Load the product image using Glide.
      // Assumes the Crystal object has a list of image URLs and takes the first one.
      if (cartItem.getCrystal().getImageUrls() != null && !cartItem.getCrystal().getImageUrls().isEmpty()) {
        Glide.with(context)
                .load(cartItem.getCrystal().getImageUrls().get(0))
                .placeholder(R.drawable.placeholder_crystal) // Default placeholder
                .into(holder.ivProductImage);
      } else {
        // Set a default image or placeholder if no image URL is available.
        holder.ivProductImage.setImageResource(R.drawable.placeholder_crystal);
        Log.w(TAG, "No image URL found for crystal: " + cartItem.getCrystal().getName());
      }

      // Set the product name.
      holder.tvProductName.setText(cartItem.getCrystal().getName());

      // Set the product price, formatting it and handling potential null price.
      Double price = cartItem.getCrystal().getPrice();
      holder.tvProductPrice.setText("NZD " + (price != null ? String.format("%.2f", price) : "0.00"));
      // Set the current quantity of the item.
      holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

      // --- Set up Click Listeners for action buttons ---

      // Listener for the delete button.
      holder.btnDelete.setOnClickListener(v -> {
        if (listener != null) {
          // Notify the listener that the item should be deleted.
          // Pass the cartItem and its adapter position.
          listener.onDeleteItem(cartItem, holder.getAdapterPosition());
        }
      });

      // Listener for the decrease quantity button.
      holder.btnDecrease.setOnClickListener(v -> {
        if (listener != null) {
          // Notify the listener that the quantity should be changed by -1.
          listener.onQuantityChanged(cartItem, -1);
        }
      });

      // Listener for the increase quantity button.
      holder.btnIncrease.setOnClickListener(v -> {
        if (listener != null) {
          // Notify the listener that the quantity should be changed by +1.
          listener.onQuantityChanged(cartItem, 1);
        }
      });

    } catch (Exception e) {
      // Catch any unexpected exceptions during binding and log them.
      // This prevents the entire app from crashing due to an issue with a single item.
      Log.e(TAG, "Error in onBindViewHolder at position: " + position, e);
      // Optionally, display an error state in the ViewHolder's views here.
    }
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in the cart.
   */
  @Override
  public int getItemCount() {
    // Return the size of the cart list.
    // Add a null check for safety, though cartList should ideally not be null.
    return cartList != null ? cartList.size() : 0;
  }
}