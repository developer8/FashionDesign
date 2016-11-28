package com.fashion.binge.fashiondesign.adapters;import android.content.Context;import android.content.Intent;import android.graphics.Color;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;import android.widget.LinearLayout;import android.widget.TextView;import com.fashion.binge.fashiondesign.OrderDetailActivity;import com.fashion.binge.fashiondesign.R;import com.fashion.binge.fashiondesign.models.OrderListModel;import java.util.List;import de.hdodenhof.circleimageview.CircleImageView;public class OrderListAdapter extends BaseAdapter {    private Context context;    private List<OrderListModel> orderListModelList;    public OrderListAdapter(Context context, List<OrderListModel> orderListModelList) {        this.context = context;        this.orderListModelList = orderListModelList;    }    @Override    public int getCount() {        return orderListModelList.size();    }    @Override    public Object getItem(int position) {        return position;    }    @Override    public long getItemId(int position) {        return position;    }    @Override    public View getView(int position, View convertView, ViewGroup parent) {        if (convertView == null) {            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);            convertView = layoutInflater.inflate(R.layout.order_list_layout_content, parent, false);        }        final OrderListModel orderListModel = orderListModelList.get(position);        final LinearLayout container = (LinearLayout) convertView.findViewById(R.id.parent);        TextView orderId = (TextView) convertView.findViewById(R.id.order_id);        TextView name = (TextView) convertView.findViewById(R.id.name);        TextView addedDate = (TextView) convertView.findViewById(R.id.added_date);        TextView noOfProducts = (TextView) convertView.findViewById(R.id.no_of_products);        TextView status = (TextView) convertView.findViewById(R.id.status);        TextView total = (TextView) convertView.findViewById(R.id.total);        CircleImageView statusImage = (CircleImageView) convertView.findViewById(R.id.status_image);        orderId.setText(String.format("Id %s", orderListModel.getOrderId()));        name.setText(orderListModel.getName());        addedDate.setText(orderListModel.getDateAdded());        noOfProducts.setText(String.format("No of Products:  %s", orderListModel.getProducts()));        status.setText(orderListModel.getOrderStatus());        total.setText(orderListModel.getTotal());        switch (orderListModel.getOrderStatus()) {            case "Complete":                statusImage.setImageResource(R.mipmap.ic_order_tick);                status.setTextColor(Color.parseColor("#45a117"));                break;            case "Processing":                statusImage.setImageResource(R.mipmap.ic_order_proccessing);                status.setTextColor(Color.parseColor("#c4a30c"));                break;            default:                statusImage.setImageResource(R.mipmap.ic_order_failed);                status.setTextColor(Color.parseColor("#da4b2f"));                break;        }        container.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                Intent intent = new Intent(context, OrderDetailActivity.class);                intent.putExtra("id",orderListModel.getOrderId());                intent.putExtra("date_added",orderListModel.getDateAdded());                intent.putExtra("status",orderListModel.getOrderStatus());                context.startActivity(intent);            }        });        return convertView;    }}