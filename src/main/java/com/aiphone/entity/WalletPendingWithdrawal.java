package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 延迟到账记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wallet_pending_withdrawals")
public class WalletPendingWithdrawal {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 金额
     */
    private BigDecimal amount;
    
    /**
     * 技术服务费
     */
    private BigDecimal serviceFee;
    
    /**
     * 可提现时间
     */
    private LocalDateTime availableAt;
    
    /**
     * 状态：pending-待处理，available-可提现，withdrawn-已提现，cancelled-已取消
     */
    private String status;
    
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
