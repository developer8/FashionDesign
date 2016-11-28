package com.fashion.binge.fashiondesign;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.adapters.AllShopPageListViewAdapter;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.ShopPageContentHolder;
import com.fashion.binge.fashiondesign.json.GetAllShopPage;
import com.fashion.binge.fashiondesign.models.ShoppageInfo;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

@SuppressWarnings("ConstantConditions")
public class AllShopPage extends AppCompatActivity {
    private StickyListHeadersListView stickyListHeadersListView;
    private Toolbar toolbar;
    private EditText searchText;
    private ImageView cart, notification;
    private List<ShoppageInfo> shoppageInfoList;
    private TextView cartNumber, notificationNumber;
    private FloatingActionButton floatingActionButton;

    String projectToken;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_all__shop_page);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        setSupportActionBar(toolbar);
        projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
        mixpanel = MixpanelAPI.getInstance(AllShopPage.this,projectToken);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);
        getAllShopPage();
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllShopPage.this, Cart.class);
                startActivity(intent);
                mixpanel.track("AllpagetoCart",null);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllShopPage.this, Notification.class);
                startActivity(intent);
                try {
                    JSONObject props = new JSONObject();
                    props.put("Notification", "Clicked");
                    //props.put("Logged in", false);
                    mixpanel.track("AllShopPage Notify", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categorySearchIntent = new Intent(AllShopPage.this, CategorySearch.class);
                startActivity(categorySearchIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                try {
                    JSONObject props = new JSONObject();
                    props.put("SearchIcon", "Clicked");
                    //props.put("Logged in", false);
                    mixpanel.track("AllShopPage SearchIcon", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }
            }
        });
    }

    private void getAllShopPage() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SweetAlertDialog pDialog = new AlertDIalogMessage().showProgressDialog(this, "Loading...");
        new GetAllShopPage(this).getShopPageContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ShopPageContentHolder() {
            @Override
            public void setErrorShopPageContent(String statusCode, String statusText) {
                if (statusCode.equals("401")) {
                    pDialog.dismiss();
                    Utils.setTokenInfo(AllShopPage.this, pDialog, new AccessTokenInfoHolder() {
                        @Override
                        public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                            getAllShopPage();
                        }
                    });
                }
            }

            @Override
            public void setSuccessShopPageContent(String success, String data) {
                if (success.equals("true")) {
                    getShopPageContent(data);
                    sortShopPageInfoList();
                    prepareHeaderListView(shoppageInfoList);
                    pDialog.dismiss();
                } else {
                    pDialog.dismiss();
                    new AlertDIalogMessage().showErrorDialog(AllShopPage.this, data);
                }
            }
        });
    }

    private void sortShopPageInfoList() {
        Comparator<ShoppageInfo> comparator = new Comparator<ShoppageInfo>() {
            public int compare(ShoppageInfo c1, ShoppageInfo c2) {
                return c1.getSellerNickName().compareTo(c2.getSellerNickName());
            }
        };
        Collections.sort(shoppageInfoList, comparator);
    }

    private void initialiseLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.shops);
        toolbarTitle.setTextColor(Color.WHITE);
        searchText = (EditText) findViewById(R.id.searchText);
        stickyListHeadersListView = (StickyListHeadersListView) findViewById(R.id.list);
        cart = (ImageView) findViewById(R.id.cart);
        cartNumber = (TextView) findViewById(R.id.cart_number);
        notification = (ImageView) findViewById(R.id.notification);
        notificationNumber = (TextView) findViewById(R.id.notification_number);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void prepareHeaderListView(List<ShoppageInfo> list) {
        final AllShopPageListViewAdapter adapter = new AllShopPageListViewAdapter(this, list);
        stickyListHeadersListView.setAdapter(adapter);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        stickyListHeadersListView.setFastScrollEnabled(true);
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

    private List<ShoppageInfo> getShopPageContent(String data /*data is type of json*/) {
        shoppageInfoList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                ShoppageInfo shoppageInfo = new ShoppageInfo();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String sellerId = jsonObject1.getString("seller_id");
                String sellerName = jsonObject1.getString("seller_name");
                String sellerNickName = jsonObject1.getString("seller_nickname");
                String sellerBanner = jsonObject1.getString("seller_banner");
                String sellerAvatar = jsonObject1.getString("seller_avatar");
                String sellerGroup = jsonObject1.getString("seller_group");
                String userFollow = jsonObject1.getString("user_follow");
                String sellerFlag = jsonObject1.getString("seller_country_flag");
                String sellerCountry = jsonObject1.getString("seller_country_name");
                shoppageInfo.setSellerId(sellerId);
                shoppageInfo.setSellerName(sellerName);
                shoppageInfo.setSellerNickName(sellerNickName);
                shoppageInfo.setSellerBanner(sellerBanner);
                shoppageInfo.setSellerGroup(sellerGroup);
                shoppageInfo.setUserFollow(userFollow);
                shoppageInfo.setSellerCountryFlag(sellerFlag);
                shoppageInfo.setSellerCountryName(sellerCountry);
                shoppageInfo.setSellerAvater(sellerAvatar);
                shoppageInfoList.add(shoppageInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shoppageInfoList;
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
        String count = sharedPreferences.getString(SharedPrefrenceInfo.NOTIFICATION_COUNT, "0");
        if (!count.equals("0")) {
            notificationNumber.setVisibility(View.VISIBLE);
            notificationNumber.setText(count);
        } else {
            notificationNumber.setVisibility(View.GONE);
        }
    }
}


