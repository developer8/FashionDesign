package com.fashion.binge.fashiondesign.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemGridDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemGridDecoration(int space) {
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
        if (parent.getChildAdapterPosition(view) == 2 || parent.getChildAdapterPosition(view) == 3) {
            outRect.top = 0;
            outRect.left = space+8;
            outRect.right = space/2+2;
            outRect.bottom = space+4;
            if (position % 2 != 0) {
                outRect.left = space/2+2;
                outRect.right=space+8;
            }
        } else {
            outRect.left = space+8;
            outRect.right = space/2+2;
            outRect.bottom = space+4;
            if (position % 2 != 0) {
                outRect.left = space/2+2;
                outRect.right=space+8;
            }
        }
    }
}