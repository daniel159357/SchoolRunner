package com.example.schoolrunner.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.schoolrunner.R;
import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.dao.OrderDao;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.model.enums.OrderStatusEnum;
import com.example.schoolrunner.utils.CurrentStudentUtils;
import com.example.schoolrunner.utils.RouteUtils;
import com.gzone.university.utils.DateUtils;

import java.util.List;

/**
 * Order Detail Page
 */
public class OrderDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivOrderTypeIcon;
    private CardView cvOrderTypeBg;
    private TextView tvStatus;
    private TextView tvRemainTime;
    private TextView tvOrderType;
    private TextView tvOrderNo;
    private TextView tvPrice;
    private TextView tvItemDescription;
    private TextView tvDeliveryAddress;
    private TextView tvDeliveryTime;
    private TextView tvPickupAddress;
    private TextView tvCreateTime;
    private TextView tvStudentName;
    private TextView tvRouteInfo; // Added route information TextView
    private Button btnCall;
    private TextView tvPriceBottom;
    private Button btnReceiveOrder;
    private TextView tvOrderDuration; // 新增用时显示控件
    private RatingBar ratingBarPublisher, ratingBarRunner;
    private TextView tvPublisherGrade, tvPublisherComment, tvRunnerGrade, tvRunnerComment;
    private EditText etPublisherComment, etRunnerComment;
    private Button btnSubmitPublisher, btnSubmitRunner;

    private Long orderId;
    private Order order;
    private String viewRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get order ID from Intent
        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Order does not exist", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewRole = getIntent().getStringExtra("viewRole");

        initView();
        initListener();
        loadOrderDetail();
    }

    /**
     * Initialize views
     */
    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        cvOrderTypeBg = findViewById(R.id.cv_order_type_bg);
        ivOrderTypeIcon = findViewById(R.id.iv_order_type_icon);
        tvStatus = findViewById(R.id.tv_status);
        tvRemainTime = findViewById(R.id.tv_remain_time);
        tvOrderType = findViewById(R.id.tv_order_type);
        tvOrderNo = findViewById(R.id.tv_order_no);
        tvPrice = findViewById(R.id.tv_price);
        tvItemDescription = findViewById(R.id.tv_item_description);
        tvDeliveryAddress = findViewById(R.id.tv_delivery_address);
        tvDeliveryTime = findViewById(R.id.tv_delivery_time);
        tvPickupAddress = findViewById(R.id.tv_pickup_address);
        tvCreateTime = findViewById(R.id.tv_create_time);
        tvStudentName = findViewById(R.id.tv_student_name);
        tvRouteInfo = findViewById(R.id.tv_route_info); // Initialize route info TextView
        tvPriceBottom = findViewById(R.id.tv_price_bottom);
        btnCall = findViewById(R.id.btn_call);
        btnReceiveOrder = findViewById(R.id.btn_receive_order);
        tvOrderDuration = findViewById(R.id.tv_order_duration); // 初始化用时控件
        ratingBarPublisher = findViewById(R.id.rating_bar_publisher);
        tvPublisherGrade = findViewById(R.id.tv_publisher_grade);
        tvPublisherComment = findViewById(R.id.tv_publisher_comment);
        ratingBarRunner = findViewById(R.id.rating_bar_runner);
        tvRunnerGrade = findViewById(R.id.tv_runner_grade);
        tvRunnerComment = findViewById(R.id.tv_runner_comment);
        // 不要findViewById动态输入控件
    }

    /**
     * Initialize listeners
     */
    private void initListener() {
        // Back button click event
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Receive order button click event
        btnReceiveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveOrder();
            }
        });

        // Call button click event
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null && !TextUtils.isEmpty(order.getContact())) {
                    // Call system dialer
                    String phoneNumber = order.getContact();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(OrderDetailActivity.this, "Dial failed, this device does not support dialing", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Load order details
     */
    private void loadOrderDetail() {
        Result<Order> result = OrderDao.getOrderById(orderId);
        if (result.isSuccess()) {
            order = result.getData();
            updateOrderUI();
        } else {
            Toast.makeText(this, "Order does not exist or failed to load", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Update order UI
     */
    private void updateOrderUI() {
        if (order == null) {
            return;
        }

        int bgColor;
        int iconColor;
        int iconResId;
        // Set order type icon
        switch (order.getOrderType()) {
            case "Express Pickup":
                bgColor = Color.parseColor("#E3F2FD");
                iconColor = Color.parseColor("#2196F3");
                iconResId = R.drawable.ic_express;
                break;
            case "Food Delivery":
                bgColor = Color.parseColor("#FFEBEE");
                iconColor = Color.parseColor("#F44336");
                iconResId = R.drawable.ic_food;
                break;
            case "Document Printing":
                bgColor = Color.parseColor("#E8F5E9");
                iconColor = Color.parseColor("#4CAF50");
                iconResId = R.drawable.ic_print;
                break;
            case "Other Services":
                bgColor = Color.parseColor("#EDE7F6");
                iconColor = Color.parseColor("#9C27B0");
                iconResId = R.drawable.ic_other;
                break;
            default:
                bgColor = Color.parseColor("#EEEEEE");
                iconColor = Color.parseColor("#9E9E9E");
                iconResId = R.drawable.ic_other;
                break;
        }

        cvOrderTypeBg.setCardBackgroundColor(bgColor);
        ivOrderTypeIcon.setImageResource(iconResId);
        ivOrderTypeIcon.setColorFilter(iconColor);

        // Set order status
        OrderStatusEnum status = OrderStatusEnum.fromCode(order.getStatus());
        tvStatus.setText(status.getDescription());

        // Adjust UI according to order status
        if (status == OrderStatusEnum.WAITING_FOR_RECEIVE) {
            btnReceiveOrder.setVisibility(View.VISIBLE);
            // If expected delivery time is set, calculate remaining time
            if (order.getDeliveryTime() != null) {
                long remainMs = order.getDeliveryTime().getTime() - System.currentTimeMillis();
                if (remainMs > 0) {
                    long remainMinutes = remainMs / (1000 * 60);
                    tvRemainTime.setText("Remaining " + remainMinutes + " minutes");
                } else {
                    tvRemainTime.setText("Overdue");
                }
            } else {
                tvRemainTime.setVisibility(View.GONE);
            }
        } else {
            btnReceiveOrder.setVisibility(View.GONE);
            tvRemainTime.setVisibility(View.GONE);
        }

        // Set order basic information
        tvOrderType.setText(order.getOrderType());
        tvOrderNo.setText("Order No.: " + order.getOrderNo());
        tvPrice.setText("Reward ¥" + order.getPrice());
        tvItemDescription.setText(order.getItemDescription());
        tvDeliveryAddress.setText(order.getDeliveryAddress());

        // Set delivery time
        if (order.getDeliveryTime() != null) {
            tvDeliveryTime.setText(DateUtils.format(order.getDeliveryTime()));
        }

        // Set creation time
        if (order.getCreateTime() != null) {
            tvCreateTime.setText(DateUtils.format(order.getCreateTime()) + " Published");
        }

        // Set delivery route information
        tvPickupAddress.setText(order.getPickupAddress());
        tvDeliveryAddress.setText(order.getDeliveryAddress());
        
        // Set route information if available
        String routeInfo = order.getRouteInfo();
        if (!TextUtils.isEmpty(routeInfo)) {
            tvRouteInfo.setText(routeInfo);
            tvRouteInfo.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Route info from database: " + routeInfo, Toast.LENGTH_LONG).show();
        } else {
            // Check if we can generate route information
            String pickupAddress = order.getPickupAddress();
            String deliveryAddress = order.getDeliveryAddress();
            
            Toast.makeText(this, "Checking route: " + pickupAddress + " to " + deliveryAddress, Toast.LENGTH_LONG).show();
            
            if (RouteUtils.isCampusPlace(pickupAddress) && RouteUtils.isCampusPlace(deliveryAddress)) {
                Toast.makeText(this, "Both are campus places!", Toast.LENGTH_LONG).show();
                List<String> route = RouteUtils.getRoute(pickupAddress, deliveryAddress);
                if (route != null) {
                    String formattedRoute = RouteUtils.formatRouteString(route);
                    tvRouteInfo.setText(formattedRoute);
                    tvRouteInfo.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Generated route: " + formattedRoute, Toast.LENGTH_LONG).show();
                } else {
                    tvRouteInfo.setVisibility(View.GONE);
                    Toast.makeText(this, "No route found between these places", Toast.LENGTH_LONG).show();
                }
            } else {
                tvRouteInfo.setVisibility(View.GONE);
                if (!RouteUtils.isCampusPlace(pickupAddress)) {
                    Toast.makeText(this, "Pickup is not a campus place: " + pickupAddress, Toast.LENGTH_LONG).show();
                }
                if (!RouteUtils.isCampusPlace(deliveryAddress)) {
                    Toast.makeText(this, "Delivery is not a campus place: " + deliveryAddress, Toast.LENGTH_LONG).show();
                }
            }
        }

        // Set order creator information
        Student student = order.getStudent();
        if (student != null) {
            tvStudentName.setText(student.getName());
        }

        // Set bottom price
        tvPriceBottom.setText("¥" + order.getPrice());

        // 显示用时
        if (order.getReceivedTime() != null && order.getCompletedTime() != null) {
            long durationMs = order.getCompletedTime().getTime() - order.getReceivedTime().getTime();
            long minutes = durationMs / (1000 * 60);
            long hours = minutes / 60;
            long remainMinutes = minutes % 60;
            String durationStr = (hours > 0 ? hours + "h " : "") + remainMinutes + "min";
            tvOrderDuration.setText("Duration: " + durationStr);
            tvOrderDuration.setVisibility(View.VISIBLE);
        } else {
            tvOrderDuration.setVisibility(View.GONE);
        }

        // 评价区逻辑
        Student currentStudent = null;
        try {
            currentStudent = CurrentStudentUtils.getCurrentStudent();
        } catch (Exception e) {
            return;
        }
        boolean isPublisher = currentStudent != null && currentStudent.getId().equals(order.getStudentId());
        boolean isRunner = currentStudent != null && order.getRunnerId() != null && currentStudent.getId().equals(order.getRunnerId());
        // Publisher grade
        if (order.getPublisherScore() != null) {
            ratingBarPublisher.setRating(order.getPublisherScore().floatValue());
            tvPublisherGrade.setText(String.format("%.1f", order.getPublisherScore()));
        } else {
            ratingBarPublisher.setRating(0f);
            tvPublisherGrade.setText("no grade");
        }
        // Publisher comment
        tvPublisherComment.setText(order.getPublisherComment() == null ? "" : order.getPublisherComment());
        // Runner grade
        if (order.getRunnerScore() != null) {
            ratingBarRunner.setRating(order.getRunnerScore().floatValue());
            tvRunnerGrade.setText(String.format("%.1f", order.getRunnerScore()));
        } else {
            ratingBarRunner.setRating(0f);
            tvRunnerGrade.setText("no grade");
        }
        // Runner comment
        tvRunnerComment.setText(order.getRunnerComment() == null ? "" : order.getRunnerComment());
        // 动态插入输入控件
        LinearLayout layoutOrderEvaluation = findViewById(R.id.layout_order_evaluation);
        // 先移除已有的输入控件
        if (etPublisherComment != null) layoutOrderEvaluation.removeView(etPublisherComment);
        if (btnSubmitPublisher != null) layoutOrderEvaluation.removeView(btnSubmitPublisher);
        if (etRunnerComment != null) layoutOrderEvaluation.removeView(etRunnerComment);
        if (btnSubmitRunner != null) layoutOrderEvaluation.removeView(btnSubmitRunner);
        // 订单完成后，允许评价
        boolean canPublisherRate = "publisher".equals(viewRole);
        boolean canRunnerRate = "runner".equals(viewRole);
        if (order.getStatus() == OrderStatusEnum.COMPLETED.getCode()) {
            if (canPublisherRate && isPublisher && order.getPublisherScore() == null) {
                // 动态查找publisher comment的索引
                int publisherCommentIndex = -1;
                for (int i = 0; i < layoutOrderEvaluation.getChildCount(); i++) {
                    View v = layoutOrderEvaluation.getChildAt(i);
                    if (v.getId() == R.id.tv_publisher_comment) {
                        publisherCommentIndex = i;
                        break;
                    }
                }
                if (publisherCommentIndex != -1) {
                    layoutOrderEvaluation.removeViewAt(publisherCommentIndex);
                    etPublisherComment = new EditText(this);
                    etPublisherComment.setHint("Write your comment...");
                    etPublisherComment.setTextSize(14f);
                    btnSubmitPublisher = new Button(this);
                    btnSubmitPublisher.setText("Submit publisher rating");
                    btnSubmitPublisher.setOnClickListener(v -> {
                        double score = ratingBarPublisher.getRating();
                        String comment = etPublisherComment.getText().toString();
                        OrderDao.updateOrderRating(order.getId(), true, score, comment);
                        loadOrderDetail();
                    });
                    layoutOrderEvaluation.addView(etPublisherComment, publisherCommentIndex);
                    layoutOrderEvaluation.addView(btnSubmitPublisher, publisherCommentIndex + 1);
                } else {
                    // 找不到就加到最后
                    etPublisherComment = new EditText(this);
                    etPublisherComment.setHint("Write your comment...");
                    etPublisherComment.setTextSize(14f);
                    btnSubmitPublisher = new Button(this);
                    btnSubmitPublisher.setText("Submit publisher rating");
                    btnSubmitPublisher.setOnClickListener(v -> {
                        double score = ratingBarPublisher.getRating();
                        String comment = etPublisherComment.getText().toString();
                        OrderDao.updateOrderRating(order.getId(), true, score, comment);
                        loadOrderDetail();
                    });
                    layoutOrderEvaluation.addView(etPublisherComment);
                    layoutOrderEvaluation.addView(btnSubmitPublisher);
                }
                ratingBarPublisher.setIsIndicator(false);
            } else {
                ratingBarPublisher.setIsIndicator(true);
            }
            if (canRunnerRate && isRunner && order.getRunnerScore() == null) {
                // 动态查找runner comment的索引
                int runnerCommentIndex = -1;
                for (int i = 0; i < layoutOrderEvaluation.getChildCount(); i++) {
                    View v = layoutOrderEvaluation.getChildAt(i);
                    if (v.getId() == R.id.tv_runner_comment) {
                        runnerCommentIndex = i;
                        break;
                    }
                }
                if (runnerCommentIndex != -1) {
                    layoutOrderEvaluation.removeViewAt(runnerCommentIndex);
                    etRunnerComment = new EditText(this);
                    etRunnerComment.setHint("Write your comment...");
                    etRunnerComment.setTextSize(14f);
                    btnSubmitRunner = new Button(this);
                    btnSubmitRunner.setText("Submit runner rating");
                    btnSubmitRunner.setOnClickListener(v -> {
                        double score = ratingBarRunner.getRating();
                        String comment = etRunnerComment.getText().toString();
                        OrderDao.updateOrderRating(order.getId(), false, score, comment);
                        loadOrderDetail();
                    });
                    layoutOrderEvaluation.addView(etRunnerComment, runnerCommentIndex);
                    layoutOrderEvaluation.addView(btnSubmitRunner, runnerCommentIndex + 1);
                } else {
                    // 找不到就加到最后
                    etRunnerComment = new EditText(this);
                    etRunnerComment.setHint("Write your comment...");
                    etRunnerComment.setTextSize(14f);
                    btnSubmitRunner = new Button(this);
                    btnSubmitRunner.setText("Submit runner rating");
                    btnSubmitRunner.setOnClickListener(v -> {
                        double score = ratingBarRunner.getRating();
                        String comment = etRunnerComment.getText().toString();
                        OrderDao.updateOrderRating(order.getId(), false, score, comment);
                        loadOrderDetail();
                    });
                    layoutOrderEvaluation.addView(etRunnerComment);
                    layoutOrderEvaluation.addView(btnSubmitRunner);
                }
                ratingBarRunner.setIsIndicator(false);
            } else {
                ratingBarRunner.setIsIndicator(true);
            }
        } else {
            ratingBarPublisher.setIsIndicator(true);
            ratingBarRunner.setIsIndicator(true);
        }
    }

    /**
     * Receive order
     */
    private void receiveOrder() {
        if (order == null) {
            return;
        }

        // Get current logged-in student
        Student currentStudent = CurrentStudentUtils.getCurrentStudent();
        if (currentStudent == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call receive order API
        Result<Order> result = OrderDao.updateOrderStatus(
                order.getId(),
                OrderStatusEnum.RECEIVED.getCode(),
                currentStudent.getId()
        );

        if (result.isSuccess()) {
            Toast.makeText(this, "Order received successfully", Toast.LENGTH_SHORT).show();
            // Refresh UI
            order = result.getData();
            updateOrderUI();
            // 如果是确认完成，显示用时
            if (order.getReceivedTime() != null && order.getCompletedTime() != null) {
                long durationMs = order.getCompletedTime().getTime() - order.getReceivedTime().getTime();
                long minutes = durationMs / (1000 * 60);
                long hours = minutes / 60;
                long remainMinutes = minutes % 60;
                String durationStr = (hours > 0 ? hours + "h " : "") + remainMinutes + "min";
                Toast.makeText(this, "Order completed in " + durationStr, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Failed to receive order: " + result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 