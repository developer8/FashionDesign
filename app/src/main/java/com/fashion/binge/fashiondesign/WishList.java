package com.fashion.binge.fashiondesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.adapters.WishListAdapter;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.classes.WishListItemDecoration;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.JsonResponseHolder;
import com.fashion.binge.fashiondesign.interfaces.WishListCartClick;
import com.fashion.binge.fashiondesign.json.WishlistJSON;
import com.fashion.binge.fashiondesign.models.WishListModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ConstantConditions")
public class WishList extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RelativeLayout topParent;
    private ImageView cart, notification;
    private TextView cartCount, noItem,notificationNumber;
    private FloatingActionButton floatingActionButton;
    String projectToken;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_wish_list);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new WishListItemDecoration(10));

        projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
        mixpanel = MixpanelAPI.getInstance(WishList.this,projectToken);

        getWishList();
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mixpanel = MixpanelAPI.getInstance(WishList.this,projectToken);
                try {
                    JSONObject props = new JSONObject();
                    props.put("Cart", "Clicked");
                    //props.put("Logged in", false);
                    mixpanel.track("WishListToCart", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }
                Intent intent = new Intent(WishList.this, Cart.class);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishList.this, Notification.class);
                startActivity(intent);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categorySearchIntent=new Intent(WishList.this,CategorySearch.class);
                startActivity(categorySearchIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });
    }

    private void getWishList() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SweetAlertDialog pDialog = new AlertDIalogMessage().showProgressDialog(this, "Loading...");
        new WishlistJSON(this).getWishlistData(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
            @Override
            public void onResponse(String status, String message) {
                if (status.equals("401")) {
                    pDialog.dismiss();
                    Utils.setTokenInfo(WishList.this, pDialog, new AccessTokenInfoHolder() {
                        @Override
                        public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                            getWishList();
                        }
                    });
                } else {
                    if (status.equals("true")) {
                        List<WishListModel> wishListData = getWishlistData(message);
                        WishListAdapter wishListAdapter = new WishListAdapter(WishList.this, noItem, wishListData, new WishListCartClick() {
                            @Override
                            public void setWishListCartClick(int x_coordinate, int y_coordinate) {
                                moveCartImageToToolbar(x_coordinate, y_coordinate);
                            }
                        });
                        recyclerView.setAdapter(wishListAdapter);
                        if (recyclerView.getAdapter().getItemCount() == 0) {
                            noItem.setText(R.string.no_wishlist);
                            noItem.setVisibility(View.VISIBLE);
                        } else {
                            noItem.setVisibility(View.INVISIBLE);
                        }
                        pDialog.dismissWithAnimation();
                    } else {
                        pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        pDialog.setTitleText(message);
                    }
                }
            }
        });
    }

    private void initialiseLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(String.format("%s", "WishList"));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        topParent = (RelativeLayout) findViewById(R.id.top_parent);
        cart = (ImageView) findViewById(R.id.cart);
        notification = (ImageView) findViewById(R.id.notification);
        cartCount = (TextView) findViewById(R.id.cart_number);
        noItem = (TextView) findViewById(R.id.no_item);
        notificationNumber=(TextView)findViewById(R.id.notification_number);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<WishListModel> getWishlistData(String data) {
        List<WishListModel> wishListModelList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            String product = jsonObject.getString("products");
            JSONArray jsonArray = new JSONArray(product);
            for (int i = 0; i < jsonArray.length(); i++) {
                WishListModel wishListModel = new WishListModel();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                wishListModel.setProductId(jsonObject1.getString("product_id"));
                wishListModel.setThumbImage(jsonObject1.getString("thumb"));
                wishListModel.setPrice(jsonObject1.getString("price"));
                wishListModel.setProductName(jsonObject1.getString("name"));
                wishListModelList.add(wishListModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wishListModelList;
    }

    private void moveCartImageToToolbar(int x, int y) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(40, 40);
        final TextView cartNumber = new TextView(getApplicationContext());
        cartNumber.setText("1");
        cartNumber.setTextSize(15);
        cartNumber.setGravity(Gravity.CENTER);
        cartNumber.setBackground(ContextCompat.getDrawable(WishList.this, R.drawable.circletextview));
        //get status bar height and toolbaer height android
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        lp.setMargins(x, y - (toolbar.getLayoutParams().height + statusBarHeight) - 5, 0, 0);
        cartNumber.setLayoutParams(lp);
        (topParent).addView(cartNumber);
        //get the coordinate of cart in the toolbar relative to window
        int pos[] = new int[2];
        cart.getLocationInWindow(pos);
        int x1 = pos[0] + 30;
        int y1 = pos[1] + 30;
        final TranslateAnimation transAnimation = new TranslateAnimation(0f, (x1 - x), 0f, (y1 - y));
        double distance = Math.sqrt(Math.pow((y1 - x1), 2) + Math.pow(y - x, 2));
        transAnimation.setDuration((long) (distance / 2));
        transAnimation.setFillAfter(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cartNumber.startAnimation(transAnimation);
                transAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        cartNumber.clearAnimation();
                        ((ViewGroup) cartNumber.getParent()).removeView(cartNumber);
                        cartCount.setVisibility(View.VISIBLE);
                        cartCount.setText(String.valueOf(Integer.parseInt(cartCount.getText().toString()) + 1));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }, 100);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cartCount.setText(sharedPreferences.getString(SharedPrefrenceInfo.CART_COUNT, "0"));
        if (cartCount.getText().toString().equals("0")) {
            cartCount.setVisibility(View.GONE);
        } else {
            cartCount.setVisibility(View.VISIBLE);
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
