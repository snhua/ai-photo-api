package com.aiphone.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 用户登录请求DTO
 */
@Data
public class UserLoginRequest {
    
    /**
     * 微信登录code
     */
    @NotBlank(message = "code不能为空")
    private String code;
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private String nickName;
        private String avatarUrl;
    }
} 