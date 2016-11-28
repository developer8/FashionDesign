package com.fashion.binge.fashiondesign.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.adapters.FollowingFragmentListAdapter;
import com.fashion.binge.fashiondesign.classes.ConnectionManager;
import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;
import com.fashion.binge.fashiondesign.json.Following;
import com.fashion.binge.fashiondesign.models.ShoppageInfo;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {
    private ListView followingFragmentListView;
    private SweetAlertDialog pDialog;
    private TextView noItem;
    private ImageView noInternet;

    public FollowingFragment() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (new ConnectionManager(getActivity()).isConnectedToInternet()) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sharedPreferences.getString(SharedPrefrenceInfo.IS_USER_LOGGED_IN, "").equals("true")) {
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                    pDialog.setTitleText("Loading....");
                    pDialog.setCancelable(true);
                    pDialog.show();
                    new Following(getActivity()).setFollowJson(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ResponseInfoHolder() {
                        @Override
                        public void setFollowingInfo(String success, String data) {
                            if (success.equals("true")) {
                                List<ShoppageInfo> shoppageInfoList = getShopPageContent(data);
                                followingFragmentListView.setAdapter(new FollowingFragmentListAdapter(getActivity(), noItem, shoppageInfoList));
                                pDialog.dismiss();
                                if (followingFragmentListView.getAdapter().getCount() == 0) {
                                    noItem.setVisibility(View.VISIBLE);
                                    noItem.setText(R.string.no_shops_under_following);
                                } else {
                                    noItem.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                pDialog.setTitleText(data);
                            }
                        }
                    });
                }
            } else {
                noInternet.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.following_fragment, container, false);
        followingFragmentListView = (ListView) view.findViewById(R.id.following_fragment_listview);
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        noItem = (TextView) view.findViewById(R.id.no_item);
        noInternet = (ImageView) view.findViewById(R.id.no_internet);
        return view;
    }

    private List<ShoppageInfo> getShopPageContent(String data /*data is type of json*/) {
        List<ShoppageInfo> shoppageInfoList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                ShoppageInfo shoppageInfo = new ShoppageInfo();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String sellerId = jsonObject1.getString("seller_id");
                String sellerName = jsonObject1.getString("seller_name");
                String sellerNickName = jsonObject1.getString("seller_nickname");
                String sellerBanner = jsonObject1.getString("seller_banner");
                String sellerGroup = jsonObject1.getString("seller_group");
                String sellerAvatar = jsonObject1.getString("seller_avatar");
                String sellerCountry = jsonObject1.getString("seller_country_name");
                String sellerFlag = jsonObject1.getString("seller_country_flag");
                shoppageInfo.setSellerId(sellerId);
                shoppageInfo.setSellerName(sellerName);
                shoppageInfo.setSellerNickName(sellerNickName);
                shoppageInfo.setSellerBanner(sellerBanner);
                shoppageInfo.setSellerGroup(sellerGroup);
                shoppageInfo.setSellerCountryFlag(sellerFlag);
                shoppageInfo.setSellerCountryName(sellerCountry);
                shoppageInfo.setSellerAvater(sellerAvatar);
                shoppageInfoList.add(shoppageInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shoppageInfoList;
    }
}
