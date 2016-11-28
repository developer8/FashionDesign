package com.fashion.binge.fashiondesign;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edmodo.rangebar.RangeBar;
import com.fashion.binge.fashiondesign.adapters.ShopPageGridViewAdapter;
import com.fashion.binge.fashiondesign.adapters.ShopPageListViewAdapter;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.SpaceItemGridDecoration;
import com.fashion.binge.fashiondesign.classes.SpaceItemListDecoration;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.GetProductByShopInterface;
import com.fashion.binge.fashiondesign.interfaces.GetShopByIDInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderFilterInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderGridInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderHelpInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderListInterface;
import com.fashion.binge.fashiondesign.json.GetProductByShop;
import com.fashion.binge.fashiondesign.json.GetShopById;
import com.fashion.binge.fashiondesign.models.GetProductByShopModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ConstantConditions")
public class ShopPageContent extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private RecyclerView gridRecyclerView, listRecycleView;
    private ShopPageGridViewAdapter adapter;
    private RelativeLayout recyclerParent;
    private RelativeLayout recyclerListParent;
    private LinearLayout colorLinearLayout, categoryLinearLayout, sizeLinearLayout;
    private GridLayoutManager manager;
    private ImageView gridImage;
    private ImageView stackImage;
    private String shopBanner, id, sellerName, description, avatar, countryFlag, countryName;
    private TextView sellerNameText;
    private ImageView help;
    private ImageView filterImage;
    private List<JSONArray> jsonArrayList;
    private List<String> selectedColorState = new ArrayList<>();
    private List<String> selectedSizeState = new ArrayList<>();
    private List<String> selectedCategoryState = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private List<GetProductByShopModel> getProductByShopModelList;
    private List<GetProductByShopModel> tempGetProductByShopModelList;
    private Button follow;
    private String followingText;
    private CircleImageView profileImageView;
    private double minimumPrice;
    private double maximumPrice;
    private TextView minPrice, maxPrice;
    private RangeBar rangeBar;
    private List<Double> priceCollections;
    private int range = 0;
    private int tempMinimumPrice, tempMaximumPrice;
    private ImageView cart, notification;
    private TextView cartNumber, notificationNumber;
    private FloatingActionButton floatingActionButton;
    String projectToken;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopBanner = getIntent().getStringExtra("shop_banner");
        id = getIntent().getStringExtra("id");
        sellerName = getIntent().getStringExtra("seller_name");
        followingText = getIntent().getStringExtra("following_text");
        avatar = getIntent().getStringExtra("avatar");
        countryFlag = getIntent().getStringExtra("seller_flag");
        countryName = getIntent().getStringExtra("seller_country");
        setContentView(R.layout.shop_page_content);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        initialiseListeners();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);

        projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
        mixpanel = MixpanelAPI.getInstance(ShopPageContent.this,projectToken);
        mixpanel.timeEvent("ShopDetailSession");

        //toolbarTitle.setText(sellerName);
        Glide.with(this)
                .load(JSONUrl.IMAGE_BASE_URL + this.avatar)
                .into(profileImageView);
        prepareGridRecycleView();
        prepareListRecycleView();
        recyclerParent.setBackgroundColor(Color.BLACK);
        sellerNameText.setText(sellerName);
        gridImage.setColorFilter(Color.parseColor("#999999"));
    }

    private void initialiseLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        gridRecyclerView = (RecyclerView) findViewById(R.id.grid_recycle_view);
        listRecycleView = (RecyclerView) findViewById(R.id.list_recycle_view);
        recyclerParent = (RelativeLayout) findViewById(R.id.recycler_parent);
        recyclerListParent = (RelativeLayout) findViewById(R.id.recycler_parent);
        gridImage = (ImageView) findViewById(R.id.grid_image);
        stackImage = (ImageView) findViewById(R.id.stack_image);
        sellerNameText = (TextView) findViewById(R.id.seller_name);
        help = (ImageView) findViewById(R.id.help);
        filterImage = (ImageView) findViewById(R.id.filter_image);
        follow = (Button) findViewById(R.id.follow);
        profileImageView = (CircleImageView) findViewById(R.id.profile_image);
        cart = (ImageView) findViewById(R.id.cart);
        notification = (ImageView) findViewById(R.id.notification);
        cartNumber = (TextView) findViewById(R.id.cart_number);
        notificationNumber = (TextView) findViewById(R.id.notification_number);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
    }

    private void initialiseListeners() {
        gridImage.setOnClickListener(this);
        stackImage.setOnClickListener(this);
        help.setOnClickListener(this);
        filterImage.setOnClickListener(this);
        cart.setOnClickListener(this);
        notification.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.grid_image:
                gridImage.setColorFilter(Color.parseColor("#999999"));
                stackImage.setColorFilter(Color.parseColor("#00999999"));
                int currentVisiblePosition = ((LinearLayoutManager) listRecycleView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                gridRecyclerView.setVisibility(View.VISIBLE);
                listRecycleView.setVisibility(View.INVISIBLE);
                if (recyclerListParent.getVisibility() == View.VISIBLE) {
                    recyclerParent.setVisibility(View.VISIBLE);
                } else {
                    recyclerParent.setVisibility(View.INVISIBLE);
                }
                ((LinearLayoutManager) gridRecyclerView.getLayoutManager()).scrollToPositionWithOffset(currentVisiblePosition, 0);
                break;


            case R.id.stack_image:
                stackImage.setColorFilter(Color.parseColor("#999999"));
                gridImage.setColorFilter(Color.parseColor("#00999999"));
                int currentVisiblePosition1 = ((GridLayoutManager) gridRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                gridRecyclerView.setVisibility(View.INVISIBLE);
                listRecycleView.setVisibility(View.VISIBLE);
                if (recyclerParent.getVisibility() == View.VISIBLE) {
                    recyclerListParent.setVisibility(View.VISIBLE);
                } else {
                    recyclerListParent.setVisibility(View.INVISIBLE);
                }
                ((LinearLayoutManager) listRecycleView.getLayoutManager()).scrollToPositionWithOffset(currentVisiblePosition1, 0);
                break;
            case R.id.help:
                prepareDescriptionDialog();
                break;
            case R.id.filter_image:
                prepareFilterDialog();
                break;
            case R.id.follow:
                break;
            case R.id.cart:
                Intent intent = new Intent(ShopPageContent.this, Cart.class);
                startActivity(intent);
                break;
            case R.id.notification:
                Intent notificationIntent = new Intent(this, Notification.class);
                startActivity(notificationIntent);
                break;
            case R.id.fab:
                Intent categorySearchIntent=new Intent(this,CategorySearch.class);
                startActivity(categorySearchIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
                break;
        }
    }

    private void prepareGridRecycleView() {
        gridRecyclerView.setHasFixedSize(true);
        SpaceItemGridDecoration itemDecoration = new SpaceItemGridDecoration(10);
        gridRecyclerView.setClipToPadding(false);
        gridRecyclerView.addItemDecoration(itemDecoration);
        gridRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int positionView = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                View view = recyclerView.getChildAt(0);
                if (view != null && recyclerView.getChildAdapterPosition(view) == 0) {
                    view.setTranslationY((float) (-view.getTop() / 6));
                }
                if (dy > 0) {
                    if (positionView > 0) {
                        recyclerParent.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (positionView == 0) {
                        recyclerParent.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        prepareData();
    }

    private void prepareData() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText("Loading data");
        pDialog.setCancelable(true);
        pDialog.show();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        new GetShopById(this).requestgetShopByIdJson(pDialog, id, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new GetShopByIDInterface() {
            @Override
            public void setErrorInfo() {

            }

            @Override
            public void setSuccessInfo(String description) {
                if (description.equals("401")) {
                    Utils.setTokenInfo(ShopPageContent.this, pDialog, new AccessTokenInfoHolder() {
                        @Override
                        public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                            prepareData();
                        }
                    });
                } else {
                    ShopPageContent.this.description = description;
                    new GetProductByShop(ShopPageContent.this).requestgetProductByShopJson(pDialog, id, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new GetProductByShopInterface() {
                        @Override
                        public void setErrorInfo(String message) {
                            pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setTitleText(message);
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void setSuccessInfo(JSONArray jsonArray, List<String> id, List<String> name, List<String> image, List<String> price, List<String> wishlist, List<String> internationShipping,List<String> category, List<List<String>> colorList, List<List<String>> sizeList, List<List<String>> categoryList, List<JSONArray> jsonArrayList,String totalProduct) {
                            getProductByShopModelList = new ArrayList<>();
                            tempGetProductByShopModelList = new ArrayList<>();
                            ShopPageContent.this.categoryList.addAll(category);
                            ShopPageContent.this.jsonArrayList = jsonArrayList;
                            for (int i = 0; i < id.size(); i++) {
                                GetProductByShopModel getProductByShopModel = new GetProductByShopModel();
                                getProductByShopModel.setId(id.get(i));
                                getProductByShopModel.setName(name.get(i));
                                getProductByShopModel.setImage(image.get(i));
                                getProductByShopModel.setPrice(price.get(i));
                                getProductByShopModel.setInternationShipping(internationShipping.get(i));
                                getProductByShopModel.setCategoryList(categoryList.get(i));
                                getProductByShopModel.setColorList(colorList.get(i));
                                getProductByShopModel.setSizeList(sizeList.get(i));
                                getProductByShopModel.setWishlist(wishlist.get(i));
                                try {
                                    getProductByShopModel.setJsonId(jsonArray.get(i).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                getProductByShopModelList.add(getProductByShopModel);
                            }
                            tempGetProductByShopModelList.addAll(getProductByShopModelList);
                            prepareGridRecyclerViewADapter(getProductByShopModelList);
                            prepareListRecyclerViewADapter(getProductByShopModelList);
                            priceCollections = new ArrayList<>();
                            for (int i = 0; i < getProductByShopModelList.size(); i++) {
                                GetProductByShopModel getProductByShopModel = getProductByShopModelList.get(i);
                                priceCollections.add(Double.parseDouble(getProductByShopModel.getPrice().replaceAll("[^\\d\\.]", "")));
                            }
                            //due to the limitation of range bar two is added here i.e rangebar not working
                            //well on left side and right side by two digits
                            maximumPrice = Collections.max(priceCollections) + 2;
                            minimumPrice = Collections.min(priceCollections) - 2;
                            tempMinimumPrice = (int) minimumPrice;
                            tempMaximumPrice = (int) maximumPrice;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                }
                            }, 1);
                        }
                    });
                }
            }
        });
    }

    private void prepareGridRecyclerViewADapter(List<GetProductByShopModel> getProductByShopModelList) {
        adapter = new ShopPageGridViewAdapter(ShopPageContent.this, followingText, id, gridRecyclerView, follow, getProductByShopModelList, sellerName, shopBanner, avatar, countryFlag, countryName,new ShopPageHeaderGridInterface() {
            @Override
            public void onClick(boolean isClicked) {
                int currentVisiblePosition = ((GridLayoutManager) gridRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                listRecycleView.scrollToPosition(currentVisiblePosition);
                if (recyclerParent.getVisibility() == View.VISIBLE) {
                    recyclerListParent.setVisibility(View.VISIBLE);
                } else {
                    recyclerListParent.setVisibility(View.INVISIBLE);
                }
                gridRecyclerView.setVisibility(View.INVISIBLE);
                listRecycleView.setVisibility(View.VISIBLE);
            }
        }, new ShopPageHeaderListInterface() {
            @Override
            public void onClick(boolean isClicked) {

            }
        }, new ShopPageHeaderHelpInterface() {
            @Override
            public void onClick(boolean isClicked) {
                prepareDescriptionDialog();
            }
        }, new ShopPageHeaderFilterInterface() {
            @Override
            public void onClick(boolean isClick) {
                prepareFilterDialog();
            }
        });
        manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.isPositionSubHeader(position)) {
                    return adapter.isPositionSubHeader(position) ? manager.getSpanCount() : 1;
                }
                return adapter.isPositionHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        gridRecyclerView.setAdapter(adapter);
        gridRecyclerView.setLayoutManager(manager);
    }

    private void prepareDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShopPageContent.this);
        View view = View.inflate(this, R.layout.help_dialog, null);
        TextView descriptionText = (TextView) view.findViewById(R.id.description);
        //noinspection deprecation
        descriptionText.setText(Html.fromHtml(description));
        TextView shopName = (TextView) view.findViewById(R.id.shop_name);
        shopName.setText(sellerName);
        CircleImageView circleImage = (CircleImageView) view.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(JSONUrl.IMAGE_BASE_URL + this.avatar)
                .into(circleImage);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void prepareFilterDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ShopPageContent.this);
        final View view = View.inflate(this, R.layout.filter_dialog, null);
        Set<String> colorList = new TreeSet<>();
        Set<String> sizeList = new TreeSet<>();
        Set<String> categoryList = new TreeSet<>();
        categoryList.addAll(this.categoryList);
        for (int i = 0; i < jsonArrayList.size(); i++) {
            JSONArray jsonArray = jsonArrayList.get(i);
            for (int j = 0; j < jsonArray.length(); j++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(j);
                    String name = jsonObject.getString("name");
                    if (name.equals("Color")) {
                        JSONArray jsonArray1 = new JSONArray(jsonObject.getString("option_value"));
                        for (int k = 0; k < jsonArray1.length(); k++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(k);
                            String color = jsonObject1.getString("image");
                            colorList.add(color);
                        }
                    } else if (name.equals("Size")) {
                        JSONArray jsonArray1 = new JSONArray(jsonObject.getString("option_value"));
                        for (int k = 0; k < jsonArray1.length(); k++) {
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(k);
                            String size = jsonObject1.getString("name");
                            sizeList.add(size);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        colorLinearLayout = (LinearLayout) view.findViewById(R.id.circle_view_child);
        categoryLinearLayout = (LinearLayout) view.findViewById(R.id.category_view_child);
        sizeLinearLayout = (LinearLayout) view.findViewById(R.id.size_view_child);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button filter = (Button) view.findViewById(R.id.filter);
        TextView notAvailable = (TextView) view.findViewById(R.id.not_available);
        TextView sizeNotAvailable = (TextView) view.findViewById(R.id.size_not_available);
        TextView categoryNotAvailable = (TextView) view.findViewById(R.id.category_not_available);
        TextView colorTitle = (TextView) view.findViewById(R.id.colorTitle);
        TextView sizeTitle = (TextView) view.findViewById(R.id.sizeTitle);
        if (colorList.size() == 0) {
            //changed here for color not to be displayed if not available
            colorTitle.setVisibility(View.GONE);
            notAvailable.setVisibility(View.GONE);
            colorLinearLayout.setVisibility(View.GONE);
        }
        if (sizeList.size() == 0) {
            //changed here for size not to be displayed if not available
            sizeNotAvailable.setVisibility(View.GONE);
            sizeLinearLayout.setVisibility(View.GONE);
            sizeTitle.setVisibility(View.GONE);

        }
        if (categoryList.size() == 0) {
            categoryNotAvailable.setVisibility(View.VISIBLE);
        }
        final ImageView clearAll = (ImageView) view.findViewById(R.id.clear_all);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                rotateAnimation.setDuration(500);
                clearAll.startAnimation(rotateAnimation);
                rotateAnimation.start();
                rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        resetDialog();
                        clearAll.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
        rangeBar = (RangeBar) view.findViewById(R.id.rangebar);
        minPrice = (TextView) view.findViewById(R.id.min_price);
        maxPrice = (TextView) view.findViewById(R.id.max_price);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
                minimumPrice = leftThumbIndex;
                minPrice.setText(String.format("$%d", leftThumbIndex + tempMinimumPrice));
                maximumPrice = rightThumbIndex;
                maxPrice.setText(String.format("$%d", rightThumbIndex + tempMinimumPrice));
            }
        });
        prepareColorImage(colorList.size(), colorList);
        prepareCategoryText(categoryList.size(), categoryList);
        prepareSizeTextView(sizeList.size(), sizeList);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterList(selectedColorState, selectedSizeState, selectedCategoryState, minPrice.getText().toString().substring(1), maxPrice.getText().toString().substring(1));
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        restoreDialogState();
    }

    private void filterList(List<String> selectedColorState, List<String> selectedSizeState, List<String> selectedCategoryState, String minPrice, String maxPrice) {
        Set<String> selectedColorSet = new HashSet<>();
        selectedColorSet.addAll(selectedColorState);
        Set<String> selectedSizeSet = new HashSet<>();
        selectedSizeSet.addAll(selectedSizeState);
        Set<String> selectedCategorySet = new HashSet<>();
        selectedCategorySet.addAll(selectedCategoryState);
        getProductByShopModelList.clear();
        for (GetProductByShopModel getProductByShopModel : tempGetProductByShopModelList) {
            Float price = Float.parseFloat(getProductByShopModel.getPrice().substring(2));
            Set<String> tempColorSet = new HashSet<>();
            Set<String> tempSizeSet = new HashSet<>();
            Set<String> tempCategorySet = new HashSet<>();
            tempColorSet.addAll(getProductByShopModel.getColorList());
            tempSizeSet.addAll(getProductByShopModel.getSizeList());
            tempCategorySet.addAll(getProductByShopModel.getCategoryList());
            tempColorSet.retainAll(selectedColorSet);
            tempSizeSet.retainAll(selectedSizeSet);
            tempCategorySet.retainAll(selectedCategorySet);
            if (price >= Float.parseFloat(minPrice) && price <= Float.parseFloat(maxPrice) &&
                    (tempColorSet.size() > 0 || selectedColorSet.size() == 0) &&
                    (tempSizeSet.size() > 0 || selectedSizeSet.size() == 0) &&
                    (tempCategorySet.size() > 0 || selectedCategorySet.size() == 0)) {
                getProductByShopModelList.add(getProductByShopModel);
            }
        }
        gridRecyclerView.getAdapter().notifyDataSetChanged();
        listRecycleView.getAdapter().notifyDataSetChanged();
    }

    private void prepareListRecycleView() {
        listRecycleView.setHasFixedSize(false);
        SpaceItemListDecoration itemDecoration = new SpaceItemListDecoration(10);
        listRecycleView.setClipToPadding(false);
        listRecycleView.addItemDecoration(itemDecoration);
        listRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int positionView = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                View view = recyclerView.getChildAt(0);
                if (view != null && recyclerView.getChildAdapterPosition(view) == 0) {
                    view.setTranslationY(-view.getTop() / 4);
                }
                if (dy > 0) {
                    if (positionView > 0) {
                        recyclerListParent.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (positionView == 0) {
                        recyclerListParent.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

    }

    private void prepareListRecyclerViewADapter(List<GetProductByShopModel> getProductByShopModelList) {
        ShopPageListViewAdapter listViewAdapter = new ShopPageListViewAdapter(ShopPageContent.this, followingText, follow, countryName,id, getProductByShopModelList, sellerName, shopBanner, this.avatar, new ShopPageHeaderGridInterface() {
            @Override
            public void onClick(boolean isClicked) {
            }
        }, new ShopPageHeaderListInterface() {
            @Override
            public void onClick(boolean isClicked) {
                int currentVisiblePosition = ((LinearLayoutManager) listRecycleView.getLayoutManager()).findFirstVisibleItemPosition();
                gridRecyclerView.setVisibility(View.VISIBLE);
                listRecycleView.setVisibility(View.INVISIBLE);
                if (recyclerListParent.getVisibility() == View.VISIBLE) {
                    recyclerParent.setVisibility(View.VISIBLE);
                } else {
                    recyclerParent.setVisibility(View.INVISIBLE);
                }
                ((LinearLayoutManager) gridRecyclerView.getLayoutManager()).scrollToPositionWithOffset(currentVisiblePosition, 0);
            }
        }, new ShopPageHeaderHelpInterface() {
            @Override
            public void onClick(boolean isClicked) {
                prepareDescriptionDialog();
            }
        }, new ShopPageHeaderFilterInterface() {
            @Override
            public void onClick(boolean isClick) {
                prepareFilterDialog();
            }
        });
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        listRecycleView.setLayoutManager(manager);
        listRecycleView.setAdapter(listViewAdapter);
    }


    private void prepareColorImage(int size, Set<String> colors) {
        for (int i = 0; i < size; i++) {
            List<String> colorTempList = new ArrayList<>();
            colorTempList.addAll(colors);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    50, 50);
            final FrameLayout frameLayout = new FrameLayout(this);
            layoutParams.setMargins(0, 0, 25, 0);
            frameLayout.setLayoutParams(layoutParams);
            final CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
            circleImageView.setBorderColor(Color.LTGRAY);
            circleImageView.setBorderWidth(1);
            Glide.with(this)
                    .load(colorTempList.get(i))
                    .into(circleImageView);
            frameLayout.setTag(colorTempList.get(i));
            final TextView selectedTextView = new TextView(this);
            selectedTextView.setBackground(ContextCompat.getDrawable(this, R.drawable.circletextview));
            GradientDrawable selectedShapeDrawable = (GradientDrawable) selectedTextView.getBackground();
            selectedShapeDrawable.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
            selectedShapeDrawable.setStroke(3, Color.WHITE);
            FrameLayout.LayoutParams rp = new FrameLayout.LayoutParams(20, 20);
            rp.setMargins(50, 50, 0, 0);
            selectedTextView.setGravity(Gravity.CENTER);
            selectedTextView.setVisibility(View.INVISIBLE);
            selectedTextView.setId(R.id.automatic);
            selectedTextView.setLayoutParams(rp);
            ((FrameLayout.LayoutParams) selectedTextView.getLayoutParams()).gravity = Gravity.BOTTOM | Gravity.END;
            frameLayout.addView(circleImageView);
            frameLayout.addView(selectedTextView);
            colorLinearLayout.addView(frameLayout);
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedTextView.getVisibility() == View.INVISIBLE) {
                        selectedTextView.setVisibility(View.VISIBLE);
                        selectedColorState.add(frameLayout.getTag().toString());
                    } else {
                        selectedTextView.setVisibility(View.INVISIBLE);
                        selectedColorState.remove(frameLayout.getTag().toString());
                    }
                }
            });
        }
    }

    private void prepareSizeTextView(int size, Set<String> sizeList) {
        for (int i = 0; i < size; i++) {
            List<String> sizeTempList = new ArrayList<>();
            sizeTempList.addAll(sizeList);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 0, 5, 0);
            final TextView circleTextView = new TextView(this);
            circleTextView.setLayoutParams(layoutParams);
            circleTextView.setPadding(40, 10, 40, 10);
            circleTextView.setText(sizeTempList.get(i));
            circleTextView.setTag(sizeTempList.get(i));
            circleTextView.setId(0);
            circleTextView.setGravity(Gravity.CENTER);
            circleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBackground(circleTextView, "size");
                }
            });
            sizeLinearLayout.addView(circleTextView);
        }
    }

    private void prepareCategoryText(int size, Set<String> category) {
        for (int i = 0; i < size; i++) {
            List<String> categoryTempList = new ArrayList<>();
            categoryTempList.addAll(category);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 0, 5, 0);
            final TextView circleTextView = new TextView(this);
            circleTextView.setId(0);
            circleTextView.setLayoutParams(layoutParams);
            circleTextView.setPadding(40, 10, 40, 10);
            circleTextView.setText(categoryTempList.get(i));
            circleTextView.setTag(categoryTempList.get(i));
            categoryLinearLayout.addView(circleTextView);
            circleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setBackground(circleTextView, "category");
                }
            });
        }
    }

    private void setBackground(View view, String type) {
        TextView textView = (TextView) view;
        if (type.equals("size")) {
            if (textView.getId() == 0) {
                textView.setTextColor(Color.WHITE);
                view.setBackground(ContextCompat.getDrawable(ShopPageContent.this, R.drawable.circletextview));
                GradientDrawable shapeDrawable = (GradientDrawable) view.getBackground();
                shapeDrawable.setColor(ContextCompat.getColor(ShopPageContent.this, R.color.colorPrimary));
                textView.setId(-1);
                selectedSizeState.add(textView.getTag().toString());
            } else {
                textView.setTextColor(Color.parseColor("#67757f"));
                textView.setBackground(null);
                textView.setId(0);
                selectedSizeState.remove(textView.getTag().toString());
            }
        } else if (type.equals("category")) {
            if (textView.getId() == 0) {
                textView.setTextColor(Color.WHITE);
                view.setBackground(ContextCompat.getDrawable(ShopPageContent.this, R.drawable.circletextview));
                GradientDrawable shapeDrawable = (GradientDrawable) view.getBackground();
                shapeDrawable.setColor(ContextCompat.getColor(ShopPageContent.this, R.color.colorPrimary));
                textView.setId(-1);
                selectedCategoryState.add(textView.getTag().toString());
            } else {
                textView.setTextColor(Color.parseColor("#67757f"));
                textView.setBackground(null);
                textView.setId(0);
                selectedCategoryState.remove(textView.getTag().toString());
            }
        }
    }

    private void restoreDialogState() {
        for (int i = 0; i < colorLinearLayout.getChildCount(); i++) {
            FrameLayout frameLayout = (FrameLayout) colorLinearLayout.getChildAt(i);
            TextView selectedState = (TextView) frameLayout.findViewById(R.id.automatic);
            for (int j = 0; j < selectedColorState.size(); j++) {
                if (selectedColorState.get(j).equals(frameLayout.getTag().toString())) {
                    selectedState.setVisibility(View.VISIBLE);
                }
            }
        }
        for (int i = 0; i < sizeLinearLayout.getChildCount(); i++) {
            TextView textView = (TextView) sizeLinearLayout.getChildAt(i);
            for (int j = 0; j < selectedSizeState.size(); j++) {
                if (selectedSizeState.get(j).equals(textView.getTag().toString())) {
                    textView.setTextColor(Color.WHITE);
                    textView.setBackground(ContextCompat.getDrawable(ShopPageContent.this, R.drawable.circletextview));
                    GradientDrawable shapeDrawable = (GradientDrawable) textView.getBackground();
                    shapeDrawable.setColor(ContextCompat.getColor(ShopPageContent.this, R.color.colorPrimary));
                    textView.setId(-1);
                }
            }
        }
        for (int i = 0; i < categoryLinearLayout.getChildCount(); i++) {
            TextView textView = (TextView) categoryLinearLayout.getChildAt(i);
            for (int j = 0; j < selectedCategoryState.size(); j++) {
                if (selectedCategoryState.get(j).equals(textView.getTag().toString())) {
                    textView.setTextColor(Color.WHITE);
                    textView.setBackground(ContextCompat.getDrawable(ShopPageContent.this, R.drawable.circletextview));
                    GradientDrawable shapeDrawable = (GradientDrawable) textView.getBackground();
                    shapeDrawable.setColor(ContextCompat.getColor(ShopPageContent.this, R.color.colorPrimary));
                    textView.setId(-1);
                }
            }
        }
        try {
            rangeBar.setThumbIndices((int) minimumPrice, (int) maximumPrice);
            if (range == 0) {
                rangeBar.setTickCount((int) (maximumPrice - minimumPrice));
                range = (int) (maximumPrice - minimumPrice);
            } else {
                rangeBar.setTickCount(range);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    private void resetDialog() {
        selectedColorState.clear();
        selectedCategoryState.clear();
        selectedSizeState.clear();
        try {
            rangeBar.invalidate();
            minPrice.setText(String.format("$%d", tempMinimumPrice));
            maxPrice.setText(String.format("$%d", tempMaximumPrice));
            rangeBar.setThumbIndices(0, (tempMaximumPrice - tempMinimumPrice - 2));
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        for (int i = 0; i < colorLinearLayout.getChildCount(); i++) {
            FrameLayout frameLayout = (FrameLayout) colorLinearLayout.getChildAt(i);
            TextView selectedState = (TextView) frameLayout.findViewById(R.id.automatic);
            selectedState.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < sizeLinearLayout.getChildCount(); i++) {
            TextView textView = (TextView) sizeLinearLayout.getChildAt(i);
            textView.setTextColor(Color.parseColor("#67757f"));
            textView.setBackground(null);
            textView.setId(0);
        }
        for (int i = 0; i < categoryLinearLayout.getChildCount(); i++) {
            TextView textView = (TextView) categoryLinearLayout.getChildAt(i);
            textView.setTextColor(Color.parseColor("#67757f"));
            textView.setBackground(null);
            textView.setId(0);
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
    protected void onPause(){
        super.onPause();
        if(appClose()){
            mixpanel.track("ShopDetailSession");
        }
    }

    public boolean appClose(){
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mixpanel.timeEvent("ShopDetailSession");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> id = sharedPreferences.getStringSet(SharedPrefrenceInfo.IDSET, new HashSet<String>());
        int size = id.size();
        int count = Integer.parseInt(sharedPreferences.getString(SharedPrefrenceInfo.NOTIFICATION_COUNT, "0"));
        if ((count - size) > 0) {
            notificationNumber.setVisibility(View.VISIBLE);
            notificationNumber.setText(String.valueOf(count - size));
        } else {
            notificationNumber.setVisibility(View.GONE);
        }
    }
}