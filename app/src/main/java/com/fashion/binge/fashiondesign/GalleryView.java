package com.fashion.binge.fashiondesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fashion.binge.fashiondesign.classes.HackyViewPager;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.senab.photoview.PhotoView;

public class GalleryView extends AppCompatActivity {
    private Toolbar toolbar;
    private static final String ISLOCKED_ARG = "isLocked";
    private ViewPager mViewPager;
    private ProgressBar progressBar;
    private List<String> imageList;
    private TextView toolbarTitle;
    private ImageView cart, notification;
    private TextView cartNumber, notificationNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection unchecked
        imageList = (ArrayList<String>) getIntent().getSerializableExtra("image_list");
        int position = getIntent().getIntExtra("position", 0);
        setContentView(R.layout.activity_gallery_view);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(position);
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryView.this, Cart.class);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationIntent = new Intent(GalleryView.this, Notification.class);
                startActivity(notificationIntent);
            }
        });
        toolbarTitle.setText(String.format("%d/%d", mViewPager.getCurrentItem() + 1, imageList.size()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbarTitle.setText(String.format("%d/%d", mViewPager.getCurrentItem() + 1, imageList.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initialiseLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        cart = (ImageView) findViewById(R.id.cart);
        notification = (ImageView) findViewById(R.id.notification);
        cartNumber = (TextView) findViewById(R.id.cart_number);
        notificationNumber = (TextView) findViewById(R.id.notification_number);
    }

    class SamplePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(GalleryView.this)
                    .load(imageList.get(position))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(photoView);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cartNumber.setText(sharedPreferences.getString(SharedPrefrenceInfo.CART_COUNT, "0"));
        if (cartNumber.getText().toString().equals("0")) {
            cartNumber.setVisibility(View.GONE);
        } else {
            cartNumber.setVisibility(View.VISIBLE);
        }
        Set<String> id = sharedPreferences.getStringSet(SharedPrefrenceInfo.IDSET, new HashSet<String>());
        int size = id.size();
        int count = Integer.parseInt(sharedPreferences.getString(SharedPrefrenceInfo.NOTIFICATION_COUNT, "0"));
        if ((count - size) > 0) {
            notificationNumber.setVisibility(View.VISIBLE);
            notificationNumber.setText(String.valueOf(count-size));
        } else {
            notificationNumber.setVisibility(View.GONE);
        }
    }
}
