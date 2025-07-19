package com.example.schoolrunner.model.enums;

public enum OrderStatusEnum {

    /**
     * Waiting for receive
     */
    WAITING_FOR_RECEIVE(0, "Waiting for receive"),
    /**
     * Received
     */
    RECEIVED(1, "Received"),
    /**
     * Completed
     */
    COMPLETED(2, "Completed"),
    /**
     * Canceled
     */
    CANCELED(3, "Canceled");

    private final int code;

    private final String description;

    OrderStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatusEnum fromCode(int code) {
        for (OrderStatusEnum orderType : OrderStatusEnum.values()) {
            if (orderType.getCode() == code) {
                return orderType;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

}
