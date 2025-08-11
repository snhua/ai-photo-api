package com.aiphone.common;

import lombok.Data;

/**
 * 统一响应结果
 */
@Data
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }
    
    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }
    
    public static <T> Result<T> success() {
        return new Result<>(0, "success");
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(1005, message);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    public static Result<Boolean> success(boolean b, String message) {
        return new Result<>(0, message);
    }
}
