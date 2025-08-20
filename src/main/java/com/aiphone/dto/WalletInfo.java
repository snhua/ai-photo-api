package com.aiphone.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包信息DTO
 */
@Data
public class WalletInfo {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 账户余额
     */
    private BigDecimal balance;
    
    /**
     * 可提现金额
     */
    private BigDecimal withdrawableBalance;
    
    /**
     * 总收入
     */
    private BigDecimal totalIncome;
    
    /**
     * 总提现
     */
    private BigDecimal totalWithdraw;
    
    /**
     * 技术服务费总额
     */
    private BigDecimal serviceFeeTotal;
    
    /**
     * 最近交易记录
     */
    private List<WalletTransactionDTO> recentTransactions;
}
