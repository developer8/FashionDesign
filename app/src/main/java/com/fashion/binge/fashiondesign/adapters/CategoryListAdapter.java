package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.models.CategoryModel;

import java.util.List;


public class CategoryListAdapter extends BaseAdapter {
    private Context context;
    private List<CategoryModel> categoryModelList;
    private int lastPosition = -1;

    public CategoryListAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }


    @Override
    public int getCount() {
        return categoryModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_recycler_view_child, parent, false);
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.category_list_animation);
            animation.setDuration((long) (150*position));
            convertView.startAnimation(animation);
            lastPosition = position;
        }
        CategoryModel categoryModel = categoryModelList.get(position);
        LinearLayout parents = (LinearLayout) convertView.findViewById(R.id.parent);
        parents.setTag(categoryModel.getCategoryId());
        TextView categoryName = (TextView) convertView.findViewById(R.id.content);
        categoryName.setTag(categoryModel.getCategoryName());
        categoryName.setText("   " + categoryModel.getCategoryName());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        lastPosition = -1;
    }
}
