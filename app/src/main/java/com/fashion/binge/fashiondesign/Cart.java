package com.fashion.binge.fashiondesign;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.adapters.CartRecyclerAdapter;
import com.fashion.binge.fashiondesign.adapters.CouponListAdapter;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.DeleteCartInterface;
import com.fashion.binge.fashiondesign.interfaces.JsonResponseHolder;
import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.UpdateCartInfoHolder;
import com.fashion.binge.fashiondesign.json.AddPromoCode;
import com.fashion.binge.fashiondesign.json.GetCartData;
import com.fashion.binge.fashiondesign.json.UpdateCart;
import com.fashion.binge.fashiondesign.models.CartModel;
import com.fashion.binge.fashiondesign.models.CouponModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Cart extends AppCompatActivity implements View.OnClickListener {
    private TextView total, coupon;
    private RecyclerView cartList;
    private Button continueShopping, checkout;
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private EditText promoCode;
    private FrameLayout plus;
    private TextView totalValue;
    private TextView noItem;
    private ImageView cart, notification;
    private CircleImageView couponIcon;
    private TextView cartCount, notificationCount, couponValue;
    private List<CouponModel> couponModelList;
    String projectToken;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseView();
        initialiseListeners();
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
        mixpanel = MixpanelAPI.getInstance(Cart.this,projectToken);
        mixpanel.timeEvent("CartSession");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);
        cartList.setLayoutManager(new LinearLayoutManager(this));
        cartList.setHasFixedSize(false);toolbarTitle.setText(R.string.cart);
        //noinspection deprecation
        total.setText(Html.fromHtml("<b>SUB TOTAL</b><sub><small>(SGD)<small></sub>"));
        toolbarTitle.setText(R.string.cart);
        if (isTablet(this)) {
            continueShopping.post(new Runnable() {
                @Override
                public void run() {
                    continueShopping.getLayoutParams().height = 90;
                    checkout.getLayoutParams().height = 90;
                }
            });
        }
        plus.post(new Runnable() {
            @Override
            public void run() {
                plus.getLayoutParams().height = promoCode.getHeight();
                plus.getLayoutParams().width = promoCode.getHeight();
            }
        });
        cart.setVisibility(View.GONE);
        cartCount.setVisibility(View.GONE);
        notification.setVisibility(View.GONE);
        notificationCount.setVisibility(View.GONE);
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading data");
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        sweetAlertDialog.show();
        setCartData(sweetAlertDialog);
    }

    private void initialiseView() {
        total = (TextView) findViewById(R.id.total);
        cartList = (RecyclerView) findViewById(R.id.cart_list);
        continueShopping = (Button) findViewById(R.id.continue_shopping);
        checkout = (Button) findViewById(R.id.checkout);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        promoCode = (EditText) findViewById(R.id.promo_code);
        plus = (FrameLayout) findViewById(R.id.plus);
        totalValue = (TextView) findViewById(R.id.total_value);
        noItem = (TextView) findViewById(R.id.no_item);
        cart = (ImageView) findViewById(R.id.cart);
        cartCount = (TextView) findViewById(R.id.cart_number);
        notification = (ImageView) findViewById(R.id.notification);
        notificationCount = (TextView) findViewById(R.id.notification_number);
        coupon = (TextView) findViewById(R.id.coupon);
        couponValue = (TextView) findViewById(R.id.coupon_value);
        couponIcon = (CircleImageView) findViewById(R.id.coupon_icon);
    }

    private void initialiseListeners() {
        checkout.setOnClickListener(this);
        plus.setOnClickListener(this);
        continueShopping.setOnClickListener(this);
        couponIcon.setOnClickListener(this);
    }

    private void prepareCartList(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            List<com.fashion.binge.fashiondesign.models.GetCartData> cartDataList = new ArrayList<>();
            CartModel cartModel = new CartModel();
            String products = jsonObject.getString("products");
            if (products.equals("[]")) {
                couponIcon.setVisibility(View.GONE);
                this.coupon.setText(R.string.coupon);
                this.couponValue.setText(R.string.coupn_value);
            }
            String errorWarnings = jsonObject.getString("error_warning");
            String attention = jsonObject.getString("attention");
            String weight = jsonObject.getString("weight");
            String couponStatus = jsonObject.getString("coupon_status");
            String coupon = jsonObject.getString("coupon");
            String voucherStatus = jsonObject.getString("voucher_status");
            String voucher = jsonObject.getString("voucher");
            String rewardStatus = jsonObject.getString("reward_status");
            JSONArray totlsJsonArray = new JSONArray(jsonObject.getString("totals"));
            couponModelList = new ArrayList<>();
            for (int k = 0; k < totlsJsonArray.length(); k++) {
                JSONObject jsonObject1 = totlsJsonArray.getJSONObject(k);
                String code = jsonObject1.getString("code");
                if (code.equals("sub_total")) {
                    this.totalValue.setText(jsonObject1.getString("text"));
                } else if (code.equals("coupon")) {
                    CouponModel couponModel = new CouponModel();
                    String couponTitle = jsonObject1.getString("title");
                    String couponCode = jsonObject1.getString("code");
                    String couponValue = jsonObject1.getString("value");
                    String couponText = jsonObject1.getString("text");
                    String storeId = jsonObject1.getString("store_id");
                    couponModel.setTitle(couponTitle);
                    couponModel.setCode(couponCode);
                    couponModel.setValue(couponValue);
                    couponModel.setText(couponText);
                    couponModel.setStoreId(storeId);
                    couponModelList.add(couponModel);

                }
            }
            //update the coupon field here
            if (couponModelList.size() < 1) {
                couponValue.setText(R.string.coupn_value);
                Cart.this.coupon.setText(R.string.coupon);
            } else if (couponModelList.size() == 1) {
                CouponModel couponModel = couponModelList.get(0);
                couponValue.setText(couponModel.getText());
                Cart.this.coupon.setText(couponModel.getTitle());
                couponIcon.setVisibility(View.GONE);
            } else {
                float value = 0.0f;
                for (int i = 0; i < couponModelList.size(); i++) {
                    CouponModel couponModel = couponModelList.get(i);
                    value = value + Float.parseFloat(couponModel.getValue());
                }
                couponValue.setText(String.valueOf("S$" + value));
                couponIcon.setVisibility(View.VISIBLE);
            }
            JSONArray jsonArray = new JSONArray(products);
            for (int i = 0; i < jsonArray.length(); i++) {
                com.fashion.binge.fashiondesign.models.GetCartData getCartData = new com.fashion.binge.fashiondesign.models.GetCartData();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String key = jsonObject1.getString("key");
                String thumb = jsonObject1.getString("thumb");
                String name = jsonObject1.getString("name");
                String model = jsonObject1.getString("model");
                String quantity = jsonObject1.getString("quantity");
                String stock = jsonObject1.getString("stock");
                String reward = jsonObject1.getString("reward");
                String price = jsonObject1.getString("price");
                String total = jsonObject1.getString("total");
                String productId = jsonObject1.getString("product_id");
                String sellerName = jsonObject1.getString("seller_name");
                String sellerId = jsonObject1.getString("seller_id");
                JSONArray jsonArray1 = new JSONArray(jsonObject1.getString("option"));
                for (int j = 0; j < jsonArray1.length(); j++) {
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                    String optionName = jsonObject2.getString("name");
                    if (optionName.equals("Size")) {
                        String size = jsonObject2.getString("value");
                        getCartData.setSize(size);
                    } else if (optionName.equals("Color")) {
                        String color = jsonObject2.getString("value");
                        getCartData.setColor(color);
                    }
                }
                for (int k = 0; k < couponModelList.size(); k++) {
                    CouponModel couponModel = couponModelList.get(k);
                    if (sellerId.equals(couponModel.getStoreId())) {
                        getCartData.setCouponName(couponModel.getTitle());
                        break;
                    }
                }
                getCartData.setKey(key);
                getCartData.setThumb(thumb);
                getCartData.setName(name);
                getCartData.setModel(model);
                getCartData.setQuantity(quantity);
                getCartData.setStock(stock);
                getCartData.setRewardStatus(reward);
                getCartData.setPrice(price);
                getCartData.setTotal(total);
                getCartData.setSellerName(sellerName);
                getCartData.setProductId(productId);
                cartDataList.add(getCartData);
            }
            cartModel.setErrorWarning(errorWarnings);
            cartModel.setAttention(attention);
            cartModel.setGetCartDataList(cartDataList);
            cartModel.setWeight(weight);
            cartModel.setCouponStatus(couponStatus);
            cartModel.setCoupon(coupon);
            cartModel.setVoucherStatus(voucherStatus);
            cartModel.setVoucher(voucher);
            cartModel.setRewardStatus(rewardStatus);
            cartList.setAdapter(new CartRecyclerAdapter(this, noItem, totalValue, cartModel, new UpdateCartInfoHolder() {
                @Override
                public void onCartUpdate(String key, String quantity, TextView textView, String operation) {
                    updateCart(key, textView, operation);
                }
            }, new DeleteCartInterface() {
                @Override
                public void onCartDelete(SweetAlertDialog sweetAlertDialog) {
                    setCartData(sweetAlertDialog);
                }
            }));
            if (cartList.getAdapter().getItemCount() > 0) {
                noItem.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setCartData(final SweetAlertDialog sweetAlertDialog) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        new GetCartData(this).getCartDatas(sweetAlertDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
            @Override
            public void onResponse(String status, String message) {
                if (status.equals("401")) {
                    sweetAlertDialog.dismissWithAnimation();
                    Utils.setTokenInfo(Cart.this, sweetAlertDialog, new AccessTokenInfoHolder() {
                        @Override
                        public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                            setCartData(sweetAlertDialog);
                        }
                    });
                } else {
                    if (status.equals("true")) {
                        prepareCartList(message);
                        switch (sweetAlertDialog.getTitleText()) {
                            case "Loading data":
                                sweetAlertDialog.dismissWithAnimation();
                                break;
                            case "Adding coupon":
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Successfully Added coupon");
                                promoCode.setText("");
                                break;
                            default:
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setTitleText("Successfully updated cart");
                                break;
                        }
                    } else if (status.equals("false")) {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setTitleText(message);
                    }
                }
            }
        });
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onPause(){
        super.onPause();
        if (appClose()){
            mixpanel.track("CartSession");
        }
    }

    public boolean appClose(){
        return true;
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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.checkout:
                mixpanel.track("CartToCheckout",null);
                Intent intent = new Intent(this, Checkout.class);
                startActivity(intent);
                break;
            case R.id.plus:
                final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitleText("Adding coupon");
                sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                sweetAlertDialog.show();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                new AddPromoCode(this).addPromoCode(sweetAlertDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), promoCode.getText().toString(), new ResponseInfoHolder() {
                    @Override
                    public void setFollowingInfo(String success, String data) {
                        if (success.equals("true")) {
                            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                            setCartData(sweetAlertDialog);
                        } else {
                            sweetAlertDialog.setTitleText("Error Adding coupon");
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        }
                    }
                });
                break;
            case R.id.continue_shopping:
                Intent homepageIntent = new Intent(Cart.this, Homepage.class);
                homepageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homepageIntent);
                mixpanel.track("Cart : continue shopping",null);
                break;
            case R.id.coupon_icon:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = View.inflate(this, R.layout.coupon_list_dialog, null);
                RecyclerView couponList = (RecyclerView) view.findViewById(R.id.coupon_list);
                Button ok = (Button) view.findViewById(R.id.ok);
                couponList.setHasFixedSize(false);
                couponList.setLayoutManager(new LinearLayoutManager(this));
                couponList.setAdapter(new CouponListAdapter(this, couponModelList));
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                break;
        }
    }

    private void updateCart(String key, final TextView textView, final String operation) {
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Updating cart");
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        sweetAlertDialog.show();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put(key, textView.getText().toString());
            jsonObject.put("quantity", jsonObject1);
            new UpdateCart(this).updateCart(sweetAlertDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), jsonObject, new JsonResponseHolder() {
                @Override
                public void onResponse(String status, String message) {
                    if (status.equals("true")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SharedPrefrenceInfo.CART_COUNT, message);
                        editor.apply();
                        sweetAlertDialog.setTitleText("Updating cart");
                        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                        setCartData(sweetAlertDialog);
                    } else {
                        if (operation.equals("increase")) {
                            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) - 1));
                        } else {
                            textView.setText(String.valueOf(Integer.parseInt(textView.getText().toString()) + 1));
                        }
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setTitleText("Cannot update cart");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
