package com.aiphone.mapper;

import com.aiphone.entity.WalletPendingWithdrawal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 延迟到账记录Mapper接口
 */
@Mapper
public interface WalletPendingWithdrawalMapper extends BaseMapper<WalletPendingWithdrawal> {
    
    /**
     * 根据用户ID获取延迟到账记录
     */
    @Select("SELECT * FROM wallet_pending_withdrawals WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<WalletPendingWithdrawal> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据状态获取延迟到账记录
     */
    @Select("SELECT * FROM wallet_pending_withdrawals WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<WalletPendingWithdrawal> getByStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 获取到期的延迟到账记录
     */
    @Select("SELECT * FROM wallet_pending_withdrawals WHERE status = 'pending' AND available_at <= #{now}")
    List<WalletPendingWithdrawal> getExpiredRecords(@Param("now") LocalDateTime now);
    
    /**
     * 更新用户可提现金额
     */
    @Update("UPDATE users SET withdrawable_balance = withdrawable_balance + #{amount} WHERE id = #{userId}")
    int updateUserWithdrawableBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    /**
     * 获取用户待处理金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM wallet_pending_withdrawals WHERE user_id = #{userId} AND status = 'pending'")
    BigDecimal getPendingAmount(@Param("userId") Long userId);
}
