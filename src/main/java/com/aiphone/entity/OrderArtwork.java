package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 订单作品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_artworks")
public class OrderArtwork {
    
    /**
     * 订单作品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 作品URL
     */
    private String artworkUrl;
    
    /**
     * 作品描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 