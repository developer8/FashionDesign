package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.ProductDetail;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.interfaces.AddToWIshListInterface;
import com.fashion.binge.fashiondesign.json.AddToWishlist;
import com.fashion.binge.fashiondesign.models.GetProductByShopModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ProductListGridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<GetProductByShopModel> GetProductByShopModelList;
    String projectToken;
    MixpanelAPI mixpanel;


    public ProductListGridViewAdapter(Context context,List<GetProductByShopModel> GetProductByShopModelList) {
        this.context = context;
        this.GetProductByShopModelList = GetProductByShopModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_page_grid_layout, parent, false);
        final RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.grid_parent);
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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final VHItem VHitem = (VHItem) holder;
        final GetProductByShopModel getProductByShopModel = GetProductByShopModelList.get(position);
        VHitem.productName.setText(getProductByShopModel.getName());
        VHitem.productName.setTag(getProductByShopModel.getId());
        VHitem.price.setText(getProductByShopModel.getPrice());
        Glide.with(context)
                .load(getProductByShopModel.getImage())
                .into(VHitem.gridItemImage);
        VHitem.gridItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id",getProductByShopModel.getId());
                intent.putExtra("from", "shoppage");
                intent.putExtra("json", getProductByShopModel.getJsonId());

                projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
                mixpanel = MixpanelAPI.getInstance(context,projectToken);

                try {
                    JSONObject props = new JSONObject();
                    props.put("ProductDetail", "Clicked");
                    props.put("From","Shoppage GridView");
                    props.put("Product ID : ",getProductByShopModel.getId());
                    mixpanel.track("ClickedOnProduct", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VHitem.gridParent.setVisibility(View.VISIBLE);
            }
        }, 500);
    }


    @Override
    public int getItemCount() {
        return GetProductByShopModelList.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        protected ImageView gridItemImage;
        protected TextView productName;
        protected TextView price;
        protected RelativeLayout gridParent;
        protected ImageView wishlist;

        public VHItem(View itemView) {
            super(itemView);
            gridItemImage = (ImageView) itemView.findViewById(R.id.grid_item_image);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            price = (TextView) itemView.findViewById(R.id.price);
            gridParent = (RelativeLayout) itemView.findViewById(R.id.grid_parent);
            wishlist = (ImageView) itemView.findViewById(R.id.wishlist);
        }
    }
}
