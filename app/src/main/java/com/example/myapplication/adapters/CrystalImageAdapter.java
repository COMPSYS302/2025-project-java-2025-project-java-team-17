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
    private Context context;
    private List<String> imageUrls;

    private onClickListener listener;
    private boolean isClickable;

    public interface onClickListener {
        void onItemClick(int position);
    }

    public CrystalImageAdapter(Context context, List<String> imageUrls, boolean isClickable) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isClickable = isClickable;
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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.crystal_image_item, parent, false);
        return new ViewHolder(view);
    }

    public void setOnClickListener(onClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.crystal)
                .error(R.drawable.crystal)
                .into(holder.imageView);

        if(isClickable) {
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            });

        }else{
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }
}