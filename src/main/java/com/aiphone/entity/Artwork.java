package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("artworks")
public class Artwork {
    
    /**
     * 作品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 绘画师ID
     */
    private Long artistId;
    
    /**
     * 作品标题
     */
    private String title;
    
    /**
     * 作品描述
     */
    private String description;
    
    /**
     * 作品图片URL
     */
    private String imageUrl;
    
    /**
     * 作品分类
     */
    private String category;
    
    /**
     * 作品标签，逗号分隔
     */
    private String tags;
    
    /**
     * 作品价格
     */
    private BigDecimal price;
    
    /**
     * 状态：1-正常，0-下架
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 