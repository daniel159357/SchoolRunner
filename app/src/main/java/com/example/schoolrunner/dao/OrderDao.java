package com.example.schoolrunner.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.schoolrunner.common.Result;
import com.example.schoolrunner.model.entity.Order;
import com.example.schoolrunner.model.entity.Student;
import com.example.schoolrunner.model.enums.OrderStatusEnum;
import com.example.schoolrunner.utils.SqliteUtils;
import com.gzone.university.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Order data access object
 */
@SuppressLint("Range")
public class OrderDao {

    /**
     * Create order
     */
    public static Result<Order> createOrder(Order order) {
        if (order == null || order.getStudentId() == null || TextUtils.isEmpty(order.getOrderType()) ||
                TextUtils.isEmpty(order.getItemDescription()) || TextUtils.isEmpty(order.getPickupAddress()) ||
                TextUtils.isEmpty(order.getDeliveryAddress()) || TextUtils.isEmpty(order.getContact()) ||
                order.getPrice() <= 0) {
            return new Result<>(false, "Order information is incomplete", null);
        }

        // Generate order number
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);

        // Set order status to waiting for receive
        order.setStatus(OrderStatusEnum.WAITING_FOR_RECEIVE.getCode());

        ContentValues values = new ContentValues();
        values.put("student_id", order.getStudentId());
        if (order.getRunnerId() != null) {
            values.put("runner_id", order.getRunnerId());
        }
        values.put("order_no", order.getOrderNo());
        values.put("order_type", order.getOrderType());
        values.put("item_description", order.getItemDescription());
        values.put("pickup_address", order.getPickupAddress());
        values.put("delivery_address", order.getDeliveryAddress());
        
        // Add route information if available
        if (!TextUtils.isEmpty(order.getRouteInfo())) {
            values.put("route_info", order.getRouteInfo());
        }
        
        values.put("contact", order.getContact());
        if (order.getDeliveryTime() != null) {
            values.put("delivery_time", DateUtils.format(order.getDeliveryTime()));
        }
        values.put("price", order.getPrice());
        values.put("status", order.getStatus());

