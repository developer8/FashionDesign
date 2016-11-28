package com.fashion.binge.fashiondesign.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.adapters.NewShopFragmentAdapter;
import com.fashion.binge.fashiondesign.classes.AlertDIalogMessage;
import com.fashion.binge.fashiondesign.classes.ConnectionManager;
import com.fashion.binge.fashiondesign.classes.Utils;
import com.fashion.binge.fashiondesign.interfaces.AccessTokenInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.JsonResponseHolder;
import com.fashion.binge.fashiondesign.interfaces.ShopPageContentHolder;
import com.fashion.binge.fashiondesign.json.GetNotificationData;
import com.fashion.binge.fashiondesign.json.ShopFragmentJson;
import com.fashion.binge.fashiondesign.models.ShoppageInfo;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NewShopFragment extends Fragment {
    private List<String> imageList = new ArrayList<>();
    private List<ShoppageInfo> shoppageInfoList;
    private RecyclerView recyclerView;
    private TextView notificationNumber;
    private ImageView noInternet;
    private FloatingActionButton floatingActionButton;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String unfollowTag = intent.getStringExtra("message");
            String from = intent.getStringExtra("from");
            if (from.equals("following")) {
                for (int i = 0; i < shoppageInfoList.size(); i++) {
                    ShoppageInfo shoppageInfo = shoppageInfoList.get(i);
                    if (shoppageInfo.getSellerId().equals(unfollowTag)) {
                        shoppageInfo.setUserFollow("false");
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        }
    };
    private BroadcastReceiver mFollowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String followingId = intent.getStringExtra("message");
            String type = intent.getStringExtra("type");
            if (type.equals("follow")) {
                for (int i = 0; i < shoppageInfoList.size(); i++) {
                    ShoppageInfo shoppageInfo = shoppageInfoList.get(i);
                    if (shoppageInfo.getSellerId().equals(followingId)) {
                        shoppageInfo.setUserFollow("true");
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            } else {
                for (int i = 0; i < shoppageInfoList.size(); i++) {
                    ShoppageInfo shoppageInfo = shoppageInfoList.get(i);
                    if (shoppageInfo.getSellerId().equals(followingId)) {
                        shoppageInfo.setUserFollow("false");
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFollowReceiver,
                new IntentFilter("follow_event"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFollowReceiver,
                new IntentFilter("follow_event"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_shop, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        notificationNumber = (TextView) getActivity().findViewById(R.id.notification_number);
        noInternet = (ImageView) view.findViewById(R.id.no_internet);
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        validateToken();
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && floatingActionButton.isShown())
                    floatingActionButton.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                //   floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(SharedPrefrenceInfo.IS_PRODUCT_LIST_PAGE_OPEN_FIRST_TIME, true)) {
            editor.putBoolean(SharedPrefrenceInfo.IS_PRODUCT_LIST_PAGE_OPEN_FIRST_TIME, false);
            editor.apply();
            prepareVideo();
        }
        return view;
    }

    private void prepareVideo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity(), R.layout.video_layout, null);
        builder.setView(view);
        VideoView videoView = (VideoView) view.findViewById(R.id.video_view);
        String path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.demo;
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                alertDialog.dismiss();
            }
        });
    }

    private void validateToken() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (new ConnectionManager(getActivity()).isConnectedToInternet()) {
            final SweetAlertDialog pDialog = new AlertDIalogMessage().showProgressDialog(getActivity(), "Loading...");
            pDialog.setCancelable(false);
            if (sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, "token_invalid").equals("token_invalid")) {
                Utils.setTokenInfo(getActivity(), pDialog, new AccessTokenInfoHolder() {
                    @Override
                    public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                        new ShopFragmentJson(getActivity()).getShopPageContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ShopPageContentHolder() {
                            @Override
                            public void setErrorShopPageContent(String statusCode, String statusText) {
                                //do nothing here since the  case unauthorized will not arrive for the first time
                            }

                            @Override
                            public void setSuccessShopPageContent(String success, final String data) {
                                if (success.equals("true")) {
                                    new GetNotificationData(getActivity()).getNotificationData(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
                                        @Override
                                        public void onResponse(String status, String message) {
                                            if (status.equals("true")) {
                                                try {
                                                    Set<String> id = sharedPreferences.getStringSet(SharedPrefrenceInfo.IDSET, new HashSet<String>());
                                                    JSONArray jsonArray = new JSONArray(message);
                                                    editor.putString(SharedPrefrenceInfo.NOTIFICATION, message);
                                                    editor.putString(SharedPrefrenceInfo.NOTIFICATION_COUNT, String.valueOf(jsonArray.length()));
                                                    editor.apply();
                                                    setNotificationCount(jsonArray.length());
                                                    List<ShoppageInfo> shoppageInfoList = getShopPageContent(data);
                                                    //set the adapter after loading data from url
                                                    final NewShopFragmentAdapter recyclerViewAdapter = new NewShopFragmentAdapter(getActivity(), recyclerView, shoppageInfoList);
                                                    recyclerView.setAdapter(recyclerViewAdapter);
                                                    pDialog.dismiss();
                                                } catch (JSONException ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    pDialog.dismiss();
                                    new AlertDIalogMessage().showErrorDialog(getActivity(), data);
                                }
                            }
                        });
                    }
                });
            } else {
                new ShopFragmentJson(getActivity()).getShopPageContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ShopPageContentHolder() {
                    @Override
                    public void setErrorShopPageContent(String statusCode, String statusText) {
                        //this method is invoked when unauthorized response come from server
                        Utils.setTokenInfo(getActivity(), pDialog, new AccessTokenInfoHolder() {
                            @Override
                            public void setAcessTokenInfo(String accessToken, String expires_in, String token_type) {
                                new ShopFragmentJson(getActivity()).getShopPageContent(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new ShopPageContentHolder() {
                                    @Override
                                    public void setErrorShopPageContent(String statusCode, String statusText) {
                                        //do nothing here since the  case unauthorized will not arrive for the first time
                                    }

                                    @Override
                                    public void setSuccessShopPageContent(String success, final String data) {
                                        if (success.equals("true")) {
                                            new GetNotificationData(getActivity()).getNotificationData(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
                                                @Override
                                                public void onResponse(String status, String message) {
                                                    if (status.equals("true")) {
                                                        try {
                                                            Set<String> id = sharedPreferences.getStringSet(SharedPrefrenceInfo.IDSET, new HashSet<String>());
                                                            int size = id.size();
                                                            JSONArray jsonArray = new JSONArray(message);
                                                            editor.putString(SharedPrefrenceInfo.NOTIFICATION, message);
                                                            editor.putString(SharedPrefrenceInfo.NOTIFICATION_COUNT, String.valueOf(jsonArray.length()));
                                                            editor.apply();
                                                            setNotificationCount(jsonArray.length() - size);
                                                            List<ShoppageInfo> shoppageInfoList = getShopPageContent(data);
                                                            //set the adapter after loading data from url
                                                            final NewShopFragmentAdapter recyclerViewAdapter = new NewShopFragmentAdapter(getActivity(), recyclerView, shoppageInfoList);
                                                            recyclerView.setAdapter(recyclerViewAdapter);
                                                            pDialog.dismiss();
                                                        } catch (JSONException ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            pDialog.dismiss();
                                            new AlertDIalogMessage().showErrorDialog(getActivity(), data);
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void setSuccessShopPageContent(String success, final String data) {
                        if (success.equals("true")) {
                            new GetNotificationData(getActivity()).getNotificationData(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), new JsonResponseHolder() {
                                @Override
                                public void onResponse(String status, String message) {
                                    if (status.equals("true")) {
                                        try {
                                            Set<String> id = sharedPreferences.getStringSet(SharedPrefrenceInfo.IDSET, new HashSet<String>());
                                            int size = id.size();
                                            JSONArray jsonArray = new JSONArray(message);
                                            editor.putString(SharedPrefrenceInfo.NOTIFICATION, message);
                                            editor.putString(SharedPrefrenceInfo.NOTIFICATION_COUNT, String.valueOf(jsonArray.length()));
                                            editor.apply();
                                            setNotificationCount(jsonArray.length() - size);
                                            List<ShoppageInfo> shoppageInfoList = getShopPageContent(data);
                                            //set the adapter after loading data from url
                                            final NewShopFragmentAdapter recyclerViewAdapter = new NewShopFragmentAdapter(getActivity(), recyclerView, shoppageInfoList);
                                            recyclerView.setAdapter(recyclerViewAdapter);
                                            pDialog.dismiss();
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            pDialog.dismiss();
                            new AlertDIalogMessage().showErrorDialog(getActivity(), data);
                        }
                    }
                });
            }
        } else {
            noInternet.setVisibility(View.VISIBLE);
        }
    }

    private void setNotificationCount(int count) {
        if (count > 0) {
            notificationNumber.setVisibility(View.VISIBLE);
            notificationNumber.setText(String.valueOf(count));
        } else {
            notificationNumber.setVisibility(View.GONE);
        }
    }

    private List<ShoppageInfo> getShopPageContent(String data /*data is type of json*/) {
        shoppageInfoList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            if (imageList.size() != 0) {
                imageList.clear();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                ShoppageInfo shoppageInfo = new ShoppageInfo();
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String sellerId = jsonObject1.getString("seller_id");
                String sellerName = jsonObject1.getString("seller_name");
                String sellerNickName = jsonObject1.getString("seller_nickname");
                String sellerBanner = jsonObject1.getString("seller_banner");
                String sellerAvatar = jsonObject1.getString("seller_avatar");
                String sellerGroup = jsonObject1.getString("seller_group");
                String userFollow = jsonObject1.getString("user_follow");
                String sellerFlag = jsonObject1.getString("seller_country_flag");
                String sellerCountry = jsonObject1.getString("seller_country_name");
                shoppageInfo.setSellerId(sellerId);
                shoppageInfo.setSellerName(sellerName);
                shoppageInfo.setSellerNickName(sellerNickName);
                shoppageInfo.setSellerBanner(sellerBanner);
                shoppageInfo.setSellerGroup(sellerGroup);
                shoppageInfo.setUserFollow(userFollow);
                shoppageInfo.setSellerCountryName(sellerCountry);
                shoppageInfo.setSellerCountryFlag(sellerFlag);
                shoppageInfo.setSellerAvater(sellerAvatar);
                imageList.add(sellerBanner);
                shoppageInfoList.add(shoppageInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shoppageInfoList;
    }

}
