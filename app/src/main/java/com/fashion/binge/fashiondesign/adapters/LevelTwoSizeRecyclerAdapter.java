package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.R;

import java.util.List;


public class LevelTwoSizeRecyclerAdapter extends RecyclerView.Adapter<LevelTwoSizeRecyclerAdapter.CategoryViewHolder> {
    private Context context;
    private List<String> size;

    public LevelTwoSizeRecyclerAdapter(Context context, List<String> size) {
        this.context = context;
        this.size = size;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.level_two_size_recyler_list, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        String size = this.size.get(position);
        holder.sizeText.setText(size);
        RelativeLayout.LayoutParams layoutParams;
        if (size.length() > 1) {
            layoutParams = new RelativeLayout.LayoutParams(120, 60);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(60, 60);
        }
        holder.container.setLayoutParams(layoutParams);
        holder.container.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_long_size_border));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = holder.container.getBackground();
                if (drawable instanceof GradientDrawable) {
                    GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                    gradientDrawable.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    gradientDrawable.setStroke(0, ContextCompat.getColor(context, R.color.colorPrimary));
                    holder.sizeText.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return size.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        protected TextView sizeText;
        protected FrameLayout container;

        public CategoryViewHolder(View v) {
            super(v);
            sizeText = (TextView) v.findViewById(R.id.size_text);
            container = (FrameLayout) v.findViewById(R.id.container);
        }
    }
}
