package com.aiphone.mapper;

import com.aiphone.entity.WalletTransaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包交易记录Mapper接口
 */
@Mapper
public interface WalletTransactionMapper extends BaseMapper<WalletTransaction> {
    
    /**
     * 根据用户ID获取交易记录
     */
    @Select("SELECT * FROM wallet_transactions WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<WalletTransaction> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据类型获取交易记录
     */
    @Select("SELECT * FROM wallet_transactions WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    List<WalletTransaction> getByType(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 获取用户总收入
     */
    @Select("SELECT SUM(amount) FROM wallet_transactions WHERE user_id = #{userId} AND type = 'income'")
    BigDecimal getUserIncome(@Param("userId") Long userId);
    
    /**
     * 获取用户总支出
     */
    @Select("SELECT SUM(amount) FROM wallet_transactions WHERE user_id = #{userId} AND type = 'expense'")
    BigDecimal getUserExpense(@Param("userId") Long userId);
    
    /**
     * 获取用户交易统计
     */
    @Select("SELECT COUNT(*) as total, SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) as income, SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) as expense FROM wallet_transactions WHERE user_id = #{userId}")
    WalletStatistics getUserWalletStatistics(@Param("userId") Long userId);
    
    /**
     * 钱包统计信息
     */
    class WalletStatistics {
        private Long total;
        private BigDecimal income;
        private BigDecimal expense;
        
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        
        public BigDecimal getIncome() { return income; }
        public void setIncome(BigDecimal income) { this.income = income; }
        
        public BigDecimal getExpense() { return expense; }
        public void setExpense(BigDecimal expense) { this.expense = expense; }
    }
} 