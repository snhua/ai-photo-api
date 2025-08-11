package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 横幅实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banners")
public class Banner {
    
    /**
     * 横幅ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 横幅标题
     */
    private String title;
    
    /**
     * 横幅描述
     */
    private String description;
    
    /**
     * 横幅图片URL
     */
    private String imageUrl;
    
    /**
     * 跳转链接
     */
    private String linkUrl;
    
    /**
     * 横幅类型：home-首页，category-分类页，promotion-促销页
     */
    private String type;
    
    /**
     * 排序权重，数字越大越靠前
     */
    private Integer sortWeight;
    
    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
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