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
import com.fashion.binge.fashiondesign.interfaces.GetShopByIDInterface;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GetShopById {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public GetShopById(Context context) {
        this.context = context;
    }

    public void requestgetShopByIdJson(final SweetAlertDialog sweetAlertDialog, final String id,final String headerValue, final GetShopByIDInterface getShopByIDInterface) {
        StringRequest requestToken = new StringRequest(Request.Method.GET,  JSONUrl.BASE_URL+JSONUrl.GET_SHOP_BY_ID_URL+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response",response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.has("success")) {
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            String data = jsonObject.getString("data");
                            JSONObject jsonObject1 = new JSONObject(data);
                            String description = jsonObject1.getString("seller_description");
                            getShopByIDInterface.setSuccessInfo(description);
                        }
                    }else{
                        String statusCode = jsonObject.getString("statusCode");
                        getShopByIDInterface.setSuccessInfo(statusCode);
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

