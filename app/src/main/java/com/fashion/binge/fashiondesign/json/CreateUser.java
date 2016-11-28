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


public class CreateUser {
    private Context context;
    private static final String tag_json_obj = "tag_json_obj";

    public CreateUser(Context context) {
        this.context = context;
    }

    public void requestSignUp(final SweetAlertDialog sweetAlertDialog, final String headerValue, final String firstName, final String lastName, final String signUpEmail, final String signUpPassword, final String retypePassword, final String telephone, final String postCode,final String city, final String country, final JsonResponseHolder jsonResponseHolder) {
        StringRequest requestToken = new StringRequest(Request.Method.POST,  JSONUrl.BASE_URL+JSONUrl.SIGN_UP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {
                        String success = jsonObject.getString("success");
                        if (success.equals("true")) {
                            String data = jsonObject.getString("data");
                            jsonResponseHolder.onResponse(success, data);
                        } else {
                            String error = jsonObject.getString("error");
                            try {
                                JSONObject jsonObject1 = new JSONObject(error);
                                if (jsonObject1.has("telephone")) {
                                    String telephone = jsonObject1.getString("telephone");
                                    jsonResponseHolder.onResponse(success, telephone);
                                } else if (jsonObject1.has("password")) {
                                    String password = jsonObject1.getString("password");
                                    jsonResponseHolder.onResponse(success, password);
                                } else if (jsonObject1.has("address_1")) {
                                    String address = jsonObject1.getString("address_1");
                                    jsonResponseHolder.onResponse(success, address);
                                } else if (jsonObject1.has("city")) {
                                    String city = jsonObject1.getString("city");
                                    jsonResponseHolder.onResponse(success, city);
                                } else if (jsonObject1.has("country")){
                                    String country = jsonObject1.getString("country");
                                    jsonResponseHolder.onResponse(success, country);
                                }
                                else {
                                    jsonResponseHolder.onResponse(success, error);
                                }
                            } catch (JSONException ex) {
                                jsonResponseHolder.onResponse(success, error);
                            }
                        }
                    } else {
                        String statusCode = jsonObject.getString("statusCode");
                        String statusText = jsonObject.getString("statusText");
                        jsonResponseHolder.onResponse(statusCode, statusText);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
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
                String countryId = "188";
                String zone_id = "0";
                String agree = "1";
                String str = "{\"firstname\":\"" + firstName + "\"," +
                        "\"lastname\":\"" + lastName + "\"," +
                        "\"email\":\"" + signUpEmail + "\"," +
                        "\"password\":\"" + signUpPassword + "\"," +
                        "\"confirm\":\"" + retypePassword + "\"," +
                        "\"telephone\":\"" + telephone + "\"," +
                        "\"postcode\":\"" + postCode + "\"," +
                        "\"city\":\"" + city + "\"," +
                        "\"country\":\"" + country + "\"," +
                        "\"zone_id\":\"" + zone_id + "\"," +
                        "\"agree\":\"" + agree + "\"}";
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
