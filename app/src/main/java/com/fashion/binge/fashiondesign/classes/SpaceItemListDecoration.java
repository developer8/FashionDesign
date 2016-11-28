package com.fashion.binge.fashiondesign.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemListDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemListDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0 || position==1) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.bottom = 0;
            outRect.top = 0;
            return;
        }
        if (parent.getChildAdapterPosition(view) == 2) {
            outRect.top = 0;
            outRect.left = (space+8);
            outRect.right = space+8;
            return;
        } else {
            outRect.left = space+8;
            outRect.right = space+8;
            outRect.top=space;
        }
    }
}