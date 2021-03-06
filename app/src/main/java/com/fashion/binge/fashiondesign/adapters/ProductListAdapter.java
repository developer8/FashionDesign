package com.fashion.binge.fashiondesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.fashion.binge.fashiondesign.ProductDetail;
import com.fashion.binge.fashiondesign.R;
import com.fashion.binge.fashiondesign.models.ProductList;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class ProductListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer, Filterable {
    private List<ProductList> productListModels;
    private List<ProductList> filterListModels;
    private LayoutInflater inflater;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;
    private ValueFilter valueFilter;
    private Context context;
    String projectToken;
    MixpanelAPI mixpanel;

    public ProductListAdapter(Context context, List<ProductList> productListModels) {
        inflater = LayoutInflater.from(context);
        this.productListModels=productListModels;
        this.filterListModels = productListModels;
        this.context = context;
        alphaIndexer = new HashMap<>();
        int size = this.productListModels.size();
        for (int x = 0; x < size; x++) {
            ProductList productList = productListModels.get(x);
            String s = productList.getName();
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
        ProductList productList = this.productListModels.get(position);
        //set header text as first char in name
        String headerText = "" + productList.getName().subSequence(0, 1).charAt(0);
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        ProductList productList = this.productListModels.get(position);
        return productList.getName().subSequence(0, 1).charAt(0);
    }

    @Override
    public int getCount() {
        return productListModels.size();
    }

    @Override
    public Object getItem(int position) {
        return productListModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.all_ship_list_row_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.avatar = (ImageView) convertView.findViewById(R.id.profile_image);
            holder.rowParent = (RelativeLayout) convertView.findViewById(R.id.row_parent);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ProductList productList = this.productListModels.get(position);
        holder.rowParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetail.class);
                intent.putExtra("id", productList.getId());
                intent.putExtra("from","product_list");

                projectToken = "8fdaec76ab6e8d1bccc743cb076c3707";
                mixpanel = MixpanelAPI.getInstance(context,projectToken);

                try {
                    JSONObject props = new JSONObject();
                    props.put("ProductDetail", "Clicked");
                    props.put("From","ProductList");
                    props.put("Product ID : ",productList.getId());
                    mixpanel.track("ClickedOnProduct", props);
                } catch (JSONException e) {
                    Log.e("MYAPP", "Unable to add properties to JSONObject", e);
                }

                context.startActivity(intent);
            }
        });
        holder.text.setText(productList.getName());
        Glide.with(context)
                .load(productList.getImage())
                .into(holder.avatar);
        return convertView;
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView text;
        ImageView avatar;
        RelativeLayout rowParent;
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
                ArrayList<ProductList> filterList = new ArrayList<>();
                for (int i = 0; i < filterListModels.size(); i++) {
                    if ((filterListModels.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        ProductList productList = new ProductList();
                        productList.setName(filterListModels.get(i).getName());
                        productList.setImage(filterListModels.get(i).getImage());
                        productList.setId(filterListModels.get(i).getId());
                        filterList.add(productList);
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
            productListModels = (ArrayList<ProductList>) results.values;
            notifyDataSetChanged();
        }
    }
}
