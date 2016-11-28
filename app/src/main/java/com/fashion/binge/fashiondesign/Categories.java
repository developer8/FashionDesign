package com.fashion.binge.fashiondesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.adapters.ViewPagerAdapter;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.fragments.CategoryFragment;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;
import com.fashion.binge.fashiondesign.json.Category;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Categories extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView toolbarTitle;
    private ImageView cart, notification;
    private TextView cartNumber,notificationNumber;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);
        toolbarTitle.setText(R.string.categories);
        requestCategory();
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Categories.this, Cart.class);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationIntent = new Intent(Categories.this, Notification.class);
                startActivity(notificationIntent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categorySearchIntent=new Intent(Categories.this,CategorySearch.class);
                startActivity(categorySearchIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });
    }

    private void requestCategory() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final SweetAlertDialog pDialog = new AlertDIalogMessage().showProgressDialog(this, "Loading...");
        new Category(this).requestCategory(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ResponseInfoHolder() {
            @Override
            public void setFollowingInfo(String success, String data) {
                if (success.equals("401")) {
                    pDialog.dismiss();
                    Utils.setTokenInfo(Categories.this, pDialog, new AccessTokenInfoHolder() {
                        @Override
                        public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                            requestCategory();
                        }
                    });
                } else {
                    List<String> categoryList = new ArrayList<>();
                    editor.putString(SharedPrefrenceInfo.LEVEL_THREE_CATEGORY, data);
                    editor.apply();
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            categoryList.add(name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setupViewPager(viewPager, categoryList);
                    prepareTabLayout();
                    pDialog.dismiss();
                }
            }
        });
    }

    private void initialiseLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        cart = (ImageView) findViewById(R.id.cart);
        cartNumber = (TextView) findViewById(R.id.cart_number);
        notification = (ImageView) findViewById(R.id.notification);
        notificationNumber=(TextView)findViewById(R.id.notification_number);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
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

    private void prepareTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.parseColor("#b3ffffff"), Color.WHITE);
        tabLayout.setBackgroundColor(Color.parseColor("#F6705E"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#243345"));
        tabLayout.setSelectedTabIndicatorHeight(5);
    }

    private void setupViewPager(ViewPager viewPager, List<String> categoryList) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < categoryList.size(); i++) {
            Fragment fragment = new CategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", categoryList.get(i).toUpperCase());
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, categoryList.get(i).toUpperCase());
        }
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {

    }
}
