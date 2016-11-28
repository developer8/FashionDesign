package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.ProductDetail;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.interfaces.AddToWIshListInterface;
import com.fashion.binge.fashiondesign.interfaces.FollowUnfollowInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderFilterInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderGridInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderHelpInterface;
import com.fashion.binge.fashiondesign.interfaces.ShopPageHeaderListInterface;
import com.fashion.binge.fashiondesign.json.AddToWishlist;
import com.fashion.binge.fashiondesign.json.FollowUnfollowJson;
import com.fashion.binge.fashiondesign.models.GetProductByShopModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ShopPageListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_SUB_ITEM = 2;
    private Context context;
    private ShopPageHeaderGridInterface shopPageHeaderGridInterface;
    private ShopPageHeaderListInterface shopPageHeaderListInterface;
    private ShopPageHeaderFilterInterface shopPageHeaderFilterInterface;
    private ShopPageHeaderHelpInterface shopPageHeaderHelpInterface;
    private String shopBanner;
    private String sellerName;
    private String avatar;
    private Button activityFollowButton;
    private String id;
    public static String followingText;
    private static Button follow;
    private String cName;
    //this is made to make the current view follow button to make visible to gridlistview adapter
    //so that the follow clicked in gridlistview will also change in this view
    private List<GetProductByShopModel> getProductByShopModelList;

    public ShopPageListViewAdapter(Context context, String followingText, Button activityFollowButton, String cName ,String id, List<GetProductByShopModel> getProductByShopModelList, String sellerName, String shopBanner, String avatar, ShopPageHeaderGridInterface shopPageHeaderGridInterface, ShopPageHeaderListInterface shopPageHeaderListInterface, ShopPageHeaderHelpInterface shopPageHeaderHelpInterface, ShopPageHeaderFilterInterface shopPageHeaderFilterInterface) {
        this.context = context;
        this.shopPageHeaderGridInterface = shopPageHeaderGridInterface;
        this.shopPageHeaderListInterface = shopPageHeaderListInterface;
        this.shopPageHeaderHelpInterface = shopPageHeaderHelpInterface;
        this.shopPageHeaderFilterInterface = shopPageHeaderFilterInterface;
        this.getProductByShopModelList = getProductByShopModelList;
        this.shopBanner = shopBanner;
        this.sellerName = sellerName;
        this.avatar = avatar;
        this.activityFollowButton = activityFollowButton;
        this.id = id;
        this.cName = cName;
        ShopPageListViewAdapter.followingText = followingText;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_page_grid_layout_header, parent, false);
            return new VHHeader(v);
        } else if (viewType == TYPE_SUB_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_header, parent, false);
            final ImageView stackImage = (ImageView) v.findViewById(R.id.stack_image);
            ImageView helpImage = (ImageView) v.findViewById(R.id.help);
            final ImageView filterImage = (ImageView) v.findViewById(R.id.filter_image);
            final ImageView gridImage = (ImageView) v.findViewById(R.id.grid_image);
            stackImage.setColorFilter(Color.parseColor("#999999"));
            stackImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopPageHeaderGridInterface.onClick(true);
                }
            });
            gridImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopPageHeaderListInterface.onClick(true);
                }
            });
            helpImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopPageHeaderHelpInterface.onClick(true);
                }
            });
            filterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shopPageHeaderFilterInterface.onClick(true);
                }
            });
            return new VHTYPESUBITEM(v);
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_page_list_layout, parent, false);
            final RelativeLayout gridParent = (RelativeLayout) v.findViewById(R.id.grid_parent);
            final ImageView image = (ImageView) v.findViewById(R.id.image);
            gridParent.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            image.setLayoutParams(
                                    new RelativeLayout.LayoutParams(
                                            image.getWidth(),
                                            (int) (gridParent.getWidth() * 0.70)));
                        }
                    });
            return new VHItem(v);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHHeader) {
            Glide.with(context)
                    .load(JSONUrl.IMAGE_BASE_URL + shopBanner)
                    .centerCrop()
                    .into(((VHHeader) holder).headerImage);
            //Glide.with(context)
                    //.load()
        } else if (holder instanceof VHItem) {
            final VHItem VHitem = (VHItem) holder;
            final GetProductByShopModel getProductByShopModel = getProductByShopModelList.get(position - 2);
            VHitem.itemName.setText(getProductByShopModel.getName());
            VHitem.price.setText(getProductByShopModel.getPrice());
            if (getProductByShopModel.getInternationShipping().equals("1")){
                VHitem.internationShipping.setVisibility(View.VISIBLE);
            }
            else if (getProductByShopModel.getInternationShipping().equals("0")){
                VHitem.internationShipping.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(getProductByShopModel.getImage())
                    .into(VHitem.image);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    VHitem.gridParent.setVisibility(View.VISIBLE);
                }
            }, 500);
            if (getProductByShopModel.getWishlist().equals("0")) {
                VHitem.wishlist.setImageResource(R.mipmap.ic_heart);
            } else {
                VHitem.wishlist.setImageResource(R.mipmap.ic_heart_fill);
            }
            VHitem.wishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                    pDialog.setTitleText("Adding to wishlist");
                    pDialog.setCancelable(true);
                    pDialog.show();
                    VHitem.wishlist.setImageResource(R.mipmap.ic_heart_non_fill);
                    new AddToWishlist(context).addToWishList(sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), getProductByShopModel.getId(), new AddToWIshListInterface() {
                        @Override
                        public void addToWishList(String result) {
                            if (result.equals("Successfully added to wishlist")) {
                                getProductByShopModel.setWishlist("1");
                                ShopPageListViewAdapter.this.notifyDataSetChanged();
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText(result);
                                VHitem.wishlist.setImageResource(R.mipmap.ic_heart_fill);
                            } else {
                                VHitem.wishlist.setImageResource(R.mipmap.ic_heart);
                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(result);
                            }
                        }
                    });
                }
            });
            VHitem.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetail.class);
                    intent.putExtra("id", getProductByShopModel.getId());
                    intent.putExtra("from", "shoppage");
                    intent.putExtra("json", getProductByShopModel.getJsonId());
                    intent.putExtra("shop_country",cName);
                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof VHTYPESUBITEM) {
            final VHTYPESUBITEM vhtypesubitem = (VHTYPESUBITEM) holder;
            vhtypesubitem.sellerName.setText(sellerName);
            vhtypesubitem.follow.setText(followingText);
            Glide.with(context)
                    .load(JSONUrl.IMAGE_BASE_URL + this.avatar)
                    .into(vhtypesubitem.avatar);
            vhtypesubitem.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vhtypesubitem.follow.getText().equals("FOLLOW")) {
                        //the first button is from adapter and second is from activity
                        followJson(vhtypesubitem.follow, ShopPageListViewAdapter.this.activityFollowButton);
                    }else{
                        unFollowJson(vhtypesubitem.follow, ShopPageListViewAdapter.this.activityFollowButton);
                    }
                }
            });
            this.activityFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ShopPageListViewAdapter.this.activityFollowButton.getText().equals("FOLLOW")) {
                        followJson(vhtypesubitem.follow, ShopPageListViewAdapter.this.activityFollowButton);
                    }else{
                        unFollowJson(vhtypesubitem.follow, ShopPageListViewAdapter.this.activityFollowButton);
                    }
                }
            });
        }
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionSubHeader(position)) {
            return TYPE_SUB_ITEM;
        }
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return position == 0;
    }

    public boolean isPositionSubHeader(int position) {
        return position == 1;
    }

    //increasing getItemcount to 1. This will be the row of header.
    @Override
    public int getItemCount() {
        return getProductByShopModelList.size() + 2;
    }

    public static Button returnFollowButton() {
        return follow;
    }

    class VHHeader extends RecyclerView.ViewHolder {
        protected ImageView headerImage;
        protected FrameLayout headerImageParent;
        protected ImageView headerFlag;

        public VHHeader(View itemView) {
            super(itemView);
            headerImage = (ImageView) itemView.findViewById(R.id.header_image);
            headerFlag = (ImageView) itemView.findViewById(R.id.country_flag_big);
            headerImageParent = (FrameLayout) itemView.findViewById(R.id.header_image_parent);
            headerImageParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    headerImageParent.getLayoutParams().height = (int) (headerImageParent.getWidth() * 0.60);
                    headerImageParent.requestLayout();
                }
            });
        }
    }

    class VHTYPESUBITEM extends RecyclerView.ViewHolder {
        protected TextView sellerName;
        protected Button follow;
        protected CircleImageView avatar;

        public VHTYPESUBITEM(View itemView) {
            super(itemView);
            sellerName = (TextView) itemView.findViewById(R.id.seller_name);
            follow = (Button) itemView.findViewById(R.id.follow);
            avatar = (CircleImageView) itemView.findViewById(R.id.profile_image);
            ShopPageListViewAdapter.follow = follow;
            activityFollowButton.setText(followingText);
        }
    }

    static class VHItem extends RecyclerView.ViewHolder {
        protected RelativeLayout gridParent;
        protected ImageView image;
        protected TextView itemName;
        protected TextView price;
        protected ImageView wishlist;
        protected ImageView internationShipping;

        public VHItem(View itemView) {
            super(itemView);
            gridParent = (RelativeLayout) itemView.findViewById(R.id.grid_parent);
            image = (ImageView) itemView.findViewById(R.id.image);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.price);
            wishlist = (ImageView) itemView.findViewById(R.id.wishlist);
            internationShipping = (ImageView) itemView.findViewById(R.id.isimage);
        }
    }

    private void followJson(final Button adapterFollowButton, final Button activityFollowButton) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText("Following");
        pDialog.setCancelable(true);
        pDialog.show();
        new FollowUnfollowJson(context).setFollowJson(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), id, new FollowUnfollowInfoHolder() {
            @Override
            public void setFollowUnfollowInfo(String success, String error) {
                if (success.equals("true")) {
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("Successfully followed");
                    adapterFollowButton.setText(R.string.following);
                    activityFollowButton.setText(R.string.following);
                    followingText = "FOLLOWING";
                    //this will update in the shoppage gridview class
                    ShopPageGridViewAdapter.followingText = "FOLLOWING";
                    ShopPageGridViewAdapter.returnFollowButton().setText(R.string.following);
                    Intent intent = new Intent("follow_event");
                    intent.putExtra("message", id);
                    intent.putExtra("type","follow");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else {
                    pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(error);
                }
            }
        });
    }

    private void unFollowJson(final Button adapterFollowButton, final Button activityFollowButton) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
        pDialog.setTitleText("Unfollowing");
        pDialog.setCancelable(true);
        pDialog.show();
        new FollowUnfollowJson(context).setUnFollowJson(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), id, new FollowUnfollowInfoHolder() {
            @Override
            public void setFollowUnfollowInfo(String success, String error) {
                if (success.equals("true")) {
                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    pDialog.setTitleText("Successfully Unfollowed");
                    adapterFollowButton.setText(R.string.follow);
                    activityFollowButton.setText(R.string.follow);
                    followingText = "FOLLOW";
                    //this will update in the shoppage gridview class
                    ShopPageGridViewAdapter.followingText = "FOLLOW";
                    ShopPageGridViewAdapter.returnFollowButton().setText(R.string.follow);
                    Intent intent = new Intent("follow_event");
                    intent.putExtra("message", id);
                    intent.putExtra("type","unfollow");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else {
                    pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(error);
                }
            }
        });
    }
}
