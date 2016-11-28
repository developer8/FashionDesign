package com.fashion.binge.fashiondesign;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.adapters.ViewPagerAdapter;
import com.fashion.binge.fashiondesign.classes.BlurActionBarDrawerToggle;
import com.fashion.binge.fashiondesign.classes.BlurDrawerLayout;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.fragments.FollowingFragment;
import com.fashion.binge.fashiondesign.fragments.NewShopFragment;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.HashSet;
import java.util.Set;

public class Homepage extends AppCompatActivity implements View.OnClickListener {
    private BlurDrawerLayout drawerLayout;
    private ImageView drawerBack, hamburgerMenu, setting, search, orders;
    private Toolbar toolbar;
    private LinearLayout drawerParent;
    private RelativeLayout loginParent, shopPageParent, cartParent, categoryParent, wishListParent, myAccountParent;
    private FrameLayout login, shopPage, cart, category, wishList, myAccount, home;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView drawerTitle;
    private TextView loginTextView;
    private SharedPreferences sharedPreferences;
    private TextView toolbarTitle;
    private ImageView cartImage, notification, notificationDrawer;
    private TextView cartNumber, notificationNumber, drawerNotificationNUmber;
    private FloatingActionButton floatingActionButton;
    String projectToken;
    MixpanelAPI mixpanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_homepage);
        if (!isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        prepareHomepage();
        initialiseListener();
        setSupportActionBar(toolbar);
        SetStatusBarColor.setStausBarColor(this);
        toolbarTitle.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        toolbarTitle.setLayoutParams(layoutParams);

        projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
        mixpanel = MixpanelAPI.getInstance(Homepage.this,projectToken);

        mixpanel.timeEvent("HomepageSession");



        prepareDrawerLayout();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setMarginsForDrawerItemsForTablet("portrait");
        } else {
            setMarginsForDrawerItemsForTablet("landscape");
        }
    }

    private void setMarginsForDrawerItemsForTablet(String orientation) {
        RelativeLayout[] relativeLayouts = new RelativeLayout[]{loginParent, shopPageParent, cartParent, categoryParent, wishListParent, myAccountParent};
        for (int i = 0; i < relativeLayouts.length; i++) {
            RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) relativeLayouts[i].getLayoutParams();
            if (isTablet(this)) {
                switch (i) {
                    case 0:
                        if (orientation.equals("portrait")) {
                            relativeParams.setMargins(0, 0, 0, 290);
                        } else {
                            relativeParams.setMargins(0, 0, 0, 240);
                        }
                        break;
                    case 1:
                        relativeParams.setMargins(220, 0, 0, 40);
                        break;
                    case 2:
                        relativeParams.setMargins(220, 40, 0, 0);
                        break;
                    case 3:
                        if (orientation.equals("portrait")) {
                            relativeParams.setMargins(0, 290, 0, 0);
                        } else {
                            relativeParams.setMargins(0, 240, 0, 0);
                        }
                        break;
                    case 4:
                        relativeParams.setMargins(0, 40, 220, 0);
                        break;
                    case 5:
                        relativeParams.setMargins(0, 0, 220, 40);
                        break;
                }
                relativeLayouts[i].setLayoutParams(relativeParams);
            }
        }
    }

    private void initialiseLayout() {
        drawerLayout = (BlurDrawerLayout) findViewById(R.id.drawer_layout);
        drawerParent = (LinearLayout) findViewById(R.id.drawer_parent);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerBack = (ImageView) findViewById(R.id.drawer_back);
        loginParent = (RelativeLayout) findViewById(R.id.login_parent);
        loginTextView = (TextView) findViewById(R.id.loginText);
        shopPageParent = (RelativeLayout) findViewById(R.id.shop_page_parent);
        cartParent = (RelativeLayout) findViewById(R.id.cart_parent);
        categoryParent = (RelativeLayout) findViewById(R.id.category_parent);
        wishListParent = (RelativeLayout) findViewById(R.id.wishlist_parent);
        myAccountParent = (RelativeLayout) findViewById(R.id.my_account_parent);
        login = (FrameLayout) findViewById(R.id.login);
        shopPage = (FrameLayout) findViewById(R.id.shop_page);
        cart = (FrameLayout) findViewById(R.id.my_cart);
        category = (FrameLayout) findViewById(R.id.category);
        wishList = (FrameLayout) findViewById(R.id.wish_list);
        myAccount = (FrameLayout) findViewById(R.id.my_account);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        hamburgerMenu = (ImageView) findViewById(R.id.hamburger_menu);
        drawerTitle = (TextView) findViewById(R.id.drawer_title);
        home = (FrameLayout) findViewById(R.id.home);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setting = (ImageView) findViewById(R.id.setting);
        cartImage = (ImageView) findViewById(R.id.cart);
        search = (ImageView) findViewById(R.id.search);
        notification = (ImageView) findViewById(R.id.notification);
        orders = (ImageView) findViewById(R.id.orders);
        cartNumber = (TextView) findViewById(R.id.cart_number);
        notificationDrawer = (ImageView) findViewById(R.id.notification_drawer);
        notificationNumber = (TextView) findViewById(R.id.notification_number);
        drawerNotificationNUmber = (TextView) findViewById(R.id.drawer_notification_number);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
    }

    private void initialiseListener() {
        drawerParent.setOnClickListener(null);
        drawerBack.setOnClickListener(this);
        loginParent.setOnClickListener(this);
        shopPageParent.setOnClickListener(this);
        cartParent.setOnClickListener(this);
        categoryParent.setOnClickListener(this);
        wishListParent.setOnClickListener(this);
        myAccountParent.setOnClickListener(this);
        hamburgerMenu.setOnClickListener(this);
        setting.setOnClickListener(this);
        home.setOnClickListener(this);
        search.setOnClickListener(this);
        cartImage.setOnClickListener(this);
        notification.setOnClickListener(this);
        notificationDrawer.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        if (tab.getPosition() == 1) {
                            if (!sharedPreferences.getString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "").equals("true")) {
                                viewPager.setCurrentItem(0);
                                tabLayout.setupWithViewPager(viewPager);
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(Homepage.this, SweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setTitleText("Please login to access this feature");
                                sweetAlertDialog.setCancelable(true);
                                sweetAlertDialog.show();
                            }
                        }
                    }
                });
    }

    private void prepareHomepage() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        hamburgerMenu.setVisibility(View.VISIBLE);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.parseColor("#b3ffffff"), Color.WHITE);
        tabLayout.setBackgroundColor(Color.parseColor("#F6705E"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#243345"));
        tabLayout.setSelectedTabIndicatorHeight(5);
        drawerTitle.setText(R.string.menu);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewShopFragment(), "SHOP");
        adapter.addFragment(new FollowingFragment(), "FOLLOWING");
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
    }

    private void prepareDrawerLayout() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        BlurActionBarDrawerToggle actionBarDrawerToggle = new BlurActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                showAnimation(loginParent, login, 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAnimation(shopPageParent, shopPage, 1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showAnimation(cartParent, cart, 2);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAnimation(categoryParent, category, 3);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAnimation(wishListParent, wishList, 4);
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showAnimation(myAccountParent, myAccount, 5);
                                                    }
                                                }, 165 / 4);
                                            }
                                        }, 231 / 4);
                                    }
                                }, 264 / 4);
                            }
                        }, 310 / 4);
                    }
                }, 330 / 4);
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                loginParent.clearAnimation();
                shopPageParent.clearAnimation();
                cartParent.clearAnimation();
                categoryParent.clearAnimation();
                wishListParent.clearAnimation();
                myAccountParent.clearAnimation();
                loginParent.setVisibility(View.INVISIBLE);
                shopPageParent.setVisibility(View.INVISIBLE);
                cartParent.setVisibility(View.INVISIBLE);
                categoryParent.setVisibility(View.INVISIBLE);
                wishListParent.setVisibility(View.INVISIBLE);
                myAccountParent.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        actionBarDrawerToggle.setRadius(4);
        actionBarDrawerToggle.setDownScaleFactor(6.0f);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.drawer_back:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.shop_page_parent:
                Intent allShopPageIntent = new Intent(this, AllShopPage.class);
                startActivity(allShopPageIntent);
                mixpanel.track("Homepage to AllShopPage", null);
                break;
            case R.id.wishlist_parent:
                Intent wishListParent = new Intent(this, WishList.class);
                startActivity(wishListParent);
                break;
            case R.id.category_parent:
                Intent categoryIntent = new Intent(this, Categories.class);
                startActivity(categoryIntent);
                break;
            case R.id.hamburger_menu:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.cart_parent:
                Intent cartIntent = new Intent(this, Cart.class);
                startActivity(cartIntent);
                break;
            case R.id.cart:
                Intent intent = new Intent(Homepage.this, Cart.class);
                startActivity(intent);
                break;
            case R.id.search:
                Intent searchIntent = new Intent(Homepage.this, ProductSearch.class);
                startActivity(searchIntent);
                break;
            case R.id.notification:
                Intent notificationIntent = new Intent(Homepage.this, Notification.class);
                startActivity(notificationIntent);
                break;
            case R.id.notification_drawer:
                Intent notificationDrawerIntent = new Intent(Homepage.this, Notification.class);
                startActivity(notificationDrawerIntent);
                break;
            case R.id.my_account_parent:
                if (sharedPreferences.getString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "false").equals("false")) {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                    pDialog.setTitleText("Please Login to access this feature");
                    pDialog.setConfirmText("Login");
                    pDialog.showCancelButton(true);
                    pDialog.setCancelText("Cancel");
                    pDialog.setCancelable(true);
                    pDialog.show();
                    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            Intent intent = new Intent(Homepage.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Intent myAccountIntent = new Intent(this, MyAccount.class);
                    startActivity(myAccountIntent);
                }
                break;
            case R.id.login_parent:
                if (loginTextView.getText().toString().equals("Login")) {
                    Intent loginIntent = new Intent(this, MainActivity.class);
                    startActivity(loginIntent);
                } else {
                    Intent orderIntent = new Intent(Homepage.this, OrderActivity.class);
                    startActivity(orderIntent);
                }
                break;
            case R.id.home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.setting:
                Intent settingIntent = new Intent(Homepage.this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.fab:
                Intent categorySearchIntent=new Intent(this,CategorySearch.class);
                startActivity(categorySearchIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                break;
        }
    }

    private void showAnimation(final View view, final View subView, int position) {
        view.setVisibility(View.VISIBLE);
        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        if (position == 0) {
            animationFadeIn.setDuration(0);
        }
        // using currentImage as the image view
        animationFadeIn.setFillAfter(true);
        view.startAnimation(animationFadeIn);
        animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1);
                scaleAnimation.setDuration(300);
                scaleAnimation.setFillAfter(true);
                subView.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(appClose()){
            mixpanel.track("HomepageSession");
        }
    }

    public boolean appClose(){
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mixpanel.timeEvent("HomepageSession");
        if (sharedPreferences.getString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "false").equals("true")) {
            loginTextView.setText(R.string.orders);
            orders.setImageResource(R.mipmap.ic_order);
        } else {
            loginTextView.setText(R.string.login);
            orders.setImageResource(R.mipmap.ic_login);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
            notificationNumber.setText(String.valueOf(count - size));
            drawerNotificationNUmber.setVisibility(View.VISIBLE);
            drawerNotificationNUmber.setText(String.valueOf(count - size));
        } else {
            notificationNumber.setVisibility(View.GONE);
            drawerNotificationNUmber.setVisibility(View.GONE);
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setMarginsForDrawerItemsForTablet("portrait");
        } else {
            setMarginsForDrawerItemsForTablet("landscape");
        }
    }

}
