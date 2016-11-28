package com.fashion.binge.fashiondesign.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.json.ShopFragmentJson;
import com.fashion.binge.fashiondesign.models.CategorySearchModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;


public class Utils {
    public static List<CategorySearchModel> categoryModelList = new ArrayList<>();
    public static void setTokenInfo(final Context context, final SweetAlertDialog pDialog, final AccessTokenInfoHolder accessTokenInfoHolder) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        new ShopFragmentJson(context).requestToken(pDialog, new AccessTokenInfoHolder() {
            @Override
            public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                editor.putString(SharedPrefrenceInfo.IS_TOKEN_VALID, accessToken);
                editor.putString(SharedPrefrenceInfo.EXPIRE_DATE, expires_in);
                editor.putString(SharedPrefrenceInfo.TOKEN_TYPE, token_type);
                editor.commit();
                accessTokenInfoHolder.setAcessTokenInfo(accessToken, expires_in, token_type);
            }
        });
    }

    public static void setTokenInfo(final Context context, final SweetAlertDialog pDialog, final String ipAddress, final AccessTokenInfoHolder accessTokenInfoHolder) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        new ShopFragmentJson(context).requestToken(pDialog, ipAddress, new AccessTokenInfoHolder() {
            @Override
            public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                editor.putString(SharedPrefrenceInfo.IS_TOKEN_VALID, accessToken);
                editor.putString(SharedPrefrenceInfo.EXPIRE_DATE, expires_in);
                editor.putString(SharedPrefrenceInfo.TOKEN_TYPE, token_type);
                editor.commit();
                accessTokenInfoHolder.setAcessTokenInfo(accessToken, expires_in, token_type);
            }
        });



    }
    public static int requestSwippableCategory=1;
}
