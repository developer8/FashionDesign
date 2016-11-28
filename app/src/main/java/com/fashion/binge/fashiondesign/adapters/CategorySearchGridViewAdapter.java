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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.ProductDetail;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.interfaces.AddToWIshListInterface;
import com.fashion.binge.fashiondesign.json.AddToWishlist;
import com.fashion.binge.fashiondesign.models.CategorySearchModel;
import com.fashion.binge.fashiondesign.shared_preference.SharedPrefrenceInfo;
import com.fashion.binge.fashiondesign.sweetalertdialog.SweetAlertDialog;

import java.util.List;


public class CategorySearchGridViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CategorySearchModel> categorySearchModelList;


    public CategorySearchGridViewAdapter(Context context, List<CategorySearchModel> categorySearchModelList) {
        this.context = context;
        this.categorySearchModelList = categorySearchModelList;
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
        final CategorySearchModel categorySearchModel = categorySearchModelList.get(position);
        VHitem.productName.setText(categorySearchModel.getName());
        VHitem.productName.setTag(categorySearchModel.getId());
        VHitem.price.setText(categorySearchModel.getPrice());
        Glide.with(context)
                .load(categorySearchModel.getImage())
                .into(VHitem.gridItemImage);
        VHitem.gridItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id",categorySearchModel.getId());
                intent.putExtra("from", "shoppage");
                intent.putExtra("json", categorySearchModel.getJsonId());
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
                new AddToWishlist(context).addToWishList(sharedPreferences.getString(SharedPrefrenceInfo.TOKEN_TYPE, "") + " " + sharedPreferences.getString(SharedPrefrenceInfo.IS_TOKEN_VALID, ""), categorySearchModel.getId(), new AddToWIshListInterface() {
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
        return categorySearchModelList.size();
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
