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
import com.example.myapplication.models.Crystal;

import java.util.List;

public class CrystalAdapter extends RecyclerView.Adapter<CrystalAdapter.ViewHolder> {

    private Context context;
    private List<Crystal> crystalList;

    public CrystalAdapter(Context context, List<Crystal> crystalList) {
        this.context = context;
        this.crystalList = crystalList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView crystalImage;
        TextView crystalName;

        public ViewHolder(View itemView) {
            super(itemView);
            crystalImage = itemView.findViewById(R.id.crystalImage);
            crystalName = itemView.findViewById(R.id.crystalName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crystal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Crystal crystal = crystalList.get(position);
        holder.crystalName.setText(crystal.getName());

        Glide.with(context)
                .load(crystal.getImageUrls().get(0))  // Load first image
                .into(holder.crystalImage);
    }

    @Override
    public int getItemCount() {
        return crystalList.size();
    }
}
