package com.aiphone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI绘画师DTO
 */
@Data
public class ArtistDTO {

    /**
     * 绘画师ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 绘画师名称
     */
    private String artistName;

    /**
     * 绘画师描述
     */
    private String description;

    /**
     * 专长领域
     */
    private List<String> specialties;

    /**
     * 每小时价格
     */
    private BigDecimal pricePerHour;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 总订单数
     */
    private Integer totalOrders;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 用户信息
     */
    private UserDTO user;

    /**
     * 作品列表
     */
    private List<ArtworkDTO> works;

    /**
     * 评价列表
     */
    private List<ReviewDTO> reviews;
} 