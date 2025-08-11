package com.aiphone.mapper;

import com.aiphone.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户类型统计用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE user_type = #{userType}")
    Long countByUserType(@Param("userType") String userType);
    
    /**
     * 获取用户余额
     */
    @Select("SELECT balance FROM users WHERE id = #{userId}")
    BigDecimal getUserBalance(@Param("userId") Long userId);
    
    /**
     * 更新用户余额
     */
    @Update("UPDATE users SET balance = balance + #{amount} WHERE id = #{userId}")
    int updateUserBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
    
    /**
     * 获取活跃用户列表
     */
    @Select("SELECT * FROM users WHERE status = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<User> getActiveUsers(@Param("limit") Integer limit);
    
    /**
     * 根据关键词搜索用户
     */
    @Select("SELECT * FROM users WHERE nickname LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%')")
    List<User> searchUsers(@Param("keyword") String keyword);
} 