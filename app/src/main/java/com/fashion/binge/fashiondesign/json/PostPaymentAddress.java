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


public class PostPaymentAddress {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";
    private String str="";
    public PostPaymentAddress(Context context) {
        this.context = context;
    }

    public void postPaymentAddress(boolean isChecked, final SweetAlertDialog sweetAlertDialog, final String headerValue, final String paymetAddress, final String addressId, final ResponseInfoHolder responseInfoHolder) {
        String URL;
        if (isChecked) {
            URL =  JSONUrl.BASE_URL+JSONUrl.GET_USER_INFORMATION_URL;
            str = "{\"payment_address\":\"" + paymetAddress + "\",\"address_id\":\"" + addressId + "\"}";
        } else {
            URL =  JSONUrl.BASE_URL+JSONUrl.GET_SHIPPING_ADDRESS;
            str = "{\"shipping_address\":\"" + paymetAddress + "\",\"address_id\":\"" + addressId + "\"}";
        }
        StringRequest requestToken = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            responseInfoHolder.setFollowingInfo(success, null);
                        } else {
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
            public byte[] getBody() throws AuthFailureError {
                return str.getBytes();
            }

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
