package com.aiphone.service;

import com.aiphone.common.Result;
import com.aiphone.dto.WalletInfo;
import com.aiphone.dto.WithdrawalRequestDTO;

import com.aiphone.entity.WithdrawalRequest;
import com.aiphone.entity.WalletTransaction;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.Map;

import java.math.BigDecimal;

/**
 * 钱包服务接口
 */
public interface WalletService {
    
    /**
     * 获取钱包信息
     */
    WalletInfo getWalletInfo(Long userId);
    

    
    /**
     * 提现申请
     */
    Result<WithdrawalRequest> withdraw(Long userId, WithdrawalRequestDTO request);
    
    /**
     * 处理订单完成后的收入分配
     */
    Result<Boolean> processOrderIncome(Long orderId);
    
    /**
     * 处理延迟到账
     */
    Result<Boolean> processPendingWithdrawals();
    
    /**
     * 获取交易记录
     */
    IPage<WalletTransaction> getTransactions(Long userId, Integer page, Integer pageSize, String type, String relatedType);
    
    /**
     * 获取提现记录
     */
    IPage<WithdrawalRequest> getWithdrawalHistory(Long userId, Integer page, Integer pageSize, String status);
    
    /**
     * 获取延迟到账记录
     */
    IPage<WalletTransaction> getPendingWithdrawals(Long userId, Integer page, Integer pageSize, String status);
    
    /**
     * 获取钱包配置
     */
    Result<Object> getWalletConfig();
    
    /**
     * 获取钱包统计
     */
    Result<Object> getWalletStatistics(Long userId, String period);
    
    /**
     * 生成交易流水号
     */
    String generateTransactionNo();
    
    /**
     * 生成提现单号
     */
    String generateWithdrawalNo();
    
    /**
     * 计算提现手续费
     */
    BigDecimal calculateWithdrawalFee(BigDecimal amount);
    
    /**
     * 更新用户余额
     */
    boolean updateUserBalance(Long userId, BigDecimal amount);
    
    /**
     * 更新用户可提现金额
     */
    boolean updateUserWithdrawableBalance(Long userId, BigDecimal amount);
    
    /**
     * 更新用户统计信息
     */
    boolean updateUserStatistics(Long userId, BigDecimal income, BigDecimal serviceFee);
    
    /**
     * 处理提现申请（调用微信企业付款）
     */
    Result<Boolean> processWithdrawal(Long withdrawalId);
    
    /**
     * 查询提现状态
     */
    Result<String> queryWithdrawalStatus(String withdrawalNo);
}
