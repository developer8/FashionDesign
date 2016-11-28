package com.fashion.binge.fashiondesign.json;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.helper.AppController;
import com.fashion.binge.fashiondesign.interfaces.FollowUnfollowInfoHolder;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gokarna on 06/05/16.
 */
public class ProductDetail {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public ProductDetail(Context context) {
        this.context = context;
    }

    public void getProductDetail(final SweetAlertDialog sweetAlertDialog, final String headerValue, String shopId, final FollowUnfollowInfoHolder followUnfollowInfoHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.GET,  JSONUrl.BASE_URL+JSONUrl.PRODUCT_DETAIL_URL + shopId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")) {
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            String data = jsonObject.getString("data");
                            followUnfollowInfoHolder.setFollowUnfollowInfo(success, data);
                        } else {
                            String error = jsonObject.getString("error");
                            followUnfollowInfoHolder.setFollowUnfollowInfo(success, error);
                        }
                    }else{
                        String statusCode = jsonObject.getString("statusCode");
                        String statusText = jsonObject.getString("statusText");
                        followUnfollowInfoHolder.setFollowUnfollowInfo(statusCode, statusText);
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
