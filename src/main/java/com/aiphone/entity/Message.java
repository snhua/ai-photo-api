package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 消息通知实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("messages")
public class Message {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 消息类型：order-订单消息，system-系统消息，payment-支付消息
     */
    private String type;
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否已读：1-已读，0-未读
     */
    private Integer isRead;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 