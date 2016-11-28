package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.ProductDetail;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.interfaces.ResponseInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.WishListCartClick;
import com.fashion.binge.fashiondesign.json.DeleteWishList;
import com.fashion.binge.fashiondesign.models.WishListModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import java.util.List;


public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.WishListViewHolder> {
    private Context context;
    private List<WishListModel> wishListModelList;
    private WishListCartClick wishListCartClick;
    private TextView noItem;

    public WishListAdapter(Context context, TextView noItem, List<WishListModel> wishListModelList, WishListCartClick wishListCartClick) {
        this.context = context;
        this.wishListModelList = wishListModelList;
        this.wishListCartClick = wishListCartClick;
        this.noItem = noItem;
    }

    @Override
    public WishListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.wishlist_recyler_list, parent, false);
        return new WishListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final WishListViewHolder holder, int position) {
        final WishListModel wishListModel = wishListModelList.get(position);
        holder.wishListImage.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        holder.wishListImage.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        holder.wishListImage.getWidth(),
                                        holder.wishListImage.getWidth()));
                    }
                });
        Glide.with(context)
                .load(wishListModel.getThumbImage())
                .into(holder.wishListImage);
        holder.productName.setText(wishListModel.getProductName());
        holder.price.setText(wishListModel.getPrice());
        holder.quantity.setVisibility(View.GONE);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id", wishListModel.getProductId());
                intent.putExtra("from","wishlist");
                context.startActivity(intent);
            }
        });
        holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
                pDialog.setTitleText("Deleting from wishlist");
                pDialog.setCancelable(true);
                pDialog.show();
                new DeleteWishList(context).deleteWishList(pDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), wishListModel.getProductId(), new ResponseInfoHolder() {
                    @Override
                    public void setFollowingInfo(String success, String data) {
                        if (success.equals("true")) {
                            pDialog.dismissWithAnimation();
                            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    removeItem(holder.getAdapterPosition());
                                }
                            });
                        } else {
                            pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                            pDialog.setTitleText(data);
                        }
                    }
                });
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos[] = new int[2];
                holder.cartImage.getLocationInWindow(pos);
                wishListCartClick.setWishListCartClick(pos[0], pos[1]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishListModelList.size();
    }

    private void removeItem(int position) {
        this.wishListModelList.remove(position);
        this.notifyItemRemoved(position);
        if (this.getItemCount() == 0) {
            noItem.setText(R.string.no_wishlist);
            noItem.setVisibility(View.VISIBLE);
        } else {
            noItem.setVisibility(View.INVISIBLE);
        }
    }

    public static class WishListViewHolder extends RecyclerView.ViewHolder {
        protected ImageView wishListImage;
        protected TextView productName, price;
        protected ImageView cartImage;
        protected RelativeLayout container;
        protected ImageView cross;
        protected LinearLayout linearLayout;
        protected TextView quantity;
        public WishListViewHolder(View v) {
            super(v);
            wishListImage = (ImageView) v.findViewById(R.id.wishlist_image);
            productName = (TextView) v.findViewById(R.id.item_name);
            price = (TextView) v.findViewById(R.id.price);
            cartImage = (ImageView) v.findViewById(R.id.cart_image);
            container = (RelativeLayout) v.findViewById(R.id.container);
            cross = (ImageView) v.findViewById(R.id.cross);
            linearLayout=(LinearLayout) v.findViewById(R.id.linear_layout);
            quantity=(TextView)v.findViewById(R.id.quantity);
        }
    }
}
