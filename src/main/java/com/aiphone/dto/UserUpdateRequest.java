package com.aiphone.dto;

import lombok.Data;

/**
 * 用户信息更新请求DTO
 */
@Data
public class UserUpdateRequest {
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
}