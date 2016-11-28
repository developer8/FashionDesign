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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.models.CategoryModel;

import java.util.List;


public class LevelTwoCategoryRecyclerAdapter extends RecyclerView.Adapter<LevelTwoCategoryRecyclerAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryModel> categoryModelList;
    private List<String> levelTwoCategorySearchFactor;

    public LevelTwoCategoryRecyclerAdapter(Context context, List<CategoryModel> categoryModelList, List<String> levelTwoCategorySearchFactor) {
        this.context = context;
        this.categoryModelList = categoryModelList;
        this.levelTwoCategorySearchFactor = levelTwoCategorySearchFactor;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.level_two_category_recyler_list, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        final CategoryModel categoryModel = categoryModelList.get(position);
        holder.levelTwoCategoryName.setText(categoryModel.getCategoryName());
        Glide.with(context)
                .load(categoryModel.getCategoryImage())
                .into(holder.levelTwoCategoryImage);
        Drawable drawable=holder.parent.getBackground();
        if (categoryModel.isBackgroundEnabled()) {
            if(drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(ContextCompat.getColor(context,R.color.colorPrimary));
            }
            holder.levelTwoCategoryImage.setColorFilter(Color.parseColor("#ffffff"));
        } else {
            if(drawable instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) drawable;
                gradientDrawable.setColor(ContextCompat.getColor(context,R.color.darkColor));
            }
            holder.levelTwoCategoryImage.setColorFilter(Color.parseColor("#00ffffff"));
        }
        holder.topParent.setTag(categoryModel.getCategoryId());
        holder.topParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!levelTwoCategorySearchFactor.contains(holder.topParent.getTag().toString())) {
                    categoryModel.setBackgroundEnabled(true);
                    notifyDataSetChanged();
                    levelTwoCategorySearchFactor.add(holder.topParent.getTag().toString());
                } else {
                    categoryModel.setBackgroundEnabled(false);
                    notifyDataSetChanged();
                    levelTwoCategorySearchFactor.remove(holder.topParent.getTag().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        protected ImageView levelTwoCategoryImage;
        protected TextView levelTwoCategoryName;
        protected LinearLayout parent, topParent;

        public CategoryViewHolder(View v) {
            super(v);
            levelTwoCategoryImage = (ImageView) v.findViewById(R.id.level_two_category_image);
            levelTwoCategoryName = (TextView) v.findViewById(R.id.level_two_category_name);
            parent = (LinearLayout) v.findViewById(R.id.parent);
            topParent = (LinearLayout) v.findViewById(R.id.top_parent);
            topParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    parent.getLayoutParams().width = parent.getHeight();
                    parent.requestLayout();
                    topParent.getLayoutParams().width = parent.getWidth();
                    topParent.requestLayout();
                }
            });
        }
    }
}
