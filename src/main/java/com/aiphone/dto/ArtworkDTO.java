package com.aiphone.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 作品DTO
 */
@Data
public class ArtworkDTO {

    /**
     * 作品ID
     */
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
     * 作品标签
     */
    private List<String> tags;

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
    private LocalDateTime createdAt;
} 