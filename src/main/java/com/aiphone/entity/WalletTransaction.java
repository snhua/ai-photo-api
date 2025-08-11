package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包交易记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wallet_transactions")
public class WalletTransaction {
    
    /**
     * 交易记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 交易类型：income-收入，expense-支出
     */
    private String type;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
    /**
     * 交易描述
     */
    private String description;
    
    /**
     * 关联ID（订单ID等）
     */
    private Long relatedId;
    
    /**
     * 关联类型
     */
    private String relatedType;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 