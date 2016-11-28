package com.fashion.binge.fashiondesign;import android.content.Intent;import android.content.SharedPreferences;import android.content.pm.ActivityInfo;import android.graphics.Color;import android.os.Bundle;import android.preference.PreferenceManager;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.GridLayoutManager;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.Toolbar;import android.view.MenuItem;import android.view.MotionEvent;import android.view.View;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import com.fashion.binge.fashiondesign.adapters.CategorySearchGridViewAdapter;import com.fashion.binge.fashiondesign.adapters.SwippableCardAdapter;import com.fashion.binge.fashiondesign.classes.JSONUrl;import com.fashion.binge.fashiondesign.classes.SetStatusBarColor;import com.fashion.binge.fashiondesign.classes.SpaceItemGrid1Decoration;import com.fashion.binge.fashiondesign.classes.Utils;import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;import com.fashion.binge.fashiondesign.interfaces.AddToWIshListInterface;import com.fashion.binge.fashiondesign.interfaces.GetProductByShopInterface;import com.fashion.binge.fashiondesign.interfaces.JsonResponseHolder;import com.fashion.binge.fashiondesign.json.AddToWishlist;import com.fashion.binge.fashiondesign.json.GetAllProductList;import com.fashion.binge.fashiondesign.json.MultipleCategorySearch;import com.fashion.binge.fashiondesign.models.CategorySearchModel;import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;import com.lorentzos.flingswipe.SwipeFlingAdapterView;import com.mixpanel.android.mpmetrics.MixpanelAPI;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;import java.io.BufferedReader;import java.io.DataOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.InputStreamReader;import java.net.HttpURLConnection;import java.net.MalformedURLException;import java.net.SocketTimeoutException;import java.net.URL;import java.net.UnknownHostException;import java.util.ArrayList;import java.util.List;import java.util.logging.Level;import java.util.logging.Logger;public class NewProductListPage extends AppCompatActivity implements View.OnClickListener {    private Toolbar toolbar;    private TextView toolbarTitle, noItem;    private ImageView cart, notification, gridImage, gridItemImage;    private TextView cartNumber, notificationNumber;    private List<CategorySearchModel> categorySearchModelList;    private SwipeFlingAdapterView flingContainer;    private SwippableCardAdapter swippableCardAdapter;    private RecyclerView gridRecyclerView;    private LinearLayout topParent;    private ImageView cancel, wishlist;    private ImageView undo, cartAdd;    private CategorySearchModel categorySearchModel, categorySearchModelForUndo;    private TextView noOfproducts;    private String swipeCategory;    private String totalProducts;    private int searchFactorSize;    private String levelOnecategoryId;    String projectToken;    MixpanelAPI mixpanel;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        //noinspection unchecked        categorySearchModelList = Utils.categoryModelList;        categorySearchModel = categorySearchModelList.get(0);        /**         * intent data         */        swipeCategory = getIntent().getStringExtra("swipe_category");        searchFactorSize = getIntent().getIntExtra("searchFactorSize", 1);        levelOnecategoryId = getIntent().getStringExtra("level_one_category_id");        totalProducts = getIntent().getStringExtra("total_products");        setContentView(R.layout.activity_new_product_list_page);        if (!Homepage.isTablet(this)) {            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);        }        initialiseLayout();        initialiseListeners();        setSupportActionBar(toolbar);        //noinspection ConstantConditions        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        SetStatusBarColor.setStausBarColor(this);        toolbarTitle.setText(R.string.results);        toolbarTitle.setVisibility(View.INVISIBLE);        cart.setVisibility(View.INVISIBLE);        cartNumber.setVisibility(View.INVISIBLE);        notification.setVisibility(View.INVISIBLE);        notificationNumber.setVisibility(View.INVISIBLE);        noOfproducts.setText(String.format("%s  %s", totalProducts, "products"));        topParent.post(new Runnable() {            @Override            public void run() {                setSwippableCardAdapter();            }        });        prepareGridView();        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {            @Override            public void onItemClicked(int position, Object object) {                Intent intent = new Intent(NewProductListPage.this, ProductDetail.class);                intent.putExtra("id", categorySearchModel.getId());                intent.putExtra("from", "new_product_list_page");                projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";                mixpanel = MixpanelAPI.getInstance(NewProductListPage.this,projectToken);                mixpanel.track(categorySearchModel.getId() + " Product Viewed", null);                startActivity(intent);            }        });        cancel.setOnTouchListener(new View.OnTouchListener() {            @Override            public boolean onTouch(View v, MotionEvent event) {                if (event.getAction() == MotionEvent.ACTION_UP) {                    cancel.setImageResource(R.mipmap.cancel);                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {                    flingContainer.getTopCardListener().selectLeft();                    cancel.setImageResource(R.mipmap.can_red);                }                return true;            }        });        wishlist.setOnTouchListener(new View.OnTouchListener() {            @Override            public boolean onTouch(View v, MotionEvent event) {                if (event.getAction() == MotionEvent.ACTION_UP) {                    wishlist.setImageResource(R.mipmap.wish);                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {                    flingContainer.getSelectedItemPosition();                    flingContainer.getTopCardListener().selectRight();                    wishlist.setImageResource(R.mipmap.wish_green);                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewProductListPage.this);                    new AddToWishlist(NewProductListPage.this).addToWishList(sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), categorySearchModel.getId(), new AddToWIshListInterface() {                        @Override                        public void addToWishList(String result) {                        }                    });                }                return true;            }        });    }    private void initialiseLayout() {        toolbar = (Toolbar) findViewById(R.id.toolbar);        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);        cart = (ImageView) findViewById(R.id.cart);        notification = (ImageView) findViewById(R.id.notification);        cartNumber = (TextView) findViewById(R.id.cart_number);        notificationNumber = (TextView) findViewById(R.id.notification_number);        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);        gridRecyclerView = (RecyclerView) findViewById(R.id.grid_recycle_view);        gridImage = (ImageView) findViewById(R.id.grid_image);        gridItemImage = (ImageView) findViewById(R.id.grid_item_image);        topParent = (LinearLayout) findViewById(R.id.top_parent);        cancel = (ImageView) findViewById(R.id.cancel);        wishlist = (ImageView) findViewById(R.id.wishlist);        undo = (ImageView) findViewById(R.id.undo);        cartAdd = (ImageView) findViewById(R.id.add_cart);        noOfproducts = (TextView) findViewById(R.id.no_of_products);        noItem = (TextView) findViewById(R.id.no_item);    }    private void initialiseListeners() {        gridImage.setOnClickListener(this);        cancel.setOnClickListener(this);        undo.setOnClickListener(this);    }    private void setSwippableCardAdapter() {        swippableCardAdapter = new SwippableCardAdapter(this, categorySearchModelList, topParent.getHeight());        flingContainer.setAdapter(swippableCardAdapter);        SwipeFlingAdapterView.onFlingListener onFlingListener = (new SwipeFlingAdapterView.onFlingListener() {            @Override            public void removeFirstObjectInAdapter() {                // this is the simplest way to delete an object from the Adapter (/AdapterView)                try {                    categorySearchModelForUndo = categorySearchModelList.get(0);                    categorySearchModelList.remove(0);                    swippableCardAdapter.notifyDataSetChanged();                    categorySearchModel = categorySearchModelList.get(0);                } catch (IndexOutOfBoundsException ex) {                    ex.printStackTrace();                }            }            @Override            public void onLeftCardExit(Object dataObject) {                executePost(categorySearchModel.getId());                cancel.setImageResource(R.mipmap.cancel);                totalProducts = String.valueOf(Integer.parseInt(totalProducts) - 1);                noOfproducts.setText(totalProducts + " " + "products");                if (swippableCardAdapter.getCount() == 10) {                    Utils.requestSwippableCategory = Utils.requestSwippableCategory + 20;                    if (Integer.parseInt(totalProducts) > 1) {                        if (searchFactorSize == 0) {                            getCategoryData(levelOnecategoryId);                        } else {                            JSONUrl.CATEGORY_SEARCH_URL = "route=feed/rest_api/products" + swipeCategory + "&start=" + Utils.requestSwippableCategory + "&limit=20";                            requestMultipleCategory();                        }                    }                }                if(swippableCardAdapter.getCount()==0){                    makeViewInvisible();                }            }            @Override            public void onRightCardExit(Object dataObject) {                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewProductListPage.this);                new AddToWishlist(NewProductListPage.this).addToWishList(sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), categorySearchModel.getId(), new AddToWIshListInterface() {                    @Override                    public void addToWishList(String result) {                    }                });                wishlist.setImageResource(R.mipmap.wish);                totalProducts = String.valueOf(Integer.parseInt(totalProducts) - 1);                noOfproducts.setText(totalProducts + " " + "products");                if (swippableCardAdapter.getCount() == 10) {                    Utils.requestSwippableCategory = Utils.requestSwippableCategory + 20;                    if (Integer.parseInt(totalProducts) > 1) {                        if (searchFactorSize == 0) {                            getCategoryData(levelOnecategoryId);                        } else {                            JSONUrl.CATEGORY_SEARCH_URL = "route=feed/rest_api/products" + swipeCategory + "&start=" + Utils.requestSwippableCategory + "&limit=20";                            requestMultipleCategory();                        }                    }                }                if(swippableCardAdapter.getCount()==0){                    makeViewInvisible();                }            }            @Override            public void onAdapterAboutToEmpty(int itemsInAdapter) {            }            @Override            public void onScroll(float scrollProgressPercent) {                View view = flingContainer.getSelectedView();                if (view != null) {                    LinearLayout alphaLayout = (LinearLayout) view.findViewById(R.id.alpha_layout);                    if (alphaLayout != null) {                        float factor = scrollProgressPercent * 160f;                        if (scrollProgressPercent > 0) {                            if (factor > 0) {                                gridImage.setVisibility(View.INVISIBLE);                                wishlist.setImageResource(R.mipmap.wish_green);                                cancel.setImageResource(R.mipmap.cancel);                                alphaLayout.setBackgroundColor(Color.argb((int) factor, 53, 202, 137));                                alphaLayout.setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 1);                            }                        } else if (scrollProgressPercent < 0) {                            if (factor < 0) {                                gridImage.setVisibility(View.INVISIBLE);                                cancel.setImageResource(R.mipmap.can_red);                                wishlist.setImageResource(R.mipmap.wish);                                alphaLayout.setBackgroundColor(Color.argb((int) -factor, 242, 52, 52));                                alphaLayout.setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 1);                            }                        } else {                            wishlist.setImageResource(R.mipmap.wish);                            cancel.setImageResource(R.mipmap.cancel);                            gridImage.setVisibility(View.VISIBLE);                            alphaLayout.setBackgroundColor(Color.parseColor("#0000ff00"));                        }                    }                }            }            @Override            public void unDo(Object dataObject, View view) {            }        });        flingContainer.setFlingListener(onFlingListener);    }    private void requestMultipleCategory() {        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));        pDialog.setTitleText("Searching products");        pDialog.setCanceledOnTouchOutside(false);        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);        new MultipleCategorySearch(this).requestMultipleCategorySearch(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {            @Override            public void onResponse(String status, String message) {                if (status.equals("401")) {                    pDialog.dismiss();                    Utils.setTokenInfo(NewProductListPage.this, pDialog, new AccessTokenInfoHolder() {                        @Override                        public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {                            requestMultipleCategory();                        }                    });                } else {                    if (status.equals("false")) {                        pDialog.show();                        pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);                        pDialog.setTitleText(message);                    } else {                        if (pDialog.isShowing()) {                            pDialog.dismissWithAnimation();                        }                        try {                            JSONArray jsonArray = new JSONArray(message);                            searchMultipleCategory(jsonArray);                        } catch (JSONException ex) {                            ex.printStackTrace();                        }                    }                }            }        });    }    private void searchMultipleCategory(JSONArray jsonArray) throws JSONException {        List<CategorySearchModel> categorySearchModelList = new ArrayList<>();        for (int i = 0; i < jsonArray.length(); i++) {            JSONObject jsonObject = jsonArray.getJSONObject(i);            CategorySearchModel categorySearchModel = new CategorySearchModel();            String id = jsonObject.getString("id");            String image = jsonObject.getString("image");            String price = jsonObject.getString("price");            String sellerNickName = jsonObject.getString("seller_nickname");            String sellerName = jsonObject.getString("name");            String sellerBanner = jsonObject.getString("seller_banner");            String sellerId = jsonObject.getString("seller_id");            String sellerAvatar = jsonObject.getString("seller_avatar");            categorySearchModel.setId(id);            categorySearchModel.setImage(image);            categorySearchModel.setPrice(price);            categorySearchModel.setSellerNickName(sellerNickName);            categorySearchModel.setName(sellerName);            categorySearchModel.setBanner(sellerBanner);            categorySearchModel.setSellerId(sellerId);            categorySearchModel.setSellerAvater(sellerAvatar);            categorySearchModelList.add(categorySearchModel);        }        makeViewVisible();        Utils.categoryModelList.addAll(categorySearchModelList);        flingContainer.removeAllViewsInLayout();        swippableCardAdapter.notifyDataSetChanged();    }    @Override    public void onClick(View view) {        int id = view.getId();        switch (id) {            case R.id.grid_image:                if (gridRecyclerView.getVisibility() != View.VISIBLE) {                    gridRecyclerView.setVisibility(View.VISIBLE);                    flingContainer.setVisibility(View.GONE);                    gridItemImage.setImageResource(R.mipmap.ic_swipe);                    gridImage.setImageResource(R.mipmap.ic_swipe);                    wishlist.setVisibility(View.GONE);                    cartAdd.setVisibility(View.GONE);                    cancel.setVisibility(View.GONE);                    undo.setVisibility(View.GONE);                } else {                    flingContainer.setVisibility(View.VISIBLE);                    gridRecyclerView.setVisibility(View.GONE);                    gridImage.setImageResource(R.mipmap.ic_grid_view);                    gridItemImage.setImageResource(R.mipmap.ic_grid_view);                    wishlist.setVisibility(View.VISIBLE);                    cartAdd.setVisibility(View.VISIBLE);                    cancel.setVisibility(View.VISIBLE);                    undo.setVisibility(View.VISIBLE);                }                break;            case R.id.add_cart:                wishlist.setImageResource(R.mipmap.wish);                cancel.setImageResource(R.mipmap.cancel);                Intent intent = new Intent(NewProductListPage.this, ProductDetail.class);                intent.putExtra("id", categorySearchModel.getId());                intent.putExtra("from", "new_product_list_page");                startActivity(intent);                break;            case R.id.undo:                if (categorySearchModelForUndo != null) {                    categorySearchModelList.add(0, categorySearchModelForUndo);                    flingContainer.removeAllViewsInLayout();                    swippableCardAdapter.notifyDataSetChanged();                }                totalProducts = String.valueOf(Integer.parseInt(totalProducts) + 1);                noOfproducts.setText(totalProducts + " " + "products");                break;        }    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        int id = item.getItemId();        if (id == android.R.id.home) {            finish();            return true;        }        return super.onOptionsItemSelected(item);    }    private void prepareGridView() {        gridRecyclerView.setHasFixedSize(true);        SpaceItemGrid1Decoration itemDecoration = new SpaceItemGrid1Decoration(10);        gridRecyclerView.setClipToPadding(false);        gridRecyclerView.addItemDecoration(itemDecoration);        prepareGridRecyclerViewADapter(categorySearchModelList);    }    private void prepareGridRecyclerViewADapter(List<CategorySearchModel> categorySearchModelList) {        CategorySearchGridViewAdapter adapter = new CategorySearchGridViewAdapter(NewProductListPage.this, categorySearchModelList);        GridLayoutManager manager = new GridLayoutManager(this, 2);        gridRecyclerView.setAdapter(adapter);        gridRecyclerView.setLayoutManager(manager);    }    @Override    public void onBackPressed() {        finish();    }    private void getCategoryData(String id) {        final SweetAlertDialog pDialog = new SweetAlertDialog(NewProductListPage.this, SweetAlertDialog.PROGRESS_TYPE);        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));        pDialog.setTitleText("Loading data");        pDialog.setCancelable(true);        pDialog.dismiss();        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewProductListPage.this);        new GetAllProductList(NewProductListPage.this).getProductListContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), id, new GetProductByShopInterface() {            @Override            public void setErrorInfo(String message) {                pDialog.show();                pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);                pDialog.setTitleText(message);            }            @Override            public void setSuccessInfo(JSONArray jsonArray, List<String> id, final List<String> name, List<String> image, List<String> price, List<String> wishlist, List<String> internationalShipping,List<String> category, List<List<String>> colorList, List<List<String>> sizeList, List<List<String>> categoryList, List<JSONArray> jsonArrayList, String totalProduct) {                try {                    if (pDialog.isShowing()) {                        pDialog.dismissWithAnimation();                    }                    searchMultipleCategory(jsonArray);                } catch (JSONException e) {                    e.printStackTrace();                }            }        });    }    private void makeViewVisible() {        noItem.setVisibility(View.GONE);        wishlist.setVisibility(View.VISIBLE);        cartAdd.setVisibility(View.VISIBLE);        cancel.setVisibility(View.VISIBLE);        undo.setVisibility(View.VISIBLE);        gridImage.setVisibility(View.VISIBLE);        gridItemImage.setVisibility(View.VISIBLE);    }    private void makeViewInvisible() {        if(Integer.parseInt(totalProducts) == 0) {            noItem.setVisibility(View.VISIBLE);        }        wishlist.setVisibility(View.GONE);        cartAdd.setVisibility(View.GONE);        cancel.setVisibility(View.GONE);        undo.setVisibility(View.GONE);        gridImage.setVisibility(View.GONE);        gridItemImage.setVisibility(View.GONE);    }    public String executePost(String urlParameters) {        int timeout=5000;        URL url;        HttpURLConnection connection = null;        try {            // Create connection            url = new URL("https://www.binge.sg/index.php?route=feed/rest_api/swipecancel");            connection = (HttpURLConnection) url.openConnection();            connection.setRequestMethod("POST");            connection.setRequestProperty("Content-Type",                    "application/json");            connection.setRequestProperty("Content-Length",                    "" + Integer.toString(urlParameters.getBytes().length));            connection.setRequestProperty("Content-Language", "en-US");            connection.setUseCaches(false);            connection.setDoInput(true);            connection.setDoOutput(true);            connection.setConnectTimeout(timeout);            connection.setReadTimeout(timeout);            // Send request            DataOutputStream wr = new DataOutputStream(                    connection.getOutputStream());            wr.writeBytes(urlParameters);            wr.flush();            wr.close();            // Get Response            InputStream is = connection.getInputStream();            BufferedReader rd = new BufferedReader(new InputStreamReader(is));            String line;            StringBuffer response = new StringBuffer();            while ((line = rd.readLine()) != null) {                response.append(line);                response.append('\r');            }            rd.close();            return response.toString();        } catch (SocketTimeoutException ex) {            ex.printStackTrace();        } catch (MalformedURLException ex) {            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);        } catch (UnknownHostException e) {            e.printStackTrace();        } catch (IOException ex) {            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);        } catch (Exception e) {            e.printStackTrace();        } finally {            if (connection != null) {                connection.disconnect();            }        }        return null;    }}