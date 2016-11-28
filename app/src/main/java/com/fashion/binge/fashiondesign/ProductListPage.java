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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edmodo.rangebar.RangeBar;
import com.fashion.binge.fashiondesign.adapters.ProductListGridViewAdapter;
import com.fashion.binge.fashiondesign.adapters.ProductListListViewAdapter;
import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;
import com.fashion.binge.fashiondesign.classes.SpaceItemGrid1Decoration;
import com.fashion.binge.fashiondesign.classes.SpaceItemList1Decoration;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.interfaces.GetProductByShopInterface;
import com.fashion.binge.fashiondesign.json.GetAllProductList;
import com.fashion.binge.fashiondesign.models.GetProductByShopModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

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
public class ProductListPage extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private RecyclerView gridRecyclerView, listRecycleView;
    private LinearLayout colorLinearLayout, categoryLinearLayout, sizeLinearLayout;
    private String id;
    private TextView toolbarTitle;
    private ImageView filterImage, stackImage, gridImage;
    private List<JSONArray> jsonArrayList;
    private List<String> selectedColorState = new ArrayList<>();
    private List<String> selectedSizeState = new ArrayList<>();
    private List<String> selectedCategoryState = new ArrayList<>();
    private List<String> categoryList = new ArrayList<>();
    private List<GetProductByShopModel> getProductByShopModelList;
    private List<GetProductByShopModel> tempGetProductByShopModelList;
    private double minimumPrice;
    private double maximumPrice;
    private TextView minPrice, maxPrice;
    private RangeBar rangeBar;
    private List<Double> priceCollections;
    private int range = 0;
    private int tempMinimumPrice, tempMaximumPrice;
    private LinearLayout stickyHeader;
    private ImageView cart, notification;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        setContentView(R.layout.product_list);
        if (!Homepage.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        initialiseLayout();
        initialiseListeners();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SetStatusBarColor.setStausBarColor(this);
        toolbarTitle.setText(name);
        stickyHeader.setVisibility(View.GONE);
        prepareGridRecycleView();
        prepareListRecycleView();
        /**
         * reset value of request swippable category
         */
        Utils.requestSwippableCategory = 1;
    }

    private void initialiseLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        gridRecyclerView = (RecyclerView) findViewById(R.id.grid_recycle_view);
        listRecycleView = (RecyclerView) findViewById(R.id.list_recycle_view);
        filterImage = (ImageView) findViewById(R.id.filter_image);
        stackImage = (ImageView) findViewById(R.id.stack_image);
        gridImage = (ImageView) findViewById(R.id.grid_image);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        stickyHeader = (LinearLayout) findViewById(R.id.sticky_header);
        cart = (ImageView) findViewById(R.id.cart);
        notification = (ImageView) findViewById(R.id.notification);
    }

    private void initialiseListeners() {
        filterImage.setOnClickListener(this);
        stackImage.setOnClickListener(this);
        gridImage.setOnClickListener(this);
        cart.setOnClickListener(this);
        notification.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.filter_image:
                prepareFilterDialog();
                break;
            case R.id.stack_image:
                int currentVisiblePosition1 = ((GridLayoutManager) gridRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                gridRecyclerView.setVisibility(View.GONE);
                listRecycleView.setVisibility(View.VISIBLE);
                ((LinearLayoutManager) listRecycleView.getLayoutManager()).scrollToPositionWithOffset(currentVisiblePosition1, 0);
                break;
            case R.id.grid_image:
                int currentVisiblePosition = ((LinearLayoutManager) listRecycleView.getLayoutManager()).findFirstVisibleItemPosition();
                gridRecyclerView.setVisibility(View.VISIBLE);
                listRecycleView.setVisibility(View.GONE);
                ((LinearLayoutManager) gridRecyclerView.getLayoutManager()).scrollToPositionWithOffset(currentVisiblePosition, 0);
                break;
            case R.id.cart:
                Intent intent = new Intent(ProductListPage.this, Cart.class);
                startActivity(intent);
                break;
            case R.id.notification:
                Intent notificationIntent = new Intent(this, Notification.class);
                startActivity(notificationIntent);
                break;
        }
    }

    private void loadmoreData() {
        SpaceItemGrid1Decoration itemDecoration = new SpaceItemGrid1Decoration(10);
        gridRecyclerView.setClipToPadding(false);
        gridRecyclerView.addItemDecoration(itemDecoration);
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText("Loading data");
        pDialog.setCancelable(true);
        pDialog.show();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        new GetAllProductList(ProductListPage.this).getProductListContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), id, new GetProductByShopInterface() {
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
            public void setSuccessInfo(JSONArray jsonArray, List<String> id, List<String> name, List<String> image, List<String> price, List<String> wishlist, List<String> internationalShipping,List<String> category, List<List<String>> colorList, List<List<String>> sizeList, List<List<String>> categoryList, List<JSONArray> jsonArrayList, String totalProduct) {
                pDialog.dismissWithAnimation();
                ProductListPage.this.categoryList.addAll(category);
                ProductListPage.this.jsonArrayList = jsonArrayList;
                for (int i = 0; i < id.size(); i++) {
                    GetProductByShopModel getProductByShopModel = new GetProductByShopModel();
                    getProductByShopModel.setId(id.get(i));
                    getProductByShopModel.setName(name.get(i));
                    getProductByShopModel.setImage(image.get(i));
                    getProductByShopModel.setPrice(price.get(i));
                    getProductByShopModel.setInternationShipping(internationalShipping.get(i));
                    getProductByShopModel.setCategoryList(categoryList.get(i));
                    getProductByShopModel.setColorList(colorList.get(i));
                    getProductByShopModel.setSizeList(sizeList.get(i));
                    try {
                        getProductByShopModel.setJsonId(jsonArray.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getProductByShopModelList.add(getProductByShopModel);
                }
                tempGetProductByShopModelList.addAll(getProductByShopModelList);
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
                if (minPrice==null) {
                    filterList(selectedColorState, selectedSizeState, selectedCategoryState, String.valueOf(minimumPrice), String.valueOf(maximumPrice));
                }else{
                    filterList(selectedColorState, selectedSizeState, selectedCategoryState, minPrice.getText().toString().substring(1), maxPrice.getText().toString().substring(1));
                }
            }
        });
    }

    private void prepareGridRecycleView() {
        SpaceItemGrid1Decoration itemDecoration = new SpaceItemGrid1Decoration(10);
        gridRecyclerView.setClipToPadding(false);
        gridRecyclerView.addItemDecoration(itemDecoration);
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText("Loading data");
        pDialog.setCancelable(true);
        pDialog.show();
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        new GetAllProductList(ProductListPage.this).getProductListContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), id, new GetProductByShopInterface() {
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
            public void setSuccessInfo(JSONArray jsonArray, List<String> id, List<String> name, List<String> image, List<String> price, List<String> wishlist, List<String> internationalShipping,List<String> category, List<List<String>> colorList, List<List<String>> sizeList, List<List<String>> categoryList, List<JSONArray> jsonArrayList, String totalProduct) {
                pDialog.dismissWithAnimation();
                getProductByShopModelList = new ArrayList<>();
                tempGetProductByShopModelList = new ArrayList<>();
                ProductListPage.this.categoryList.addAll(category);
                ProductListPage.this.jsonArrayList = jsonArrayList;
                for (int i = 0; i < id.size(); i++) {
                    GetProductByShopModel getProductByShopModel = new GetProductByShopModel();
                    getProductByShopModel.setId(id.get(i));
                    getProductByShopModel.setName(name.get(i));
                    getProductByShopModel.setImage(image.get(i));
                    getProductByShopModel.setPrice(price.get(i));
                    getProductByShopModel.setInternationShipping(internationalShipping.get(i));
                    getProductByShopModel.setCategoryList(categoryList.get(i));
                    getProductByShopModel.setColorList(colorList.get(i));
                    getProductByShopModel.setSizeList(sizeList.get(i));
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

    private void prepareGridRecyclerViewADapter(final List<GetProductByShopModel> getProductByShopModelList) {
        ProductListGridViewAdapter adapter = new ProductListGridViewAdapter(ProductListPage.this, getProductByShopModelList);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        gridRecyclerView.setLayoutManager(manager);
        gridRecyclerView.setAdapter(adapter);
        gridRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager gridLayoutManager = ((GridLayoutManager) gridRecyclerView.getLayoutManager());
                if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == getProductByShopModelList.size() - 1) {
                    Utils.requestSwippableCategory = Utils.requestSwippableCategory + 20;
                    loadmoreData();
                }
            }
        });
    }

    private void prepareListRecyclerViewADapter(final List<GetProductByShopModel> getProductByShopModelList) {
        ProductListListViewAdapter listViewAdapter = new ProductListListViewAdapter(ProductListPage.this, getProductByShopModelList);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        listRecycleView.setLayoutManager(manager);
        listRecycleView.setAdapter(listViewAdapter);
        listRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) listRecycleView.getLayoutManager());
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == getProductByShopModelList.size() - 1) {
                    Utils.requestSwippableCategory = Utils.requestSwippableCategory + 20;
                    loadmoreData();
                }
            }
        });
    }

    private void prepareFilterDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProductListPage.this);
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
        //don't show the category linear layout if it has come from the category menu in filter option
        categoryLinearLayout.setVisibility(View.GONE);
        TextView categoryText = (TextView) view.findViewById(R.id.category_text);
        categoryText.setVisibility(View.GONE);
        sizeLinearLayout = (LinearLayout) view.findViewById(R.id.size_view_child);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button filter = (Button) view.findViewById(R.id.filter);
        TextView notAvailable = (TextView) view.findViewById(R.id.not_available);
        TextView sizeNotAvailable = (TextView) view.findViewById(R.id.size_not_available);
        TextView categoryNotAvailable = (TextView) view.findViewById(R.id.category_not_available);
        if (colorList.size() == 0) {
            notAvailable.setVisibility(View.VISIBLE);
        }
        if (sizeList.size() == 0) {
            sizeNotAvailable.setVisibility(View.VISIBLE);
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
        SpaceItemList1Decoration itemDecoration = new SpaceItemList1Decoration(10);
        listRecycleView.setClipToPadding(false);
        listRecycleView.addItemDecoration(itemDecoration);
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
                view.setBackground(ContextCompat.getDrawable(ProductListPage.this, R.drawable.circletextview));
                GradientDrawable shapeDrawable = (GradientDrawable) view.getBackground();
                shapeDrawable.setColor(ContextCompat.getColor(ProductListPage.this, R.color.colorPrimary));
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
                view.setBackground(ContextCompat.getDrawable(ProductListPage.this, R.drawable.circletextview));
                GradientDrawable shapeDrawable = (GradientDrawable) view.getBackground();
                shapeDrawable.setColor(ContextCompat.getColor(ProductListPage.this, R.color.colorPrimary));
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
                    textView.setBackground(ContextCompat.getDrawable(ProductListPage.this, R.drawable.circletextview));
                    GradientDrawable shapeDrawable = (GradientDrawable) textView.getBackground();
                    shapeDrawable.setColor(ContextCompat.getColor(ProductListPage.this, R.color.colorPrimary));
                    textView.setId(-1);
                }
            }
        }
        for (int i = 0; i < categoryLinearLayout.getChildCount(); i++) {
            TextView textView = (TextView) categoryLinearLayout.getChildAt(i);
            for (int j = 0; j < selectedCategoryState.size(); j++) {
                if (selectedCategoryState.get(j).equals(textView.getTag().toString())) {
                    textView.setTextColor(Color.WHITE);
                    textView.setBackground(ContextCompat.getDrawable(ProductListPage.this, R.drawable.circletextview));
                    GradientDrawable shapeDrawable = (GradientDrawable) textView.getBackground();
                    shapeDrawable.setColor(ContextCompat.getColor(ProductListPage.this, R.color.colorPrimary));
                    textView.setId(-1);
                }
            }
        }
        rangeBar.setThumbIndices((int) minimumPrice, (int) maximumPrice);
        if (range == 0) {
            rangeBar.setTickCount((int) (maximumPrice - minimumPrice));
            range = (int) (maximumPrice - minimumPrice);
        } else {
            rangeBar.setTickCount(range);
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
}