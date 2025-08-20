package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现申请实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("withdrawal_requests")
public class WithdrawalRequest {
    
    /**
     * 提现申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 提现单号
     */
    private String withdrawalNo;
    
    /**
     * 提现金额
     */
    private BigDecimal amount;
    
    /**
     * 手续费
     */
    private BigDecimal fee;
    
    /**
     * 实际到账金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 提现方式：wechat-微信，alipay-支付宝，bank-银行卡
     */
    private String paymentMethod;
    
    /**
     * 收款账户
     */
    private String paymentAccount;
    
    /**
     * 提现状态：pending-待处理，processing-处理中，success-成功，failed-失败，cancelled-已取消
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 处理时间
     */
    private LocalDateTime processedAt;
    
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
