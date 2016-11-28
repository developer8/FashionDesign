package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.interfaces.CategoryParentOnClickInteface;
import com.fashion.binge.fashiondesign.models.CategoryModel;

import java.util.List;


public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryModel> categoryModelList;
    private CategoryParentOnClickInteface categoryParentOnClickInteface;
    public CategoryRecyclerAdapter(Context context, List<CategoryModel> categoryModelList, CategoryParentOnClickInteface categoryParentOnClickInteface) {
        this.context = context;
        this.categoryModelList=categoryModelList;
        this.categoryParentOnClickInteface=categoryParentOnClickInteface;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.category_recyler_list, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        final CategoryModel categoryModel=categoryModelList.get(position);
        holder.categoryText.setText(categoryModel.getCategoryName());
        holder.categoryText.setTag(categoryModel.getCategoryId());
        Glide.with(context)
                .load(categoryModel.getCategoryImage())
                .into(holder.categoryImage);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryParentOnClickInteface.onCategoryParentClick(categoryModel.getCategoryId(),holder.categoryText,holder.categoryImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        protected ImageView categoryImage;
        protected TextView categoryText;
        protected LinearLayout parent;
        public CategoryViewHolder(View v) {
            super(v);
            categoryImage = (ImageView) v.findViewById(R.id.category_image);
            categoryText=(TextView)v.findViewById(R.id.category_name);
            parent=(LinearLayout)v.findViewById(R.id.parent);
        }
    }
}
