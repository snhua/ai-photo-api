package com.aiphone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户DTO
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 微信openid
     */
    private String openid;

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

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 用户类型：user-普通用户，artist-AI绘画师
     */
    private String userType;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 