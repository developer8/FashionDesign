package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


public class NewShopFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    public RecyclerView recyclerView;
    private List<ShoppageInfo> shoppageInfo;
    private int lastPosition = -1;

    public NewShopFragmentAdapter(Context context, RecyclerView recyclerView, List<ShoppageInfo> shoppageInfo) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.shoppageInfo = shoppageInfo;
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.newshopfragment_layout, parent, false);
        return new VHTYPEEVENITEM(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ShoppageInfo shoppageInfo = this.shoppageInfo.get(position);
        final VHTYPEEVENITEM vhtypeevenitem = (VHTYPEEVENITEM) holder;
        vhtypeevenitem.shopName.setText(shoppageInfo.getSellerNickName());
        vhtypeevenitem.follow.setTag(shoppageInfo.getSellerId());
        Animation animation = AnimationUtils.loadAnimation(context,
                (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = holder.getAdapterPosition();
        if (shoppageInfo.getUserFollow().equals("true")) {
            shoppageInfo.setClicked(true);
        } else {
            shoppageInfo.setClicked(false);
        }
        if (shoppageInfo.isClicked()) {
            vhtypeevenitem.follow.setText(R.string.following);
        } else {
            vhtypeevenitem.follow.setText(R.string.follow);
        }
        Glide.with(context)
                .load(shoppageInfo.getSellerCountryFlag())
                .into(vhtypeevenitem.bannerFlag);
        Glide.with(context)
                .load(JSONUrl.IMAGE_BASE_URL + shoppageInfo.getSellerBanner())
                .into(vhtypeevenitem.bannerImage);
        Glide.with(context)
                .load(JSONUrl.IMAGE_BASE_URL + shoppageInfo.getSellerAvater())
                .into(vhtypeevenitem.profileImage);
        vhtypeevenitem.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFollowUnfollowRequest(vhtypeevenitem.follow, shoppageInfo);
            }
        });

        vhtypeevenitem.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopPageContent.class);
                intent.putExtra("shop_banner", shoppageInfo.getSellerBanner());
                intent.putExtra("avatar", shoppageInfo.getSellerAvater());
                intent.putExtra("id", shoppageInfo.getSellerId());
                intent.putExtra("seller_flag",shoppageInfo.getSellerCountryFlag());
                intent.putExtra("seller_country",shoppageInfo.getSellerCountryName());
                intent.putExtra("seller_name", shoppageInfo.getSellerNickName());
                intent.putExtra("following_text", vhtypeevenitem.follow.getText().toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppageInfo.size();
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    class VHTYPEEVENITEM extends RecyclerView.ViewHolder {
        protected TextView shopName;
        protected ImageView profileImage;
        protected Button follow;
        protected LinearLayout parent;
        protected ImageView bannerImage;
        //protected ImageView sellerFlag;
        protected ImageView bannerFlag;

        public VHTYPEEVENITEM(View itemView) {
            super(itemView);
            parent = (LinearLayout) itemView.findViewById(R.id.parent);
            shopName = (TextView) itemView.findViewById(R.id.shop_name);
            follow = (Button) itemView.findViewById(R.id.follow);
            bannerFlag = (ImageView) itemView.findViewById(R.id.banner_image_flag);
          //  sellerFlag = (ImageView) itemView.findViewById(R.id.seller_country_flag);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            bannerImage = (ImageView) itemView.findViewById(R.id.banner_image);
            follow = (Button) itemView.findViewById(R.id.follow);
            parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    parent.getLayoutParams().height = (int) (parent.getWidth() * 0.65);
                    parent.requestLayout();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }, 1);

                }
            });
        }
    }
    private void sendFollowUnfollowRequest(final Button follow, final ShoppageInfo shoppageInfo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText("Following");
        pDialog.setCancelable(true);
        pDialog.show();
        if (follow.getText().equals("FOLLOW")) {
            new FollowUnfollowJson(context).setFollowJson(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), follow.getTag().toString(), new FollowUnfollowInfoHolder() {
                @Override
                public void setFollowUnfollowInfo(String success, String error) {
                    if (success.equals("true")) {
                        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        pDialog.setTitleText("Successfully followed");
                        follow.setText(R.string.following);
                        shoppageInfo.setUserFollow("true");
                        shoppageInfo.setClicked(true);
                    } else {
                        pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        pDialog.setTitleText(error);
                    }
                }
            });
        } else {
            new FollowUnfollowJson(context).setUnFollowJson(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), follow.getTag().toString(), new FollowUnfollowInfoHolder() {
                @Override
                public void setFollowUnfollowInfo(String success, String error) {
                    if (success.equals("true")) {
                        pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        pDialog.setTitleText("Successfully Unfollowed");
                        follow.setText(R.string.follow);
                        shoppageInfo.setUserFollow("false");
                        shoppageInfo.setClicked(true);
                    } else {
                        pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        pDialog.setTitleText(error);
                    }
                }
            });
        }
    }

}


