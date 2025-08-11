package com.aiphone.service;

import com.aiphone.dto.UserLoginRequest;
import com.aiphone.dto.UserLoginResponse;
import com.aiphone.dto.UserUpdateRequest;
import com.aiphone.entity.User;
import com.aiphone.common.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 微信小程序登录
     * @param request 登录请求
     * @return 登录响应
     */
    Result<UserLoginResponse> login(UserLoginRequest request);
    
    /**
     * 根据openid获取用户信息
     * @param openid 微信openid
     * @return 用户信息
     */
    User getUserByOpenid(String openid);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);
    
    /**
     * 创建用户
     * @param user 用户信息
     * @return 创建结果
     */
    boolean createUser(User user);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUser(User user);
    
    /**
     * 更新用户基本信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新结果
     */
    Result<User> updateUserInfo(Long userId, UserUpdateRequest request);
    
    /**
     * 获取用户列表（分页）
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param userType 用户类型
     * @return 用户列表
     */
    IPage<User> getUserList(Page<User> page, String keyword, String userType);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteUser(Long userId);
    
    /**
     * 禁用/启用用户
     * @param userId 用户ID
     * @param status 状态：1-启用，0-禁用
     * @return 操作结果
     */
    boolean updateUserStatus(Long userId, Integer status);
    
    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param amount 金额变动
     * @return 更新结果
     */
    boolean updateUserBalance(Long userId, BigDecimal amount);
    
    /**
     * 充值
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 充值结果
     */
    Result<User> recharge(Long userId, BigDecimal amount);
    
    /**
     * 提现
     * @param userId 用户ID
     * @param amount 提现金额
     * @return 提现结果
     */
    Result<User> withdraw(Long userId, BigDecimal amount);
    
    /**
     * 检查用户是否存在
     * @param openid 微信openid
     * @return 是否存在
     */
    boolean existsByOpenid(String openid);
    
    /**
     * 获取用户统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    UserStatistics getUserStatistics(Long userId);
    
    /**
     * 用户统计信息
     */
    class UserStatistics {
        private Long userId;
        private Integer totalOrders;
        private Integer completedOrders;
        private Integer pendingOrders;
        private BigDecimal totalAmount;
        private BigDecimal balance;
        
        // getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Integer getTotalOrders() { return totalOrders; }
        public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
        
        public Integer getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(Integer completedOrders) { this.completedOrders = completedOrders; }
        
        public Integer getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(Integer pendingOrders) { this.pendingOrders = pendingOrders; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
    }
} 