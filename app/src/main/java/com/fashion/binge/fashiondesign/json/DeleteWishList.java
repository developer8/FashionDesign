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
 * Created by gokarna on 05/05/16.
 */
public class DeleteWishList {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public DeleteWishList(Context context) {
        this.context = context;
    }

    public void deleteWishList(final SweetAlertDialog sweetAlertDialog, final String headerValue, String shopId, final ResponseInfoHolder responseInfoHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.DELETE,  JSONUrl.BASE_URL+JSONUrl.DELETE_WISHLIST_URL + shopId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        String data = jsonObject.getString("data");
                        responseInfoHolder.setFollowingInfo(success, data);
                    } else {
                        String error = jsonObject.getString("error");
                        responseInfoHolder.setFollowingInfo(success, error);
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
