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
import it.sephiroth.android.library.tooltip.Tooltip;


public class ShopPageGridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private String cflag;
    private String cName;
    public static String followingText;
    private RecyclerView recyclerView;
    private Button activityFollowButton;
    private List<GetProductByShopModel> getProductByShopModelList;
    private String id;
    private static Button follow;

    public ShopPageGridViewAdapter(Context context, String followingText, String id, RecyclerView recyclerView, Button activityFollowButton, List<GetProductByShopModel> getProductByShopModelList,
                                   String sellerName, String shopBanner, String avatar, String cflag,String cName,
                                   ShopPageHeaderGridInterface shopPageHeaderGridInterface,
                                   ShopPageHeaderListInterface shopPageHeaderListInterface, ShopPageHeaderHelpInterface shopPageHeaderHelpInterface, ShopPageHeaderFilterInterface shopPageHeaderFilterInterface) {
        this.context = context;
        this.shopPageHeaderGridInterface = shopPageHeaderGridInterface;
        this.shopPageHeaderListInterface = shopPageHeaderListInterface;
        this.shopPageHeaderHelpInterface = shopPageHeaderHelpInterface;
        this.shopPageHeaderFilterInterface = shopPageHeaderFilterInterface;
        this.getProductByShopModelList = getProductByShopModelList;
        this.shopBanner = shopBanner;
        this.sellerName = sellerName;
        this.recyclerView = recyclerView;
        recyclerView.setVisibility(View.INVISIBLE);
        this.activityFollowButton = activityFollowButton;
        this.id = id;
        this.avatar = avatar;
        this.cflag = cflag;
        this.cName = cName;
        ShopPageGridViewAdapter.followingText = followingText;
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
            gridImage.setColorFilter(Color.parseColor("#999999"));
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_page_grid_layout, parent, false);
            final RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.grid_parent);
            // gridItemImage.setImageResource(R.drawable.winter_sale);
            relativeLayout.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            relativeLayout.setLayoutParams(
                                    new FrameLayout.LayoutParams(
                                            relativeLayout.getWidth(),
                                            (int) (relativeLayout.getWidth() * 1.2)));
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
                    .into(((VHHeader) holder).headerImage);
            //Glide.with(context)
                   // .load(this.cflag)
                  //  .into(((VHHeader) holder).headerFlag);
        } else if (holder instanceof VHItem) {
            final VHItem VHitem = (VHItem) holder;
            final GetProductByShopModel getProductByShopModel = getProductByShopModelList.get(position - 2);
            VHitem.productName.setText(getProductByShopModel.getName());
            VHitem.productName.setTag(getProductByShopModel.getId());
            VHitem.price.setText(getProductByShopModel.getPrice());
            if (getProductByShopModel.getWishlist().equals("0")) {
                VHitem.wishlist.setImageResource(R.mipmap.ic_heart);
            } else {
                VHitem.wishlist.setImageResource(R.mipmap.ic_heart_fill);
            }

            if (getProductByShopModel.getInternationShipping().equals("1")){
                VHitem.internationalShipping.setVisibility(View.VISIBLE);
            }
            else if (getProductByShopModel.getInternationShipping().equals("0")){
                VHitem.internationalShipping.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(getProductByShopModel.getImage())
                    .into(VHitem.gridItemImage);
            VHitem.gridItemImage.setOnClickListener(new View.OnClickListener() {
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
                                ShopPageGridViewAdapter.this.notifyDataSetChanged();
                                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                pDialog.setTitleText(result);
                            } else {
                                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                pDialog.setTitleText(result);
                            }
                        }
                    });
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    VHitem.gridParent.setVisibility(View.VISIBLE);
                }
            }, 500);

        } else if (holder instanceof VHTYPESUBITEM) {
            final VHTYPESUBITEM vhtypesubitem = (VHTYPESUBITEM) holder;
            vhtypesubitem.sellerName.setText(sellerName);
            vhtypesubitem.follow.setText(followingText);
            activityFollowButton.setText(followingText);
            Glide.with(context)
                    .load(this.cflag)
                    .into(vhtypesubitem.countryFlag);
            Glide.with(context)
                    .load(JSONUrl.IMAGE_BASE_URL + this.avatar)
                    .into(vhtypesubitem.avatar);
            vhtypesubitem.countryFlag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cName != null){
                        Tooltip.removeAll(context);
                        Tooltip.make(context,new Tooltip.Builder(101).withStyleId(R.style.ToolTipLayoutHoianStyle)
                                .anchor(vhtypesubitem.countryFlag, Tooltip.Gravity.TOP)
                                .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true,false)
                                .outsidePolicy(true,false),3000)
                                .activateDelay(800)
                                .showDelay(300)
                                .text(cName)
                                .maxWidth(500)
                                .withArrow(true)
                                .withOverlay(true)
                                .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                                .build()
                        ).show();
                    }
                }
            });
            vhtypesubitem.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vhtypesubitem.follow.getText().equals("FOLLOW")) {
                        //the first button is from adapter and second is from activity
                        followJson(vhtypesubitem.follow, ShopPageGridViewAdapter.this.activityFollowButton);
                    } else {
                        unFollowJson(vhtypesubitem.follow, ShopPageGridViewAdapter.this.activityFollowButton);
                    }
                }
            });
            this.activityFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ShopPageGridViewAdapter.this.activityFollowButton.getText().equals("FOLLOW")) {
                        followJson(vhtypesubitem.follow, ShopPageGridViewAdapter.this.activityFollowButton);
                    } else {
                        unFollowJson(vhtypesubitem.follow, ShopPageGridViewAdapter.this.activityFollowButton);
                    }
                }
            });
        }
    }

    public static Button returnFollowButton() {
        return follow;
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

    //increasing getItemcount to 2. This will be the row of header and sub ro.
    @Override
    public int getItemCount() {
        return getProductByShopModelList.size() + 2;
    }

    class VHHeader extends RecyclerView.ViewHolder {
        protected ImageView headerImage;
       // protected ImageView headerFlag;
        protected FrameLayout headerImageParent;

        public VHHeader(View itemView) {
            super(itemView);
         //   headerFlag = (ImageView) itemView.findViewById(R.id.country_flag_big);
            headerImage = (ImageView) itemView.findViewById(R.id.header_image);
            headerImageParent = (FrameLayout) itemView.findViewById(R.id.header_image_parent);
            headerImageParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    headerImageParent.getLayoutParams().height = (int) (headerImageParent.getWidth() * 0.60);
                    headerImageParent.requestLayout();
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

    class VHTYPESUBITEM extends RecyclerView.ViewHolder {
        protected TextView sellerName;
        protected Button follow;
        protected ImageView countryFlag;
        protected CircleImageView avatar;

        public VHTYPESUBITEM(View itemView) {
            super(itemView);
            sellerName = (TextView) itemView.findViewById(R.id.seller_name);
            follow = (Button) itemView.findViewById(R.id.follow);
            avatar = (CircleImageView) itemView.findViewById(R.id.profile_image);
            countryFlag = (ImageView) itemView.findViewById(R.id.seller_country_flag);
            ShopPageGridViewAdapter.follow = follow;
        }
    }

    class VHItem extends RecyclerView.ViewHolder {
        protected ImageView gridItemImage;
        protected TextView productName;
        protected TextView price;
        protected RelativeLayout gridParent;
        protected ImageView wishlist;
        protected ImageView internationalShipping;

        public VHItem(View itemView) {
            super(itemView);
            gridItemImage = (ImageView) itemView.findViewById(R.id.grid_item_image);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.price);
            gridParent = (RelativeLayout) itemView.findViewById(R.id.grid_parent);
            wishlist = (ImageView) itemView.findViewById(R.id.wishlist);
            internationalShipping = (ImageView) itemView.findViewById(R.id.isimage);
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
                    //make change in the shoppagelistview adapter class
                    ShopPageListViewAdapter.followingText = "FOLLOWING";
                    ShopPageListViewAdapter.returnFollowButton().setText(R.string.following);
                    //sending the data in broadcast receiver to indicate in the carousel view that the follow button has
                    //been clicked and the button should be updated at onBackPressed.
                    Intent intent = new Intent("follow_event");
                    intent.putExtra("message", id);
                    intent.putExtra("type", "follow");
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
                    //make change in the shoppagelistview adapter class
                    ShopPageListViewAdapter.followingText = "FOLLOW";
                    ShopPageListViewAdapter.returnFollowButton().setText(R.string.follow);
                    //sending the data in broadcast receiver to indicate in the carousel view that the follow button has
                    //been clicked and the button should be updated at onBackPressed.
                    Intent intent = new Intent("follow_event");
                    intent.putExtra("message", id);
                    intent.putExtra("type", "unfollow");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else {
                    pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                    pDialog.setTitleText(error);
                }
            }
        });
    }
}
