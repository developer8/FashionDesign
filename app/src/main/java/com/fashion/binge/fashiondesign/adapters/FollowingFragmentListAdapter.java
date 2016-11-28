package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.ShopPageContent;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.interfaces.FollowUnfollowInfoHolder;
import com.fashion.binge.fashiondesign.json.FollowUnfollowJson;
import com.fashion.binge.fashiondesign.models.ShoppageInfo;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FollowingFragmentListAdapter extends BaseAdapter {
    private Context context;
    private List<ShoppageInfo> shoppageInfoList;
    private TextView noItem;

    public FollowingFragmentListAdapter(Context context, TextView noItem, List<ShoppageInfo> shoppageInfoList) {
        this.context = context;
        this.shoppageInfoList = shoppageInfoList;
        this.noItem = noItem;
    }

    @Override
    public int getCount() {
        return shoppageInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.following_fragment_list_content, parent, false);
        }
        final ShoppageInfo shoppageInfo = shoppageInfoList.get(position);
        CircleImageView circleImageView = (CircleImageView) convertView.findViewById(R.id.profile_image);
        ImageView sellerCountryFlag = (ImageView) convertView.findViewById(R.id.seller_country_flag);
        LinearLayout topParent=(LinearLayout)convertView.findViewById(R.id.top_parent);
        topParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopPageContent.class);
                intent.putExtra("shop_banner", shoppageInfo.getSellerBanner());
                intent.putExtra("avatar", shoppageInfo.getSellerAvater());
                intent.putExtra("id", shoppageInfo.getSellerId());
                intent.putExtra("seller_name", shoppageInfo.getSellerNickName());
                intent.putExtra("seller_flag",shoppageInfo.getSellerCountryFlag());
                intent.putExtra("seller_country",shoppageInfo.getSellerCountryName());
                intent.putExtra("following_text","FOLLOWING");
                context.startActivity(intent);
            }
        });
        final Button unfollow = (Button) convertView.findViewById(R.id.unfollow);
        unfollow.setTag(shoppageInfo.getSellerId());
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                pDialog.setTitleText("UnFollowing");
                pDialog.setCancelable(true);
                pDialog.show();
                new FollowUnfollowJson(context).setUnFollowJson(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), unfollow.getTag().toString(), new FollowUnfollowInfoHolder() {
                    @Override
                    public void setFollowUnfollowInfo(String success, String error) {
                        if (success.equals("true")) {
                            Intent intent = new Intent("custom-event-name");
                            intent.putExtra("message", unfollow.getTag().toString());
                            intent.putExtra("from", "following");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            pDialog.setTitleText("Successfully Unfollowed");
                            removeItem(position);
                            if (FollowingFragmentListAdapter.this.getCount() == 0) {
                                noItem.setVisibility(View.VISIBLE);
                                noItem.setText(R.string.no_shops_under_following);
                            } else {
                                noItem.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setTitleText(error);
                        }
                    }
                });
            }
        });
        Glide.with(context)
                .load(JSONUrl.IMAGE_BASE_URL + shoppageInfo.getSellerAvater())
                .into(circleImageView);
        Glide.with(context)
                .load(shoppageInfo.getSellerCountryFlag()).into(sellerCountryFlag);
        TextView shopName = (TextView) convertView.findViewById(R.id.shop_name);
        shopName.setText(shoppageInfo.getSellerNickName());
        return convertView;
    }

    public void removeItem(int position) {
        shoppageInfoList.remove(position);
        this.notifyDataSetChanged();
    }
}
