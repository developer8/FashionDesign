package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.ShopPageContent;
import com.fashion.binge.fashiondesign.classes.JSONUrl;
import com.fashion.binge.fashiondesign.models.ShoppageInfo;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import it.sephiroth.android.library.tooltip.Tooltip;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class AllShopPageListViewAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer, Filterable {
    private List<ShoppageInfo> shopPageModels;
    private List<ShoppageInfo> filterListModels;
    private LayoutInflater inflater;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private ValueFilter valueFilter;
    private Context context;
    String projectToken;
    MixpanelAPI mixpanel;

    public AllShopPageListViewAdapter(Context context, List<ShoppageInfo> shopPageModels) {
        inflater = LayoutInflater.from(context);
        this.shopPageModels = shopPageModels;
        this.filterListModels = shopPageModels;
        this.context = context;
        alphaIndexer = new HashMap<>();
        int size = this.shopPageModels.size();
        for (int x = 0; x < size; x++) {
            ShoppageInfo allShopPageModel = shopPageModels.get(x);
            String s = allShopPageModel.getSellerNickName();
            // get the first letter of the store
            String ch = s.substring(0, 1);
            // convert to uppercase otherwise lowercase a -z will be sorted
            // after upper A-Z
            ch = ch.toUpperCase();
            // put only if the key does not exist
            if (!alphaIndexer.containsKey(ch))
                alphaIndexer.put(ch,x);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<>(
                sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sections = sectionList.toArray(sections);
        getFilter();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.all_shop_list_header_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        ShoppageInfo allShopPageModel = this.shopPageModels.get(position);
        //set header text as first char in name
        String headerText = "" + allShopPageModel.getSellerNickName().subSequence(0, 1).charAt(0);
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        ShoppageInfo allShopPageModel = this.shopPageModels.get(position);
        return allShopPageModel.getSellerNickName().subSequence(0, 1).charAt(0);
    }

    @Override
    public int getCount() {
        return shopPageModels.size();
    }

    @Override
    public Object getItem(int position) {
        return shopPageModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.all_ship_list_row_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.avatar = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.countryFlag = (ImageView) convertView.findViewById(R.id.country_flag);
            holder.rowParent = (RelativeLayout) convertView.findViewById(R.id.row_parent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ShoppageInfo allShopPage = this.shopPageModels.get(position);
        holder.text.setText(allShopPage.getSellerNickName());
        Glide.with(context)
                .load(allShopPage.getSellerCountryFlag())
                .into(holder.countryFlag);
        holder.countryFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allShopPage.getSellerCountryName() != null){
                    Tooltip.removeAll(context);
                    Tooltip.make(context,new Tooltip.Builder(101).withStyleId(R.style.ToolTipLayoutHoianStyle)
                            .anchor(holder.countryFlag, Tooltip.Gravity.TOP)
                            .closePolicy(new Tooltip.ClosePolicy()
                                    .insidePolicy(true,false)
                                    .outsidePolicy(true,false),3000)
                            .activateDelay(800)
                            .showDelay(300)
                            .text(allShopPage.getSellerCountryName())
                            .maxWidth(500)
                            .withArrow(true)
                            .withOverlay(true)
                            .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                            .build()
                    ).show();
                }
            }
        });
        Glide.with(context)
                .load(JSONUrl.IMAGE_BASE_URL + allShopPage.getSellerAvater())
                .into(holder.avatar);
        holder.rowParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopPageContent.class);
                intent.putExtra("shop_banner", allShopPage.getSellerBanner());
                intent.putExtra("avatar", allShopPage.getSellerAvater());
                intent.putExtra("id", allShopPage.getSellerId());
                intent.putExtra("seller_name", allShopPage.getSellerNickName());
                intent.putExtra("seller_flag",allShopPage.getSellerCountryFlag());
                intent.putExtra("seller_country",allShopPage.getSellerCountryName());
                if (allShopPage.getUserFollow().equals("true")) {
                    intent.putExtra("following_text", "FOLLOWING");
                } else {
                    intent.putExtra("following_text", "FOLLOW");
                }

                projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
                mixpanel = MixpanelAPI.getInstance(context,projectToken);
                mixpanel.track(allShopPage.getSellerName() + " Shop Viewed", null);

                /*try {
                    JSONObject props = new JSONObject();
                    props.put("ShopPage", "Clicked");
                    props.put("SellerName",allShopPage.getSellerName());
                    props.put("Seller ID",allShopPage.getSellerId());
                    mixpanel.track("ShopDetailPage", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }*/

                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
        ImageView avatar;
        RelativeLayout rowParent;
        ImageView countryFlag;
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {

            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {

        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<ShoppageInfo> filterList = new ArrayList<>();
                for (int i = 0; i < filterListModels.size(); i++) {
                    if ((filterListModels.get(i).getSellerNickName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        ShoppageInfo allShopPageModel = new ShoppageInfo();
                        allShopPageModel.setSellerNickName(filterListModels.get(i).getSellerNickName());
                        allShopPageModel.setSellerAvater(filterListModels.get(i).getSellerAvater());
                        allShopPageModel.setSellerCountryFlag(filterListModels.get(i).getSellerCountryFlag());
                        allShopPageModel.setSellerId(filterListModels.get(i).getSellerId());
                        allShopPageModel.setUserFollow(filterListModels.get(i).getUserFollow());
                        allShopPageModel.setSellerBanner(filterListModels.get(i).getSellerBanner());
                        filterList.add(allShopPageModel);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filterListModels.size();
                results.values = filterListModels;
            }
            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            shopPageModels = (ArrayList<ShoppageInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
