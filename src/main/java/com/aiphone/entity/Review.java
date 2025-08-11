package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("reviews")
public class Review {
    
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
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
     * 评价标签，JSON格式
     */
    private String tags;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 