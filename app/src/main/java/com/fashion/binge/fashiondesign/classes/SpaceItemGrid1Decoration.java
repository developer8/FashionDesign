package com.fashion.binge.fashiondesign.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemGrid1Decoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemGrid1Decoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0 || parent.getChildAdapterPosition(view) == 1) {
            outRect.top = space+4;
            outRect.left = space;
            outRect.right = space/2+2;
            outRect.bottom = space+4;

        } else {
            outRect.left = space;
            outRect.right = space/2+2;
            outRect.bottom = space+4;
        }
    }
}