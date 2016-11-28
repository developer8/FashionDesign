package com.fashion.binge.fashiondesign.classes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.fashion.binge.fashiondesign.R;

public class SetStatusBarColor {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStausBarColor(Activity context) {
        if (Build.VERSION.SDK_INT >Build.VERSION_CODES.KITKAT) {
            Window window = context.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }
}
