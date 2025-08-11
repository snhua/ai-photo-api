package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI绘画师实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("artists")
public class Artist {

    /**
     * 绘画师ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 绘画师名称
     */
    @TableField("artist_name")
    private String artistName;

    /**
     * 绘画师描述
     */
    @TableField("description")
    private String description;

    /**
     * 专长领域，JSON格式
     */
    @TableField("specialties")
    private String specialties;

    /**
     * 每小时价格
     */
    @TableField("price_per_hour")
    private BigDecimal pricePerHour;

    /**
     * 评分
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 总订单数
     */
    @TableField("total_orders")
    private Integer totalOrders;

    /**
     * 状态：1-正常，0-禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 关联的用户信息
     */
    @TableField(exist = false)
    private User user;
} 