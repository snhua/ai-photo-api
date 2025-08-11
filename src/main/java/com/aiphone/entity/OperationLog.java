package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("operation_logs")
public class OperationLog {
    
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 操作用户ID
     */
    private Long userId;
    
    /**
     * 操作类型
     */
    private String operation;
    
    /**
     * 操作描述
     */
    private String description;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 