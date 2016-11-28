package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.Cart;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.models.GetCartData;

import java.util.List;


public class CompleteRecyclerAdapter extends RecyclerView.Adapter<CompleteRecyclerAdapter.CartViewHolder> {
    private Context context;
    private List<GetCartData> getCartList;

    public CompleteRecyclerAdapter(Context context, List<GetCartData> getCartList) {
        this.context = context;
        this.getCartList = getCartList;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.complete_recyler_list, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, int position) {
        GetCartData getCartData = getCartList.get(position);
        holder.itemName.setText(getCartData.getName());
        holder.quantity.setText("Quantity " + Html.fromHtml("<b>" + getCartData.getQuantity() + "</b>"));
        holder.price.setText(getCartData.getPrice());
        holder.size.setText(getCartData.getSize());
        if (holder.size.getText().equals("")) {
            holder.sizeParent.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(getCartData.getThumb())
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return getCartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        protected ImageView productImage;
        protected TextView itemName;
        protected TextView quantity;
        protected TextView size, color;
        protected LinearLayout parent, sizeParent;
        private TextView price;

        public CartViewHolder(View v) {
            super(v);
            productImage = (ImageView) v.findViewById(R.id.product_image);
            parent = (LinearLayout) v.findViewById(R.id.linear_layout);
            itemName = (TextView) v.findViewById(R.id.item_name);
            price = (TextView) v.findViewById(R.id.price);
            size = (TextView) v.findViewById(R.id.size);
            color = (TextView) v.findViewById(R.id.color);
            quantity = (TextView) v.findViewById(R.id.quantity);
            sizeParent = (LinearLayout) v.findViewById(R.id.size_parent);
            if (Cart.isTablet(context)) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                productImage.setLayoutParams(layoutParams);
            }
            parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    productImage.getLayoutParams().height = productImage.getWidth();
                    productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    parent.requestLayout();
                }
            });

        }
    }
}
