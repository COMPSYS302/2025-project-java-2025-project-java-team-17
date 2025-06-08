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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
  private final List<CartItem> cartList;
  private final Context context;
  private final CartItemClickListener listener;

  public interface CartItemClickListener {
    void onDeleteItem(CartItem item, int position);
    void onQuantityChanged(CartItem item, int change);
  }

  public CartAdapter(Context context, List<CartItem> cartList, CartItemClickListener listener) {
    this.context = context;
    this.cartList = cartList;
    this.listener = listener;

    for (int i = 0; i < cartList.size(); i++) {
      CartItem item = cartList.get(i);
      if (item == null) {
        Log.e("CartAdapter", "CartItem at position " + i + " is null!");
      } else if (item.getCrystal() == null) {
        Log.e("CartAdapter", "Crystal at position " + i + " is null!");
      }
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    final ImageView ivProductImage;
    final TextView tvProductName;
    final TextView tvProductPrice;
    final ImageButton btnDelete;
    final ImageButton btnDecrease;
    final TextView tvQuantity;
    final ImageButton btnIncrease;

    public ViewHolder(View itemView) {
      super(itemView);
      ivProductImage = itemView.findViewById(R.id.img_product);
      tvProductName = itemView.findViewById(R.id.tv_product_name);
      tvProductPrice = itemView.findViewById(R.id.tv_product_price);
      btnDelete = itemView.findViewById(R.id.btn_delete);
      btnDecrease = itemView.findViewById(R.id.btn_decrease);
      tvQuantity = itemView.findViewById(R.id.tv_quantity);
      btnIncrease = itemView.findViewById(R.id.btn_increase);
    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    try {
      CartItem cartItem = cartList.get(position);
      if (cartItem == null || cartItem.getCrystal() == null) return;

      Glide.with(context)
              .load(cartItem.getCrystal().getImageUrls().get(0))
              .placeholder(R.drawable.placeholder_crystal)
              .into(holder.ivProductImage);

      holder.tvProductName.setText(cartItem.getCrystal().getName());

      Double price = cartItem.getCrystal().getPrice();
      holder.tvProductPrice.setText("NZD " + (price != null ? price : 0.00));
      holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

      holder.btnDelete.setOnClickListener(v -> {
        if (listener != null) listener.onDeleteItem(cartItem, position);
      });

      holder.btnDecrease.setOnClickListener(v -> {
        if (listener != null) listener.onQuantityChanged(cartItem, -1);
      });

      holder.btnIncrease.setOnClickListener(v -> {
        if (listener != null) listener.onQuantityChanged(cartItem, 1);
      });

    } catch (Exception e) {
      Log.e("CartAdapter", "Error in onBindViewHolder at position: " + position, e);
    }
  }

  @Override
  public int getItemCount() {
    return cartList.size();
  }
}
