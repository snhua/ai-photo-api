package com.aiphone.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价DTO
 */
@Data
public class ReviewDTO {

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 绘画师ID
     */
    private Long artistId;

    /**
     * 评分：1-5
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价标签
     */
    private List<String> tags;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 用户信息
     */
    private UserDTO user;
} 