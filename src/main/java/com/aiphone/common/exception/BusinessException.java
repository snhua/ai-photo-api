package com.aiphone.common.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 1005;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }
}
