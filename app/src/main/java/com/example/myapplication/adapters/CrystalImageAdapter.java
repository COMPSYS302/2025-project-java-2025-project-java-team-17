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

public class CrystalImageAdapter extends RecyclerView.Adapter<CrystalImageAdapter.ViewHolder> {

    private final Context context;
    private final List<String> imageUrls;
    private final boolean isClickable;
    private final onClickListener listener;

    public interface onClickListener {
        void onClick(int position);
    }

    public CrystalImageAdapter(Context context, List<String> imageUrls, boolean isClickable, onClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isClickable = isClickable;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.crystalImageItem);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crystal_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.crystal)
                .into(holder.imageView);

        if (isClickable) {
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(position);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}
