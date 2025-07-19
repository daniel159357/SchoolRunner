package com.example.schoolrunner.common;

/**
 * General result wrapper class
 *
 * @param <T> Data type
 */
public class Result<T> {
    /**
     * Whether the operation was successful
     */
    private boolean success;

    /**
     * Result message
     */
    private String message;

    /**
     * Result data
     */
    private T data;

    public Result(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
} 