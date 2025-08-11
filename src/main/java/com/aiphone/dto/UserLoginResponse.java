package com.aiphone.dto;

import com.aiphone.entity.User;
import lombok.Data;

/**
 * 用户登录响应DTO
 */
@Data
public class UserLoginResponse {
    
    /**
     * JWT token
     */
    private String token;
    
    /**
     * 用户信息
     */
    private User userInfo;
} 