package com.example.schoolrunner.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.OrderDao;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.model.enums.OrderStatusEnum;
import com.example.schoolrunner.ui.activity.CreateOrderActivity;
import com.example.schoolrunner.ui.activity.OrderDetailActivity;
import com.example.schoolrunner.ui.adapter.OrderAdapter;
import com.example.schoolrunner.utils.CurrentStudentUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Top search bar
    private ImageView ivAdd;
    private LinearLayout llSearch;

    // Category navigation
    private TextView tvCategoryAll;
    private TextView tvCategoryExpress;
    private TextView tvCategoryFood;
    private TextView tvCategoryPrint;
    private TextView tvCategoryOther;
    private List<TextView> categoryTextViews = new ArrayList<>();

    // Order list
    private RecyclerView rvOrderList;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();

    // Add empty view layout
    private LinearLayout layoutEmpty;

    // Currently selected order type
    private String currentOrderType = null;

    // Add search box related variables
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        loadOrderData();
        
        // Make sure all category tabs are visible by scrolling to the selected one
        scrollToSelectedCategory();
    }
    
    /**
     * Scroll to the currently selected category tab
     */
    private void scrollToSelectedCategory() {
        for (TextView textView : categoryTextViews) {
            if (textView.isSelected()) {
                textView.post(() -> {
                    // Ensure the selected tab is visible by scrolling to it
                    HorizontalScrollView scrollView = (HorizontalScrollView) textView.getParent().getParent();
                    int scrollX = textView.getLeft() - (scrollView.getWidth() - textView.getWidth()) / 2;
                    scrollView.smoothScrollTo(Math.max(0, scrollX), 0);
                });
                break;
            }
        }
    }

    private void initView(View view) {
        // Initialize top search bar
        ivAdd = view.findViewById(R.id.iv_add);
        llSearch = view.findViewById(R.id.ll_search);
        etSearch = view.findViewById(R.id.et_search); // Add search box reference

        // Initialize empty view
        layoutEmpty = view.findViewById(R.id.layout_empty);

        // Initialize category navigation
        tvCategoryAll = view.findViewById(R.id.tv_category_all);
        tvCategoryExpress = view.findViewById(R.id.tv_category_express);
        tvCategoryFood = view.findViewById(R.id.tv_category_food);
        tvCategoryPrint = view.findViewById(R.id.tv_category_print);
        tvCategoryOther = view.findViewById(R.id.tv_category_other);

        // Add to list for unified management
        categoryTextViews.add(tvCategoryAll);
        categoryTextViews.add(tvCategoryExpress);
        categoryTextViews.add(tvCategoryFood);
        categoryTextViews.add(tvCategoryPrint);
        categoryTextViews.add(tvCategoryOther);
        // Default select all category
        tvCategoryAll.setSelected(true);

        // Initialize order list
        rvOrderList = view.findViewById(R.id.rv_order_list);
        rvOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(getContext(), orderList);
        rvOrderList.setAdapter(orderAdapter);
    }

    private void initListener() {
        // Search box text change listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Delay 500ms to search
                etSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String keyword = s.toString();
                        searchOrders(keyword);
                    }
                }, 500);
            }
        });

        // Top search bar click event
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Let the search box get focus and show the soft keyboard
                etSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateOrderActivity.class);
                startActivity(intent);
            }
        });

        // Category navigation click events
        tvCategoryAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(tvCategoryAll, null);
            }
        });

        tvCategoryExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(tvCategoryExpress, "Express Pickup");
            }
        });

        tvCategoryFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(tvCategoryFood, "Food Delivery");
            }
        });

        tvCategoryPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(tvCategoryPrint, "Document Printing");
            }
        });

        tvCategoryOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory(tvCategoryOther, "Other Services");
            }
        });

        // Set order list click events
        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order order, int position) {
                // Click order item, jump to order detail page
                navigateToOrderDetail(order);
            }

            @Override
            public void onAcceptButtonClick(Order order, int position) {
                // Click accept order button, handle accept order logic
                acceptOrder(order, position);
            }
        });
    }

    /**
     * Jump to order detail page
     */
    private void navigateToOrderDetail(Order order) {
        if (order != null && order.getId() != null) {
            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Order information is incomplete", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle category selection
     */
    private void selectCategory(TextView selectedTextView, String orderType) {
        // Reset all category styles
        for (TextView textView : categoryTextViews) {
            textView.setSelected(false);
        }

        // Set selected category
        selectedTextView.setSelected(true);

        // Update currently selected order type
        currentOrderType = orderType;

        // Reload data
        loadOrderData();
        
        // Scroll to make the selected category visible
        scrollToSelectedCategory();
    }

    /**
     * Load order data
     */
    private void loadOrderData() {
        // Get the list of available orders from the database
        Result<List<Order>> result = OrderDao.getAvailableOrders(etSearch.getText().toString());
        if (result.isSuccess()) {
            List<Order> allOrders = result.getData();
            List<Order> filteredOrders = new ArrayList<>();

            // Filter data according to selected category
            if (currentOrderType == null) {
                // All orders
                filteredOrders.addAll(allOrders);
            } else {
                // Filter by type
                for (Order order : allOrders) {
                    if (currentOrderType.equals(order.getOrderType())) {
                        filteredOrders.add(order);
                    }
                }
            }

            // Update list data
            orderList.clear();
            orderList.addAll(filteredOrders);
            orderAdapter.notifyDataSetChanged();

            // Update empty view display status
            if (orderList.isEmpty()) {
                rvOrderList.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                rvOrderList.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
        } else {
            // Load failed
            Toast.makeText(getContext(), "Failed to load orders: " + result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle accept order logic
     */
    private void acceptOrder(Order order, int position) {
        Long currentStudentId = CurrentStudentUtils.getCurrentStudent().getId();

        // Call accept order interface
        Result<Order> result = OrderDao.updateOrderStatus(order.getId(),
                OrderStatusEnum.RECEIVED.getCode(), currentStudentId);

        if (result.isSuccess()) {
            Toast.makeText(getContext(), "Accept order successful", Toast.LENGTH_SHORT).show();
            // Remove the order from the list
            orderList.remove(position);
            orderAdapter.notifyItemRemoved(position);
            // Update empty view display status
            if (orderList.isEmpty()) {
                rvOrderList.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
            // After accepting the order, jump to the order detail page
            navigateToOrderDetail(result.getData());
        } else {
            Toast.makeText(getContext(), "Accept order failed: " + result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Refresh data when the Fragment is visible again
     */
    @Override
    public void onResume() {
        super.onResume();
        loadOrderData();
    }

    /**
     * Search orders by keyword
     */
    private void searchOrders(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(getContext(), "Please enter a search keyword", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get all available orders
        Result<List<Order>> result = OrderDao.getAvailableOrders(keyword);
        if (result.isSuccess()) {
            List<Order> allOrders = result.getData();
            // Update list data
            orderList.clear();
            orderList.addAll(allOrders);
            orderAdapter.notifyDataSetChanged();

            // Update empty view display status
            if (orderList.isEmpty()) {
                rvOrderList.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "No matching orders found", Toast.LENGTH_SHORT).show();
            } else {
                rvOrderList.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Found " + allOrders.size() + " orders", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Failed to search orders: " + result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 