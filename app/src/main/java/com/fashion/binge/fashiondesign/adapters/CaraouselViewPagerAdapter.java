package com.fashion.binge.fashiondesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dd.ShadowLayout;
import com.fashion.binge.fashiondesign.GalleryView;
import com.fashion.binge.fashiondesign.R;

import java.util.ArrayList;
import java.util.List;


public class CaraouselViewPagerAdapter extends PagerAdapter {
    private boolean mIsDefaultItemSelected = false;
    private Context context;
    private List<String> images;

    public CaraouselViewPagerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = mLayoutInflater.inflate(R.layout.fancy_cover_flow_product_detail, container, false);
        ShadowLayout gridParent = (ShadowLayout) itemView.findViewById(R.id.grid_parent);
        final String image= this.images.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.content);
        Glide.with(context)
                .load(image)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GalleryView.class);
                intent.putExtra("image_list", (ArrayList<String>)images);
                intent.putExtra("position",position);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        ViewGroup.LayoutParams params = gridParent.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        gridParent.requestLayout();
        if (!mIsDefaultItemSelected) {
            float MAX_SCALE = 1f;
            gridParent.setScaleX(MAX_SCALE);
            gridParent.setScaleY(MAX_SCALE);
            mIsDefaultItemSelected = true;
        } else {
            float MIN_SCALE = 1f - 1f / 7f;
            gridParent.setScaleX(MIN_SCALE);
            gridParent.setScaleY(MIN_SCALE);
        }
        gridParent.setTag(position);
        container.addView(gridParent);
        return gridParent;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}

