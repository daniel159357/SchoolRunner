package com.example.schoolrunner.model.entity;

import com.example.schoolrunner.model.enums.OrderStatusEnum;

import java.util.Date;
import java.util.List;

/**
 * Order entity
 */
public class Order {
    /**
     * Order ID
     */
    private Long id;

    /**
     * Order number
     */
    private String orderNo;

    /**
     * Student ID (sender)
     */
    private Long studentId;

    /**
     * Student object (sender)
     */
    private Student student;

    /**
     * Runner ID (receiver)
     */
    private Long runnerId;

    /**
     * Runner object (receiver)
     */
    private Student runner;

    /**
     * Order type
     */
    private String orderType;

    /**
     * Item description
     */
    private String itemDescription;

    /**
     * Pickup address
     */
    private String pickupAddress;

    /**
     * Delivery address
     */
    private String deliveryAddress;
    
    /**
     * Route information (waypoints)
     */
    private String routeInfo;

    /**
     * Contact information
     */
    private String contact;

    /**
     * Order price
     */
    private double price;

    /**
     * Expected delivery time
     */
    private Date deliveryTime;

    /**
     * Order status
     */
    private Integer status = OrderStatusEnum.WAITING_FOR_RECEIVE.getCode();

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Update time
     */
    private Date updateTime;

    /**
     * Time when the order was received
     */
    private Date receivedTime;

    /**
     * Time when the order was completed
     */
    private Date completedTime;

    private Double publisherScore; // 发布人对接单人的评分
    private String publisherComment; // 发布人对接单人的评价
    private Double runnerScore; // 接单人对发布人的评分
    private String runnerComment; // 接单人对发布人的评价
    private Integer runnerLimitType; // 0=所有人，1=新人或高分，2=仅高分
    private Double minRunnerScore; // 最低评分

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(Long runnerId) {
        this.runnerId = runnerId;
    }

    public Student getRunner() {
        return runner;
    }

    public void setRunner(Student runner) {
        this.runner = runner;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    
    public String getRouteInfo() {
        return routeInfo;
    }

    public void setRouteInfo(String routeInfo) {
        this.routeInfo = routeInfo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public Double getPublisherScore() {
        return publisherScore;
    }
    public void setPublisherScore(Double publisherScore) {
        this.publisherScore = publisherScore;
    }
    public String getPublisherComment() {
        return publisherComment;
    }
    public void setPublisherComment(String publisherComment) {
        this.publisherComment = publisherComment;
    }
    public Double getRunnerScore() {
        return runnerScore;
    }
    public void setRunnerScore(Double runnerScore) {
        this.runnerScore = runnerScore;
    }
    public String getRunnerComment() {
        return runnerComment;
    }
    public void setRunnerComment(String runnerComment) {
        this.runnerComment = runnerComment;
    }
    public Integer getRunnerLimitType() { return runnerLimitType; }
    public void setRunnerLimitType(Integer runnerLimitType) { this.runnerLimitType = runnerLimitType; }
    public Double getMinRunnerScore() { return minRunnerScore; }
    public void setMinRunnerScore(Double minRunnerScore) { this.minRunnerScore = minRunnerScore; }
}