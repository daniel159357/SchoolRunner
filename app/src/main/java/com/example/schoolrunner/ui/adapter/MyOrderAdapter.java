package com.example.schoolrunner.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolrunner.R;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.model.enums.OrderStatusEnum;
import com.example.schoolrunner.ui.activity.OrderDetailActivity;
import com.example.schoolrunner.utils.CurrentStudentUtils;
import com.gzone.university.utils.DateUtils;

import java.util.List;

/**
 * My Orders List Adapter
 */
public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final boolean isMyPublish; // true means my published orders, false means my received orders
    private OnOrderActionListener actionListener;

    public MyOrderAdapter(Context context, List<Order> orderList, boolean isMyPublish) {
        this.context = context;
        this.orderList = orderList;
        this.isMyPublish = isMyPublish;
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set order type icon and background color
        setOrderTypeStyle(holder, order.getOrderType());

        // Set order type text
        holder.tvOrderType.setText(order.getOrderType());

        // Set order status
        setOrderStatus(holder, order.getStatus());

        // Set order description
        holder.tvDescription.setText(order.getItemDescription());

        // Set delivery address
        String addressText = order.getPickupAddress() + " → " + order.getDeliveryAddress();
        holder.tvAddress.setText(addressText);

        // Set delivery time
        String deliveryTime = "Deliver as soon as possible";
        if (order.getDeliveryTime() != null) {
            deliveryTime = "Expected to deliver before " + DateUtils.format(order.getDeliveryTime());
        }
        holder.tvDeliveryTime.setText(deliveryTime);

        // Set price
        String priceText = "¥" + order.getPrice();
        holder.tvPrice.setText(priceText);

        // Set student info according to whether it is my published order
        String studentName = "";
        if (isMyPublish) {
            // My published order, show receiver info
            Student receiver = order.getRunner();
            studentName += "Receiver: ";
            if (receiver != null) {
                studentName += receiver.getName();
            } else {
                studentName += "Not received yet";
            }
        } else {
            // My received order, show publisher info
            Student publisher = order.getStudent();
            studentName = "Publisher: ";
            if (publisher != null) {
                studentName += publisher.getName();
            } else {
                studentName += "Unknown publisher";
            }
        }

        // Set student info
        holder.tvStudentName.setText(studentName);

        // Set action button text and click event
        setActionButton(holder, order);

        // Set click event for the whole item, jump to order detail page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("orderId", order.getId());
                context.startActivity(intent);
            }
        });
    }

    /**
     * Set order type style
     */
    private void setOrderTypeStyle(ViewHolder holder, String orderType) {
        int iconRes = R.drawable.ic_other;
        int bgColor = Color.parseColor("#E3F2FD");
        int iconColor = Color.parseColor("#2196F3");

        switch (orderType) {
            case "Express Pickup":
                iconRes = R.drawable.ic_express;
                bgColor = Color.parseColor("#E3F2FD");
                iconColor = Color.parseColor("#2196F3");
                break;
            case "Food Delivery":
                iconRes = R.drawable.ic_food;
                bgColor = Color.parseColor("#FFEBEE");
                iconColor = Color.parseColor("#F44336");
                break;
            case "Document Printing":
                iconRes = R.drawable.ic_print;
                bgColor = Color.parseColor("#E8F5E9");
                iconColor = Color.parseColor("#4CAF50");
                break;
            case "Other Services":
                iconRes = R.drawable.ic_other;
                bgColor = Color.parseColor("#F3E5F5");
                iconColor = Color.parseColor("#9C27B0");
                break;
        }

        holder.ivOrderTypeIcon.setImageResource(iconRes);
        holder.cvOrderTypeBg.setCardBackgroundColor(bgColor);
        holder.ivOrderTypeIcon.setColorFilter(iconColor);
    }

    /**
     * Set order status text and color
     */
    private void setOrderStatus(ViewHolder holder, int status) {
        String statusText;
        int statusColor;

        switch (status) {
            case 0: // Waiting for receive
                statusText = "Waiting for receive";
                statusColor = Color.parseColor("#FF9800");
                break;
            case 1: // Received
                statusText = "In progress";
                statusColor = Color.parseColor("#FF9500");
                break;
            case 2: // Completed
                statusText = "Completed";
                statusColor = Color.parseColor("#4CAF50");
                break;
            case 3: // Canceled
                statusText = "Canceled";
                statusColor = Color.parseColor("#9E9E9E");
                break;
            default:
                statusText = "Unknown status";
                statusColor = Color.parseColor("#9E9E9E");
        }

        holder.tvOrderStatus.setText(statusText);
        holder.tvOrderStatus.setTextColor(statusColor);
    }

    /**
     * Set action button
     */
    private void setActionButton(ViewHolder holder, Order order) {
        Student currentStudent = CurrentStudentUtils.getCurrentStudent();
        int status = order.getStatus();

        if (isMyPublish) {
            // My published order
            if (status == OrderStatusEnum.RECEIVED.getCode()) {
                holder.tvAction.setText("Confirm receipt");
                holder.tvAction.setBackgroundResource(R.drawable.bg_button);
                holder.tvAction.setTextColor(Color.WHITE);
                holder.tvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionListener != null) {
                            actionListener.onConfirmReceive(order);
                        }
                    }
                });
            } else if (status == OrderStatusEnum.WAITING_FOR_RECEIVE.getCode()) {
                holder.tvAction.setText("Cancel order");
                holder.tvAction.setBackgroundResource(R.drawable.bg_button_outline);
                holder.tvAction.setTextColor(context.getResources().getColor(R.color.primary));
                holder.tvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionListener != null) {
                            actionListener.onCancelOrder(order);
                        }
                    }
                });
            } else {
                holder.tvAction.setText("View details");
                holder.tvAction.setBackgroundResource(R.drawable.bg_button_outline_gray);
                holder.tvAction.setTextColor(Color.parseColor("#9E9E9E"));
                holder.tvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.putExtra("orderId", order.getId());
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            // My received order
            if (status == OrderStatusEnum.RECEIVED.getCode()) {
                holder.tvAction.setText("Contact other party");
                holder.tvAction.setBackgroundResource(R.drawable.bg_button_outline);
                holder.tvAction.setTextColor(context.getResources().getColor(R.color.primary));
                holder.tvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionListener != null) {
                            actionListener.onContactOwner(order);
                        }
                    }
                });
            } else {
                holder.tvAction.setText("View details");
                holder.tvAction.setBackgroundResource(R.drawable.bg_button_outline_gray);
                holder.tvAction.setTextColor(Color.parseColor("#9E9E9E"));
                holder.tvAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.putExtra("orderId", order.getId());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    /**
     * Update order data
     */
    public void updateData(List<Order> newOrders) {
        this.orderList.clear();
        if (newOrders != null) {
            this.orderList.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvOrderTypeBg;
        ImageView ivOrderTypeIcon;
        TextView tvOrderType;
        TextView tvOrderStatus;
        TextView tvDescription;
        TextView tvAddress;
        TextView tvDeliveryTime;
        TextView tvStudentName;
        TextView tvPrice;
        TextView tvAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvOrderTypeBg = itemView.findViewById(R.id.cv_order_type_bg);
            ivOrderTypeIcon = itemView.findViewById(R.id.iv_order_type_icon);
            tvOrderType = itemView.findViewById(R.id.tv_order_type);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvAction = itemView.findViewById(R.id.tv_action);
        }
    }

    /**
     * Order action listener interface
     */
    public interface OnOrderActionListener {
        /**
         * Confirm receipt
         */
        void onConfirmReceive(Order order);

        /**
         * Cancel order
         */
        void onCancelOrder(Order order);

        /**
         * Contact publisher
         */
        void onContactOwner(Order order);
    }
} 