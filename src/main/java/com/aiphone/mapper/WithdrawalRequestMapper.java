package com.aiphone.mapper;

import com.aiphone.entity.WithdrawalRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 提现申请Mapper接口
 */
@Mapper
public interface WithdrawalRequestMapper extends BaseMapper<WithdrawalRequest> {
    
    /**
     * 根据用户ID获取提现记录
     */
    @Select("SELECT * FROM withdrawal_requests WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<WithdrawalRequest> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据状态获取提现记录
     */
    @Select("SELECT * FROM withdrawal_requests WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<WithdrawalRequest> getByStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 获取用户今日提现金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM withdrawal_requests WHERE user_id = #{userId} AND DATE(created_at) = CURDATE()")
    BigDecimal getTodayWithdrawalAmount(@Param("userId") Long userId);
    
    /**
     * 检查提现单号是否存在
     */
    @Select("SELECT COUNT(*) FROM withdrawal_requests WHERE withdrawal_no = #{withdrawalNo}")
    int checkWithdrawalNoExists(@Param("withdrawalNo") String withdrawalNo);
}
