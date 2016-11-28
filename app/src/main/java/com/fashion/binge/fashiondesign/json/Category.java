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
import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gokarna on 01/05/16.
 */
public class Category {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public Category(Context context) {
        this.context = context;
    }

    public void requestCategory(final SweetAlertDialog sweetAlertDialog, final String headerValue, final ResponseInfoHolder responseInfoHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.GET,  JSONUrl.BASE_URL+JSONUrl.LEVEL_THREE_CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            String data = jsonObject.getString("data");
                            responseInfoHolder.setFollowingInfo(success, data);
                        } else if (success.equals("false")) {
                            String error = jsonObject.getString("error");
                            responseInfoHolder.setFollowingInfo(success, error);
                        }
                    } else {
                        String statusCode = jsonObject.getString("statusCode");
                        String statusText = jsonObject.getString("statusText");
                        responseInfoHolder.setFollowingInfo(statusCode, statusText);
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
