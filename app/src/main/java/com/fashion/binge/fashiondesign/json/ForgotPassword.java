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
import com.fashion.binge.fashiondesign.interfaces.JsonResponseHolder;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgotPassword {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public ForgotPassword(Context context) {
        this.context = context;
    }

    public void requestForgotPassword(final SweetAlertDialog sweetAlertDialog,final String headerValue,final String email,final JsonResponseHolder jsonResponseHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.POST,  JSONUrl.BASE_URL+JSONUrl.FORGOT_PASSWORD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String success=jsonObject.getString("success");
                    if(jsonObject.has("success")) {
                        if (success.equals("false")) {
                            String error = jsonObject.getString("error");
                            jsonResponseHolder.onResponse(success, error);
                        } else {
                            jsonResponseHolder.onResponse(success, null);
                        }
                    }else{
                        String statusCode = jsonObject.getString("statusCode");
                        String statusText = jsonObject.getString("statusText");
                        jsonResponseHolder.onResponse(statusCode, statusText);
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
                String str = "{\"email\":\"" + email + "\"}";
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
