package com.fashion.binge.fashiondesign.json;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.helper.AppController;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.ShopPageContentHolder;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ShopFragmentJson {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public ShopFragmentJson(Context context) {
        this.context = context;
    }

    public void requestToken(final SweetAlertDialog sweetAlertDialog, final AccessTokenInfoHolder accessTokenInfoHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.POST,  JSONUrl.BASE_URL+JSONUrl.ACCESS_TOKEN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String access_token = jsonObject.getString("access_token");
                    String expires_in = jsonObject.getString("expires_in");
                    String token_type = jsonObject.getString("token_type");
                    accessTokenInfoHolder.setAcessTokenInfo(access_token, expires_in, token_type);
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
                params.put("Authorization", "Basic MTkxMTE5OTE6MTkxMTE5OTE=");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(requestToken, tag_json_obj);

    }

    public void requestToken(final SweetAlertDialog sweetAlertDialog,final String ipAddress ,final AccessTokenInfoHolder accessTokenInfoHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.POST,  JSONUrl.BASE_URL+JSONUrl.ACCESS_TOKEN_URL_WITH_IP+ipAddress, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String access_token = jsonObject.getString("access_token");
                    String expires_in = jsonObject.getString("expires_in");
                    String token_type = jsonObject.getString("token_type");
                    accessTokenInfoHolder.setAcessTokenInfo(access_token, expires_in, token_type);
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
                params.put("Authorization", "Basic MTkxMTE5OTE6MTkxMTE5OTE=");
                return params;
            }
        };
        Log.v("Request Token",requestToken.toString());
        AppController.getInstance().addToRequestQueue(requestToken, tag_json_obj);

    }

    public void getShopPageContent(final SweetAlertDialog sweetAlertDialog, final String headerValue, final ShopPageContentHolder shopPageContentHolder) {
        Log.e("Shop url", JSONUrl.SHOP_URL);
        StringRequest requestToken = new StringRequest(Request.Method.GET,  JSONUrl.BASE_URL+JSONUrl.SHOP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            String data = jsonObject.getString("data");
                            shopPageContentHolder.setSuccessShopPageContent(success, data);
                        } else {
                            String error = jsonObject.getString("error");
                            shopPageContentHolder.setSuccessShopPageContent(success, error);
                        }
                    } else {
                        String statusCode = jsonObject.getString("statusCode");
                        String statusText = jsonObject.getString("statusText");
                        shopPageContentHolder.setErrorShopPageContent(statusCode, statusText);
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
        AppController.getInstance().addToRequestQueue(requestToken, tag_json_obj);
    }
}