        long id = SqliteUtils.getInstance().getWritableDatabase().insert("tb_order", null, values);
        if (id > 0) {
            order.setId(id);
            return new Result<>(true, "Order created successfully", order);
        }
        return new Result<>(false, "Order creation failed", null);
    }

    /**
     * Generate order number
     */
    private static String generateOrderNo() {
        // Generate random order number (timestamp + 4-digit random number)
        return System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    /**
     * Query order by order ID
     */
    public static Result<Order> getOrderById(Long orderId) {
        if (orderId == null) {
            return new Result<>(false, "Order ID cannot be null", null);
        }

        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "tb_order", null, "id=?",
                new String[]{String.valueOf(orderId)}, null, null, null);

        if (cursor.moveToNext()) {
            Order order = extractOrderFromCursor(cursor);
            cursor.close();
            return new Result<>(true, "Query successful", order);
        }
        cursor.close();
        return new Result<>(false, "Order does not exist", null);
    }

    /**
     * Query order by order number
     */
    public static Result<Order> getOrderByOrderNo(String orderNo) {
        if (TextUtils.isEmpty(orderNo)) {
            return new Result<>(false, "Order number cannot be null", null);
        }

        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "tb_order", null, "order_no=?",
                new String[]{orderNo}, null, null, null);

        if (cursor.moveToNext()) {
            Order order = extractOrderFromCursor(cursor);
            cursor.close();
            return new Result<>(true, "Query successful", order);
        }
        cursor.close();
        return new Result<>(false, "Order does not exist", null);
    }

    /**
     * Query orders published by student
     */
    public static Result<List<Order>> getOrdersByStudentId(Long studentId) {
        if (studentId == null) {
            return new Result<>(false, "Student ID cannot be null", null);
        }

        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "tb_order", null, "student_id=?",
                new String[]{String.valueOf(studentId)}, null, null, "create_time DESC");

        List<Order> orderList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Order order = extractOrderFromCursor(cursor);
            orderList.add(order);
        }
        cursor.close();
        return new Result<>(true, "Query successful", orderList);
    }

    /**
     * Query orders received by student
     */
    public static Result<List<Order>> getOrdersByRunnerId(Long runnerId) {
        if (runnerId == null) {
            return new Result<>(false, "Runner ID cannot be null", null);
        }

        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "tb_order", null, "runner_id=?",
                new String[]{String.valueOf(runnerId)}, null, null, "create_time DESC");

        List<Order> orderList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Order order = extractOrderFromCursor(cursor);
            orderList.add(order);
        }
        cursor.close();
        return new Result<>(true, "Query successful", orderList);
    }

    /**
     * Query available orders
     */
    public static Result<List<Order>> getAvailableOrders(String keyword) {
        Cursor cursor = SqliteUtils.getInstance().getReadableDatabase().query(
                "tb_order", null, "status=? AND (order_type LIKE ? OR item_description LIKE ?)",
                new String[]{String.valueOf(OrderStatusEnum.WAITING_FOR_RECEIVE.getCode()),
                        "%" + keyword + "%", "%" + keyword + "%"}, null, null, "create_time DESC");

        List<Order> orderList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Order order = extractOrderFromCursor(cursor);
            orderList.add(order);
        }
        cursor.close();
        return new Result<>(true, "Query successful", orderList);
    }

    /**
     * Update order status
     */
    public static Result<Order> updateOrderStatus(Long orderId, Integer status, Long runnerId) {
        if (orderId == null || status == null) {
            return new Result<>(false, "Order ID and status cannot be null", null);
        }

        // Query whether the order exists
        Result<Order> orderResult = getOrderById(orderId);
        if (!orderResult.isSuccess()) {
            return new Result<>(false, "Order does not exist", null);
        }

        Order order = orderResult.getData();

        if (Objects.equals(order.getStudentId(), runnerId)) {
            return new Result<>(false, "Cannot receive your own order", null);
        }

        // Check status update logic
        if (status == OrderStatusEnum.RECEIVED.getCode() && runnerId == null) {
            return new Result<>(false, "Runner ID must be provided when receiving order", null);
        }

        ContentValues values = new ContentValues();
        values.put("status", status);

        // If it's a receive order status, update the runner ID
        if (status == OrderStatusEnum.RECEIVED.getCode() && runnerId != null) {
            values.put("runner_id", runnerId);
        }

        int rowsAffected = SqliteUtils.getInstance().getWritableDatabase().update(
                "tb_order", values, "id=?", new String[]{String.valueOf(orderId)});

        if (rowsAffected > 0) {
            order.setStatus(status);
            if (runnerId != null) {
                order.setRunnerId(runnerId);
            }
            return new Result<>(true, "Order status updated successfully", order);
        }

        return new Result<>(false, "Order status update failed", null);
    }

    /**
     * Cancel order
     */
    public static Result<Order> cancelOrder(Long orderId, Long studentId) {
        if (orderId == null || studentId == null) {
            return new Result<>(false, "Order ID and student ID cannot be null", null);
        }

        // Query whether the order exists
        Result<Order> orderResult = getOrderById(orderId);
        if (!orderResult.isSuccess()) {
            return new Result<>(false, "Order does not exist", null);
        }

        Order order = orderResult.getData();

        // Check if it's the order creator
        if (!studentId.equals(order.getStudentId())) {
            return new Result<>(false, "Only the order creator can cancel the order", null);
        }

        // Check if the order status is cancellable
        if (order.getStatus() != OrderStatusEnum.WAITING_FOR_RECEIVE.getCode()) {
            return new Result<>(false, "Only orders in waiting for receive status can be cancelled", null);
        }

        return updateOrderStatus(orderId, OrderStatusEnum.CANCELED.getCode(), null);
    }

    /**
     * Complete order
     */
    public static Result<Order> completeOrder(Long orderId, Long studentId) {
        if (orderId == null || studentId == null) {
            return new Result<>(false, "Order ID and student ID cannot be null", null);
        }

        // Query whether the order exists
        Result<Order> orderResult = getOrderById(orderId);
        if (!orderResult.isSuccess()) {
            return new Result<>(false, "Order does not exist", null);
        }

        Order order = orderResult.getData();

        // Check if it's the order creator
        if (!studentId.equals(order.getStudentId())) {
            return new Result<>(false, "Only the order creator can confirm order completion", null);
        }

        // Check if the order status is received
        if (order.getStatus() != OrderStatusEnum.RECEIVED.getCode()) {
            return new Result<>(false, "Only orders in received status can be completed", null);
        }

        return updateOrderStatus(orderId, OrderStatusEnum.COMPLETED.getCode(), null);
    }

    /**
     * Extract order from cursor
     */
    private static Order extractOrderFromCursor(Cursor cursor) {
        Order order = new Order();
        order.setId(cursor.getLong(cursor.getColumnIndex("id")));
        order.setStudentId(cursor.getLong(cursor.getColumnIndex("student_id")));
        
        int runnerIdIndex = cursor.getColumnIndex("runner_id");
        if (runnerIdIndex != -1 && !cursor.isNull(runnerIdIndex)) {
            order.setRunnerId(cursor.getLong(runnerIdIndex));
        }
        
        order.setOrderNo(cursor.getString(cursor.getColumnIndex("order_no")));
        order.setOrderType(cursor.getString(cursor.getColumnIndex("order_type")));
        order.setItemDescription(cursor.getString(cursor.getColumnIndex("item_description")));
        order.setPickupAddress(cursor.getString(cursor.getColumnIndex("pickup_address")));
        order.setDeliveryAddress(cursor.getString(cursor.getColumnIndex("delivery_address")));
        
        // Read route information if available
        int routeInfoIndex = cursor.getColumnIndex("route_info");
        if (routeInfoIndex != -1 && !cursor.isNull(routeInfoIndex)) {
            order.setRouteInfo(cursor.getString(routeInfoIndex));
        }
        
        order.setContact(cursor.getString(cursor.getColumnIndex("contact")));

        String deliveryTimeStr = cursor.getString(cursor.getColumnIndex("delivery_time"));
        if (!TextUtils.isEmpty(deliveryTimeStr)) {
            order.setDeliveryTime(DateUtils.parse(deliveryTimeStr));
        }

        order.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
        order.setStatus(cursor.getInt(cursor.getColumnIndex("status")));

        String createTimeStr = cursor.getString(cursor.getColumnIndex("create_time"));
        if (!TextUtils.isEmpty(createTimeStr)) {
            order.setCreateTime(DateUtils.parse(createTimeStr));
        }

        // Query student information
        Result<Student> studentResult = StudentDao.getByStudentId(order.getStudentId());
        if (studentResult.isSuccess()) {
            order.setStudent(studentResult.getData());
        }

        // Query runner information
        if (order.getRunnerId() != null) {
            Result<Student> runnerResult = StudentDao.getByStudentId(order.getRunnerId());
            if (runnerResult.isSuccess()) {
                order.setRunner(runnerResult.getData());
            }
        }

        return order;
    }
} 