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
  private List<CartItem> cartList;
  private Context context;
  private CartItemClickListener listener;


  public interface CartItemClickListener {
    void onDeleteItem(CartItem item, int position);
    void onQuantityChanged(CartItem item, int newQuantity);
  }

  public CartAdapter(Context context, List<CartItem> cartList, CartItemClickListener listener) {
    Log.d("CartAdapter", "Constructor called with " + cartList.size() + " items");
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

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
    return new ViewHolder(view);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    ImageView ivProductImage;
    TextView tvProductName;
    TextView tvProductPrice;
    ImageButton btn_delete;
    ImageButton btn_decrease;
    TextView tvQuantity;
    ImageButton btn_increase;

    public ViewHolder(View itemView) {
      super(itemView);

      ivProductImage = itemView.findViewById(R.id.img_product);
      tvProductName = itemView.findViewById(R.id.tv_product_name);
      tvProductPrice = itemView.findViewById(R.id.tv_product_price);
      btn_delete = itemView.findViewById(R.id.btn_delete);
      btn_decrease = itemView.findViewById(R.id.btn_decrease);
      tvQuantity = itemView.findViewById(R.id.tv_quantity);
      btn_increase = itemView.findViewById(R.id.btn_increase);
    }
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Log.d("CartAdapter", "onBindViewHolder called for position: " + position);

    try {
      CartItem cartItem = cartList.get(position);
      if (cartItem == null) {
        Log.e("CartAdapter", "CartItem is null at position: " + position);
        return;
      }

      if (cartItem.getCrystal() == null) {
        Log.e("CartAdapter", "Crystal is null at position: " + position);
        return;
      }

      Log.d("CartAdapter", "Binding crystal: " + cartItem.getCrystal().getName());

      if (cartItem.getCrystal().getImageUrls() != null
          && !cartItem.getCrystal().getImageUrls().isEmpty()) {

        Log.d("CartAdapter", "Loading image: " + cartItem.getCrystal().getImageUrls().get(0));
        Glide.with(context)
            .load(cartItem.getCrystal().getImageUrls().get(0))
            .placeholder(R.drawable.placeholder_crystal)
            .into(holder.ivProductImage);
      } else {
        Log.w("CartAdapter", "No image URLs available, using placeholder");
        holder.ivProductImage.setImageResource(R.drawable.placeholder_crystal);
      }

      holder.tvProductName.setText(cartItem.getCrystal().getName());

      Double price = cartItem.getCrystal().getPrice();
      if (price != null) {
        holder.tvProductPrice.setText("NZD " + String.valueOf(price));
      } else {
        holder.tvProductPrice.setText("NZD 0.00");
      }

      holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

      Log.d("CartAdapter", "Successfully bound item at position: " + position);


      holder.btn_delete.setOnClickListener(v->{

        if (listener != null) {
          listener.onDeleteItem(cartItem, position);
        }
      });

      holder.btn_decrease.setOnClickListener(v-> {
        listener.onQuantityChanged(cartItem, -1);
      });

      holder.btn_increase.setOnClickListener(v-> {
        listener.onQuantityChanged(cartItem, 1);
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
