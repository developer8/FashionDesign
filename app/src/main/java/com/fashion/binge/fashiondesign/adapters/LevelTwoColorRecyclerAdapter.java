package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.models.CategoryModel;

import java.util.List;


public class LevelTwoColorRecyclerAdapter extends RecyclerView.Adapter<LevelTwoColorRecyclerAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryModel> categoryModelList;

    public LevelTwoColorRecyclerAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.level_two_color_recyler_list, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        public CategoryViewHolder(View v) {
            super(v);

        }
    }
}
