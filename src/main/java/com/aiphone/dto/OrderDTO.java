package com.aiphone.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单DTO
 */
@Data
public class OrderDTO {

    /**
     * 订单ID
     */
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
     * 参考图片
     */
    private List<String> referenceImages;

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
     * 作品文件URL列表
     */
    private List<String> artworkUrls;

    /**
     * 作品说明
     */
    private String notes;

    /**
     * 技术说明
     */
    private String technicalNotes;

    /**
     * 制作时间（小时）
     */
    private Integer workHours;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 用户信息
     */
    private UserDTO user;

    /**
     * 绘画师信息
     */
    private ArtistDTO artist;
} 