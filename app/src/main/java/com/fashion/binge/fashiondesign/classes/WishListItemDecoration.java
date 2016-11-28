package com.fashion.binge.fashiondesign.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class WishListItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public WishListItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left =0;
        outRect.right =0;
        outRect.bottom = space-6;
    }
}