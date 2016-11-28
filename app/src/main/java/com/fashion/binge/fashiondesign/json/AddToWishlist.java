package com.fashion.binge.fashiondesign.json;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.helper.AppController;
import com.fashion.binge.fashiondesign.interfaces.AddToWIshListInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gokarna on 02/05/16.
 */
public class AddToWishlist {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public AddToWishlist(Context context) {
        this.context = context;
    }

    public void addToWishList(final String headerValue, final String shopId, final AddToWIshListInterface addToWIshListInterface) {
        StringRequest requestToken = new StringRequest(Request.Method.POST,  JSONUrl.BASE_URL+JSONUrl.ADD_TO_WISHLIST_URL + shopId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if(jsonObject.has("success")) {
                        if (success.equals("true")) {
                            addToWIshListInterface.addToWishList("Successfully added to wishlist");
                        } else {
                            addToWIshListInterface.addToWishList("Cannot add to wishlist");
                        }
                    }else{
                        String statusCode = jsonObject.getString("statusCode");
                        addToWIshListInterface.addToWishList(statusCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    addToWIshListInterface.addToWishList("No internet connection");
                } else {
                    addToWIshListInterface.addToWishList("Error adding to wishList");
                }
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
