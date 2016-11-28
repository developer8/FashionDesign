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
import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PostNewPaymentAddress {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";
    private String str="";
    public PostNewPaymentAddress(Context context) {
        this.context = context;
    }

    public void postNewPaymentAddress(boolean isChecked,final SweetAlertDialog sweetAlertDialog, final String headerValue, final String firstname, final String lastname, final String address, final String city, final String postcode, final ResponseInfoHolder responseInfoHolder) {
        String URL;
        if (isChecked) {
            str = "{\"address_1\":\"" + address + "\"," +
                    "\"address_2\":\"" + address + "\"," +
                    "\"city\":\"" + city + "\"," +
                    "\"company_id\":\"" + address + "\"," +
                    "\"company\":\"" + address + "\"," +
                    "\"country_id\":\"" + address + "\"," +
                    "\"firstname\":\"" + firstname + "\"," +
                    "\"lastname\":\"" + lastname + "\"," +
                    "\"postcode\":\"" + postcode + "\"," +
                    "\"tax_id\":\"" + address + "\"," +
                    "\"zone_id\":\"" + address + "\"}";
            URL =  JSONUrl.BASE_URL+JSONUrl.GET_USER_INFORMATION_URL;
        } else {
            String isNew="new";
            str = "{\"address_1\":\"" + address + "\"," +
                    "\"address_2\":\"" + address + "\"," +
                    "\"city\":\"" + city + "\"," +
                    "\"company_id\":\"" + address + "\"," +
                    "\"company\":\"" + address + "\"," +
                    "\"country_id\":\"" + address + "\"," +
                    "\"firstname\":\"" + firstname + "\"," +
                    "\"lastname\":\"" + lastname + "\"," +
                    "\"postcode\":\"" + postcode + "\"," +
                    "\"tax_id\":\"" + address + "\"," +
                    "\"shipping_address\":\"" + isNew  + "\"," +
                    "\"zone_id\":\"" + address + "\"}";
            URL =  JSONUrl.BASE_URL+JSONUrl.GET_SHIPPING_ADDRESS;
        }
        StringRequest requestToken = new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Reponse",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if(success.equals("true")) {
                        responseInfoHolder.setFollowingInfo(success,null);
                    }else{
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
            public byte[] getBody() throws AuthFailureError {
                str = "{\"address_1\":\"" + address + "\"," +
                        "\"address_2\":\"" + address + "\"," +
                        "\"city\":\"" + city + "\"," +
                        "\"company_id\":\"" + address + "\"," +
                        "\"company\":\"" + address + "\"," +
                        "\"country_id\":\"" + address + "\"," +
                        "\"firstname\":\"" + firstname + "\"," +
                        "\"lastname\":\"" + lastname + "\"," +
                        "\"postcode\":\"" + postcode + "\"," +
                        "\"tax_id\":\"" + address + "\"," +
                        "\"zone_id\":\"" + address + "\"}";
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
