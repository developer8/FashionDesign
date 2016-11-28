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
import com.fashion.binge.fashiondesign.interfaces.CartResponseInfoHolder;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DeleteCart {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public DeleteCart(Context context) {
        this.context = context;
    }

    public void deleteCart(final SweetAlertDialog sweetAlertDialog, final String headerValue, final JSONObject jsonObject, final CartResponseInfoHolder cartResponseInfoHolder) {
        final StringRequest requestToken = new StringRequest(Request.Method.DELETE,  JSONUrl.BASE_URL+JSONUrl.GET_CART_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response delete",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        String total=jsonObject.getString("total");
                        String totalProductCount=jsonObject.getString("total_product_count");
                        cartResponseInfoHolder.setCartInfo(success,total,totalProductCount);
                    } else {
                        cartResponseInfoHolder.setCartInfo(success, null,null);
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
                String str = jsonObject.toString();
                return str.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
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
