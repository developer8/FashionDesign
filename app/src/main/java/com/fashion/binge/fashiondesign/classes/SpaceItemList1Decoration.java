package com.fashion.binge.fashiondesign.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemList1Decoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemList1Decoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 2) {
            outRect.top = space+8;
            outRect.left = (space+8);
            outRect.right = space+8;
        } else {
            outRect.left = space+8;
            outRect.right = space+8;
            outRect.top=space;
        }
    }
}