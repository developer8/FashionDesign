package com.fashion.binge.fashiondesign.json;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.helper.AppController;
import com.fashion.binge.fashiondesign.interfaces.GetProductByShopInterface;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetAllProductList {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";
    public GetAllProductList(Context context) {
        this.context = context;
    }

    public void getProductListContent(final SweetAlertDialog sweetAlertDialog, final String headerValue, final String categoryId, final GetProductByShopInterface getProductByShopInterface) {
        StringRequest requestToken = new StringRequest(Request.Method.GET,  JSONUrl.BASE_URL+JSONUrl.PRODUCT_LIST_URL+categoryId+"&start="+ Utils.requestSwippableCategory+"&limit=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<String> idList = new ArrayList<>();
                    List<String> nameList = new ArrayList<>();
                    List<String> imageList = new ArrayList<>();
                    List<String> priceList = new ArrayList<>();
                    List<String> internationShippingList = new ArrayList<>();
                    List<List<String>> globalColorList = new ArrayList<>();
                    List<List<String>> globalSizeList = new ArrayList<>();
                    List<List<String>> globalCategoryList=new ArrayList<>();
                    List<String> temp=new ArrayList<>();
                    List<JSONArray> jsonArrayList = new ArrayList<>();
                    JSONArray jsonArray1;
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        String data = jsonObject.getString("data");
                        Log.v("Product Data",data);
                        String totalProduct=jsonObject.getString("total");
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            List<String> colorList = new ArrayList<>();
                            List<String> sizeList = new ArrayList<>();
                            List<String> categoryList=new ArrayList<>();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String id = jsonObject1.getString("id");
                            String name = jsonObject1.getString("name");
                            String image = jsonObject1.getString("image");
                            String price = jsonObject1.getString("price");
                            String internationShipping = jsonObject1.getString("international_shipping");
                            String category = jsonObject1.getString("category");
                            JSONArray categoryJsonArray = new JSONArray(category);
                            for (int m = 0; m < categoryJsonArray.length(); m++) {
                                JSONObject categoryObject = categoryJsonArray.getJSONObject(m);
                                String categoryName = categoryObject.getString("name");
                                categoryList.add(categoryName);
                            }
                            temp.addAll(categoryList);
                            String options = jsonObject1.getString("options");
                            jsonArray1 = new JSONArray(options);
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                String value = jsonObject2.getString("name");
                                if (value.equals("Color")) {
                                    JSONArray jsonArray2 = new JSONArray(jsonObject2.getString("option_value"));
                                    for (int k = 0; k < jsonArray2.length(); k++) {
                                        JSONObject jsonObject3 = jsonArray2.getJSONObject(k);
                                        String color = jsonObject3.getString("image");
                                        colorList.add(color);
                                    }
                                } else if (value.equals("Size")) {
                                    JSONArray jsonArray2 = new JSONArray(jsonObject2.getString("option_value"));
                                    for (int k = 0; k < jsonArray2.length(); k++) {
                                        JSONObject jsonObject3 = jsonArray2.getJSONObject(k);
                                        String size = jsonObject3.getString("name");
                                        sizeList.add(size);
                                    }
                                }
                            }
                            idList.add(id);
                            nameList.add(name);
                            imageList.add(image);
                            priceList.add(price);
                            internationShippingList.add(internationShipping);
                            globalColorList.add(colorList);
                            globalSizeList.add(sizeList);
                            globalCategoryList.add(categoryList);
                            jsonArrayList.add(jsonArray1);
                        }
                        getProductByShopInterface.setSuccessInfo(jsonArray,idList, nameList, imageList, priceList,internationShippingList,null,temp,globalColorList, globalSizeList,globalCategoryList,jsonArrayList,totalProduct);
                    } else {
                        String error = jsonObject.getString("error");
                        getProductByShopInterface.setErrorInfo(error);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                sweetAlertDialog.dismiss();
                new AlertDIalogMessage().showErrorMessage(volleyError, context);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", headerValue);
                return params;
            }
        };
        requestToken.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(requestToken, tag_json_obj);
    }
}
