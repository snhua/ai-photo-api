package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
public class Order {
    
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 绘画师ID
     */
    private Long artistId;
    
    /**
     * 订单标题
     */
    private String title;
    
    /**
     * 订单描述
     */
    private String description;
    
    /**
     * 参考图片，JSON格式
     */
    private String referenceImages;
    
    /**
     * 需求说明
     */
    private String requirements;
    
    /**
     * 订单价格
     */
    private BigDecimal price;
    
    /**
     * 订单状态：pending-待接单，accepted-已接单，in_progress-进行中，completed-已完成，cancelled-已取消
     */
    private String status;
    
    /**
     * 截止时间
     */
    private LocalDateTime deadline;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 