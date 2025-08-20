package com.aiphone.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包交易记录DTO
 */
@Data
public class WalletTransactionDTO {
    
    /**
     * 交易记录ID
     */
    private Long id;
    
    /**
     * 交易类型：income-收入，expense-支出
     */
    private String type;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    
    /**
     * 可提现金额变动
     */
    private BigDecimal withdrawableAmount;
    
    /**
     * 技术服务费
     */
    private BigDecimal serviceFee;
    
    /**
     * 交易描述
     */
    private String description;
    
    /**
     * 关联类型
     */
    private String relatedType;
    
    /**
     * 关联ID
     */
    private Long relatedId;
    
    /**
     * 可提现时间（延迟到账）
     */
    private LocalDateTime availableAt;
    
    /**
     * 交易流水号
     */
    private String transactionNo;
    
    /**
     * 交易状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
