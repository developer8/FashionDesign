package com.fashion.binge.fashiondesign.classes;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class LevelTwoCategoryItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public LevelTwoCategoryItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left =2*space;
            outRect.right =space;
            outRect.bottom = 0;
            outRect.top = 0;
        }else {
            outRect.left =0;
            outRect.right =space;
            outRect.bottom = 0;
            outRect.top = 0;
        }
    }
}