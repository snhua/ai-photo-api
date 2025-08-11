package com.aiphone.common.exception;

/**
 * 未授权异常
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
} 