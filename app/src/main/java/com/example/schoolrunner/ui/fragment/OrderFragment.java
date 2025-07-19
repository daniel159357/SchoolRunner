package com.example.schoolrunner.ui.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.OrderDao;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.model.enums.OrderStatusEnum;
import com.example.schoolrunner.ui.adapter.MyOrderAdapter;
import com.example.schoolrunner.utils.CurrentStudentUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment implements MyOrderAdapter.OnOrderActionListener {

    // Order type tabs
    private TextView tvMyPickupOrders;
    private TextView tvMyPublishOrders;

    // Order status filter
    private TextView tvStatusAll;
    private TextView tvStatusReceived;
    private TextView tvStatusCompleted;
    private TextView tvStatusCanceled;

    // List and empty view
    private RecyclerView rvOrders;
    private LinearLayout layoutEmpty;

    // Adapter and data
    private MyOrderAdapter myOrderAdapter;
    private final List<Order> orderList = new ArrayList<>();

    // Currently selected status
    private Integer currentStatus = null; // null means all
    private boolean isMyPublish = false; // default show my received orders

    // Currently logged-in student
    private Student currentStudent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        initData();
    }

    private void initView(View view) {

        // Order type tabs
        tvMyPickupOrders = view.findViewById(R.id.tv_my_pickup_orders);
        tvMyPublishOrders = view.findViewById(R.id.tv_my_publish_orders);
        tvMyPickupOrders.setSelected(true);

        // Order status filter
        tvStatusAll = view.findViewById(R.id.tv_status_all);
        tvStatusReceived = view.findViewById(R.id.tv_status_received);
        tvStatusCompleted = view.findViewById(R.id.tv_status_completed);
        tvStatusCanceled = view.findViewById(R.id.tv_status_canceled);
        tvStatusAll.setSelected(true);

        // List and empty view
        rvOrders = view.findViewById(R.id.rv_orders);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        // Initialize RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        myOrderAdapter = new MyOrderAdapter(getContext(), orderList, isMyPublish);
        myOrderAdapter.setOnOrderActionListener(this);
        rvOrders.setAdapter(myOrderAdapter);
    }

    private void initListener() {
        // Order type tab click events
        tvMyPickupOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(false);
            }
        });
        tvMyPublishOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(true);
            }
        });

        // Order status filter click events
        tvStatusAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByStatus(null);
            }
        });
        tvStatusReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByStatus(OrderStatusEnum.RECEIVED.getCode());
            }
        });
        tvStatusCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByStatus(OrderStatusEnum.COMPLETED.getCode());
            }
        });
        tvStatusCanceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByStatus(OrderStatusEnum.CANCELED.getCode());
            }
        });
    }

    private void initData() {
        try {
            // Get the currently logged-in student
            currentStudent = CurrentStudentUtils.getCurrentStudent();
            // Default load my received orders list
            loadOrders();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to get order data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Switch tab
     *
     * @param isPublish true means switch to my published orders, false means switch to my received orders
     */
    private void switchTab(boolean isPublish) {
        if (this.isMyPublish == isPublish) {
            return;
        }

        this.isMyPublish = isPublish;
        tvMyPickupOrders.setSelected(!isPublish);
        tvMyPublishOrders.setSelected(isPublish);

        // Reset filter status
        currentStatus = null;
        updateFilterUI();

        // Update adapter
        myOrderAdapter = new MyOrderAdapter(getContext(), orderList, isMyPublish);
        myOrderAdapter.setOnOrderActionListener(this);
        rvOrders.setAdapter(myOrderAdapter);

        // Reload data
        loadOrders();
    }

    /**
     * Filter by status
     */
    private void filterByStatus(Integer status) {
        this.currentStatus = status;
        updateFilterUI();
        loadOrders();
    }

    /**
     * Update filter UI
     */
    private void updateFilterUI() {
        tvStatusAll.setSelected(currentStatus == null);
        tvStatusReceived.setSelected(currentStatus != null && currentStatus == OrderStatusEnum.RECEIVED.getCode());
        tvStatusCompleted.setSelected(currentStatus != null && currentStatus == OrderStatusEnum.COMPLETED.getCode());
        tvStatusCanceled.setSelected(currentStatus != null && currentStatus == OrderStatusEnum.CANCELED.getCode());
    }

    /**
     * Load order data
     */
    private void loadOrders() {
        if (currentStudent == null) {
            try {
                currentStudent = CurrentStudentUtils.getCurrentStudent();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Result<List<Order>> result;
        if (isMyPublish) {
            // Query my published orders
            result = OrderDao.getOrdersByStudentId(currentStudent.getId());
        } else {
            // Query my received orders
            result = OrderDao.getOrdersByRunnerId(currentStudent.getId());
        }

        if (result.isSuccess()) {
            List<Order> allOrders = result.getData();
            List<Order> filteredOrders = new ArrayList<>();

            // Filter by status
            if (currentStatus == null) {
                filteredOrders.addAll(allOrders);
            } else {
                for (Order order : allOrders) {
                    if (order.getStatus() == currentStatus) {
                        filteredOrders.add(order);
                    }
                }
            }

            // Update list
            orderList.clear();
            orderList.addAll(filteredOrders);
            myOrderAdapter.notifyDataSetChanged();

            // Update empty state view
            if (orderList.isEmpty()) {
                rvOrders.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                rvOrders.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getContext(), "Failed to load orders: " + result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Confirm receipt
     */
    @Override
    public void onConfirmReceive(Order order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm receipt")
                .setMessage("Are you sure you have received the goods?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Result<Order> result = OrderDao.completeOrder(order.getId(), currentStudent.getId());
                        if (result.isSuccess()) {
                            Toast.makeText(getContext(), "Confirm receipt successful", Toast.LENGTH_SHORT).show();
                            loadOrders(); // Reload data
                        } else {
                            Toast.makeText(getContext(), "Confirm receipt failed: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Cancel order
     */
    @Override
    public void onCancelOrder(Order order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Result<Order> result = OrderDao.cancelOrder(order.getId(), currentStudent.getId());
                        if (result.isSuccess()) {
                            Toast.makeText(getContext(), "Order cancelled", Toast.LENGTH_SHORT).show();
                            loadOrders(); // Reload data
                        } else {
                            Toast.makeText(getContext(), "Failed to cancel order: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Contact order owner
     */
    @Override
    public void onContactOwner(Order order) {
        // Call system dialer
        String phoneNumber = order.getContact();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Dialing failed, current device does not support dialing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when the page is restored
        loadOrders();
    }
} 