package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.Cart;
import com.fashion.binge.fashiondesign.ProductDetail;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.interfaces.CartResponseInfoHolder;
import com.fashion.binge.fashiondesign.interfaces.DeleteCartInterface;
import com.fashion.binge.fashiondesign.interfaces.UpdateCartInfoHolder;
import com.fashion.binge.fashiondesign.json.DeleteCart;
import com.fashion.binge.fashiondesign.models.CartModel;
import com.fashion.binge.fashiondesign.models.GetCartData;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder> {
    private Context context;
    private UpdateCartInfoHolder updateCartInfoHolder;
    private List<GetCartData> cartModel;
    private TextView noItem, totalValue;
    private DeleteCartInterface deleteCartInterface;

    public CartRecyclerAdapter(Context context, TextView noItem, TextView totalValue, CartModel cartModel, UpdateCartInfoHolder updateCartInfoHolder, DeleteCartInterface deleteCartInterface) {
        this.context = context;
        this.updateCartInfoHolder = updateCartInfoHolder;
        this.cartModel = cartModel.getGetCartDataList();
        this.noItem = noItem;
        this.totalValue = totalValue;
        this.deleteCartInterface = deleteCartInterface;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.cart_recyler_list, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, int position) {
        final GetCartData getCartData = cartModel.get(position);
        Glide.with(context)
                .load(getCartData.getThumb())
                .into(holder.productImage);
        Glide.with(context)
                .load(getCartData.getColor())
                .into(holder.colorImage);
        holder.size.setText(getCartData.getSize());
        if (holder.size.getText().equals("")) {
            holder.sizeTitle.setVisibility(View.GONE);
        }
        holder.itemName.setText(getCartData.getName());
        holder.quantity.setText(getCartData.getQuantity());
        holder.quantity.setTag(getCartData.getKey());
        holder.sellerName.setText(getCartData.getSellerName());
        holder.couponName.setText(getCartData.getCouponName());
        if (holder.couponName.getText().equals("")) {
            holder.couponIcon.setVisibility(View.GONE);
        }
        //noinspection deprecation
        holder.price.setText(Html.fromHtml("<b>" + getCartData.getPrice() + "</b><font color='#c6c7c7'><sub><small></small></sub></font>"));
        if (Cart.isTablet(context)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            holder.productImage.setLayoutParams(layoutParams);
        }
        holder.increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCartData.getStock().equals("true")) {
                    int inc = Integer.parseInt(holder.quantity.getText().toString()) + 1;
                    holder.quantity.setText(String.valueOf(inc));
                    updateCartInfoHolder.onCartUpdate(holder.quantity.getTag().toString(), holder.quantity.getText().toString(), holder.quantity, "increase");
                } else {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                    sweetAlertDialog.setTitleText("Quantity limit exceed");
                    sweetAlertDialog.show();
                }
            }
        });
        holder.decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.quantity.getText().toString()) > 1) {
                    int inc = Integer.parseInt(holder.quantity.getText().toString()) - 1;
                    holder.quantity.setText(String.valueOf(inc));
                    updateCartInfoHolder.onCartUpdate(holder.quantity.getTag().toString(), holder.quantity.getText().toString(), holder.quantity, "decrease");
                } else {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.shake_animation);
                    holder.decreaseQuantity.startAnimation(animation);
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProductFromCart(getCartData.getKey(), holder.getAdapterPosition());
            }
        });
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("from", "cart");
                intent.putExtra("id", getCartData.getProductId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartModel.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        protected ImageView productImage;
        protected LinearLayout parent;
        private ImageView increaseQuantity;
        private ImageView decreaseQuantity;
        private TextView quantity;
        protected TextView price;
        protected LinearLayout shopParent;
        protected TextView itemName;
        protected TextView size;
        protected ImageView delete;
        protected CircleImageView colorImage;
        protected TextView sellerName;
        protected TextView sizeTitle;
        protected TextView couponName;
        private ImageView couponIcon;

        public CartViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            increaseQuantity = (ImageView) v.findViewById(R.id.increase_quantity);
            decreaseQuantity = (ImageView) v.findViewById(R.id.decrease_quantity);
            quantity = (TextView) v.findViewById(R.id.quantity);
            parent = (LinearLayout) v.findViewById(R.id.linear_layout);
            price = (TextView) v.findViewById(R.id.price);
            shopParent = (LinearLayout) v.findViewById(R.id.shop_parent);
            itemName = (TextView) v.findViewById(R.id.item_name);
            delete = (ImageView) v.findViewById(R.id.delete);
            size = (TextView) v.findViewById(R.id.size);
            colorImage = (CircleImageView) v.findViewById(R.id.color_image);
            sellerName = (TextView) v.findViewById(R.id.seller_name);
            sizeTitle = (TextView) v.findViewById(R.id.size_title);
            couponName = (TextView) v.findViewById(R.id.coupon_name);
            couponIcon = (ImageView) v.findViewById(R.id.coupon_icon);
            parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    productImage.getLayoutParams().height = productImage.getWidth();
                    parent.requestLayout();
                }
            });
        }
    }

    private void deleteProductFromCart(String productKey, final int position) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("product_id", productKey + "::");
            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Updating cart");
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#F6705E"));
            sweetAlertDialog.show();
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            new DeleteCart(context).deleteCart(sweetAlertDialog, sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), jsonObject, new CartResponseInfoHolder() {
                @Override
                public void setCartInfo(String success, String data, String totalProductCount) {
                    if (success.equals("true")) {
                        String total = data.replaceAll(".*-", "");
                        totalValue.setText(total);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SharedPrefrenceInfo.CART_COUNT, totalProductCount);
                        editor.apply();
                        removeItem(position);
                        deleteCartInterface.onCartDelete(sweetAlertDialog);
                    } else {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        sweetAlertDialog.setTitleText("Cannot delete item");
                    }
                }
            });
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void removeItem(int position) {
        this.cartModel.remove(position);
        this.notifyItemRemoved(position);
        if (this.getItemCount() == 0) {
            noItem.setText(R.string.no_cart);
            noItem.setVisibility(View.VISIBLE);
        } else {
            noItem.setVisibility(View.INVISIBLE);
        }
    }
}
