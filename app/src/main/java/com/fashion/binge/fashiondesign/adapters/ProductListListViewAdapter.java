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


public class ProductListListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<GetProductByShopModel> getProductByShopModelList;
    String projectToken;
    MixpanelAPI mixpanel;

    public ProductListListViewAdapter(Context context, List<GetProductByShopModel> getProductByShopModelList) {
        this.context = context;
        this.getProductByShopModelList = getProductByShopModelList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final VHItem VHitem = (VHItem) holder;
        final GetProductByShopModel getProductByShopModel = getProductByShopModelList.get(position);
        VHitem.itemName.setText(getProductByShopModel.getName());
        VHitem.price.setText(getProductByShopModel.getPrice());
        Glide.with(context)
                .load(getProductByShopModel.getImage())
                .into(VHitem.image);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VHitem.gridParent.setVisibility(View.VISIBLE);
            }
        }, 500);
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
        VHitem.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id", getProductByShopModel.getId());
                intent.putExtra("from", "shoppage");
                intent.putExtra("json", getProductByShopModel.getJsonId());

                projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
                mixpanel = MixpanelAPI.getInstance(context,projectToken);

                try {
                    JSONObject props = new JSONObject();
                    props.put("ProductDetail", "Clicked");
                    props.put("From","Shoppage");
                    props.put("Product ID : ",getProductByShopModel.getId());
                    mixpanel.track("ClickedOnProduct", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getProductByShopModelList.size();
    }

    static class VHItem extends RecyclerView.ViewHolder {
        protected RelativeLayout gridParent;
        protected ImageView image;
        protected TextView itemName;
        protected TextView price;
        protected ImageView wishlist;

        public VHItem(View itemView) {
            super(itemView);
            gridParent = (RelativeLayout) itemView.findViewById(R.id.grid_parent);
            image = (ImageView) itemView.findViewById(R.id.image);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            price = (TextView) itemView.findViewById(R.id.price);
            wishlist = (ImageView) itemView.findViewById(R.id.wishlist);
        }
    }

}
