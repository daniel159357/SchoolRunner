package com.example.schoolrunner.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolrunner.R;
import com.example.schoolrunner.model.entity.Order;
import com.gzone.university.utils.DateUtils;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnItemClickListener listener;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set order type
        holder.tvOrderType.setText(order.getOrderType());

        // Set order type icon and background
        setOrderTypeIconAndBackground(holder, order.getOrderType());

        // Set order description
        holder.tvDescription.setText(order.getItemDescription());

        // Set address
        holder.tvAddress.setText(order.getPickupAddress() + " → " + order.getDeliveryAddress());

        // Set delivery time
        String deliveryTime = "Deliver as soon as possible";
        if (order.getDeliveryTime() != null) {
            deliveryTime = "Expected to deliver before " + DateUtils.format(order.getDeliveryTime());
        }
        holder.tvDeliveryTime.setText(deliveryTime);

        // Set price
        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvPrice.setText("Reward ¥" + df.format(order.getPrice()));

        // Set user name
        if (order.getStudent() != null) {
            holder.tvUserName.setText(order.getStudent().getName());
        }

        // Accept order button click event
        holder.btnAcceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAcceptButtonClick(order, holder.getAdapterPosition());
                }
            }
        });

        // Whole item click event
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(order, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    /**
     * Set order type icon and background color
     */
    private void setOrderTypeIconAndBackground(OrderViewHolder holder, String orderType) {
        int bgColor;
        int iconColor;
        int iconResId;

        // Set different icons and background colors according to order type
        switch (orderType) {
            case "Express Pickup":
                iconResId = R.drawable.ic_express;
                bgColor = Color.parseColor("#E3F2FD");
                iconColor = Color.parseColor("#2196F3");
                break;
            case "Food Delivery":
                iconResId = R.drawable.ic_food;
                bgColor = Color.parseColor("#FFEBEE");
                iconColor = Color.parseColor("#F44336");
                break;
            case "Document Printing":
                iconResId = R.drawable.ic_print;
                bgColor = Color.parseColor("#E8F5E9");
                iconColor = Color.parseColor("#4CAF50");
                break;
            case "Other Services":
                iconResId = R.drawable.ic_other;
                bgColor = Color.parseColor("#EDE7F6");
                iconColor = Color.parseColor("#9C27B0");
                break;
            default:
                iconResId = R.drawable.ic_other;
                bgColor = Color.parseColor("#EEEEEE");
                iconColor = Color.parseColor("#9E9E9E");
                break;
        }

        holder.cvOrderTypeBg.setCardBackgroundColor(bgColor);
        holder.ivOrderTypeIcon.setImageResource(iconResId);
        holder.ivOrderTypeIcon.setColorFilter(iconColor);
    }

    /**
     * Update data
     */
    public void updateData(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    /**
     * Add data
     */
    public void addData(List<Order> newOrders) {
        if (newOrders != null && newOrders.size() > 0) {
            int startPos = orderList.size();
            orderList.addAll(newOrders);
            notifyItemRangeInserted(startPos, newOrders.size());
        }
    }

    /**
     * Set listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(Order order, int position);

        void onAcceptButtonClick(Order order, int position);
    }

    /**
     * ViewHolder class
     */
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderType;
        TextView tvPrice;
        TextView tvDescription;
        TextView tvAddress;
        TextView tvDeliveryTime;
        TextView tvUserName;
        Button btnAcceptOrder;
        ImageView ivOrderTypeIcon;
        CardView cvOrderTypeBg;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderType = itemView.findViewById(R.id.tv_order_type);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            tvUserName = itemView.findViewById(R.id.tv_push_student);
            btnAcceptOrder = itemView.findViewById(R.id.btn_accept_order);
            ivOrderTypeIcon = itemView.findViewById(R.id.iv_order_type_icon);
            cvOrderTypeBg = itemView.findViewById(R.id.cv_order_type_bg);
        }
    }
} 