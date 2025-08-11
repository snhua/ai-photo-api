package com.aiphone.service.impl;

import com.aiphone.dto.UserLoginRequest;
import com.aiphone.dto.UserLoginResponse;
import com.aiphone.dto.UserUpdateRequest;
import com.aiphone.entity.User;
import com.aiphone.entity.WalletTransaction;
import com.aiphone.mapper.UserMapper;
import com.aiphone.mapper.WalletTransactionMapper;
import com.aiphone.security.WechatAuthenticationService;
import com.aiphone.service.UserService;
import com.aiphone.common.Result;
import com.aiphone.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private WechatAuthenticationService wechatAuthenticationService;
    
    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @Override
    public Result<UserLoginResponse> login(UserLoginRequest request) {
        try {
            log.info("开始处理微信登录请求: code={}", request.getCode());
            
            // 1. 微信认证，获取openid和token
            String openid = wechatAuthenticationService.getOpenidByCode(request.getCode());
            if (openid == null) {
                log.error("获取openid失败");
                return Result.error(1001, "微信登录失败，请重试");
            }
            
            // 2. 查找或创建用户
            User user = getUserByOpenid(openid);
            if (user == null) {
                // 创建新用户
                user = new User();
                user.setOpenid(openid);
                user.setNickname(request.getUserInfo().getNickName());
                user.setAvatar(request.getUserInfo().getAvatarUrl());
                user.setUserType("user");
                user.setStatus(1);
                user.setBalance(BigDecimal.ZERO);
                
                boolean success = createUser(user);
                if (!success) {
                    log.error("创建用户失败: openid={}", openid);
                    return Result.error(1005, "创建用户失败");
                }
                log.info("创建新用户成功: userId={}, openid={}", user.getId(), openid);
            } else {
                // 更新用户信息
//                user.setNickname(request.getUserInfo().getNickName());
//                user.setAvatar(request.getUserInfo().getAvatarUrl());
//                updateUser(user);
                log.info("更新用户信息成功: userId={}, openid={}", user.getId(), openid);
            }
            
            // 3. 生成JWT token
            String token = wechatAuthenticationService.generateToken(openid);
            
            // 4. 构建响应
            UserLoginResponse response = new UserLoginResponse();
            response.setToken(token);
            response.setUserInfo(user);
            
            log.info("用户登录成功: userId={}, openid={}", user.getId(), openid);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return Result.error(1005, "登录失败：" + e.getMessage());
        }
    }

    @Override
    public User getUserByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return getOne(wrapper);
    }

    @Override
    public User getUserById(Long userId) {
        return getById(userId);
    }

    @Override
    @Transactional
    public boolean createUser(User user) {
        // 检查用户是否已存在
        if (existsByOpenid(user.getOpenid())) {
            log.warn("用户已存在: openid={}", user.getOpenid());
            return false;
        }
        
        // 设置默认值
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getUserType() == null) {
            user.setUserType("user");
        }
        
        return save(user);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        return updateById(user);
    }

    @Override
    @Transactional
    public Result<User> updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = getById(userId);
        if (user == null) {
            return Result.error(1004, "用户不存在");
        }
        
        // 更新用户信息
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        
        boolean success = updateById(user);
        if (success) {
            return Result.success(user);
        } else {
            return Result.error(1005, "更新失败");
        }
    }

    @Override
    public IPage<User> getUserList(Page<User> page, String keyword, String userType) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(User::getNickname, keyword)
                .or()
                .like(User::getPhone, keyword)
            );
        }
        
        // 用户类型筛选
        if (StringUtils.hasText(userType)) {
            wrapper.eq(User::getUserType, userType);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(User::getCreatedAt);
        
        return page(page, wrapper);
    }

    @Override
    public List<User> getAllUsers() {
        return list();
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        return removeById(userId);
    }

    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    @Transactional
    public boolean updateUserBalance(Long userId, BigDecimal amount) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        BigDecimal newBalance = user.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("余额不足");
        }
        
        user.setBalance(newBalance);
        return updateById(user);
    }

    @Override
    @Transactional
    public Result<User> recharge(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error(1001, "充值金额必须大于0");
        }
        
        // 更新用户余额
        boolean success = updateUserBalance(userId, amount);
        if (!success) {
            return Result.error(1005, "充值失败");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("income");
        transaction.setAmount(amount);
        transaction.setDescription("账户充值");
        transaction.setRelatedType("recharge");
        walletTransactionMapper.insert(transaction);
        
        User user = getById(userId);
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result<User> withdraw(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Result.error(1001, "提现金额必须大于0");
        }
        
        User user = getById(userId);
        if (user.getBalance().compareTo(amount) < 0) {
            return Result.error(2002, "余额不足");
        }
        
        // 更新用户余额
        boolean success = updateUserBalance(userId, amount.negate());
        if (!success) {
            return Result.error(1005, "提现失败");
        }
        
        // 记录交易
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUserId(userId);
        transaction.setType("expense");
        transaction.setAmount(amount);
        transaction.setDescription("账户提现");
        transaction.setRelatedType("withdraw");
        walletTransactionMapper.insert(transaction);
        
        user = getById(userId);
        return Result.success(user);
    }

    @Override
    public boolean existsByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return count(wrapper) > 0;
    }

    @Override
    public UserStatistics getUserStatistics(Long userId) {
        UserStatistics statistics = new UserStatistics();
        statistics.setUserId(userId);
        
        User user = getById(userId);
        if (user != null) {
            statistics.setBalance(user.getBalance());
        }
        
        // 这里可以添加订单统计逻辑
        // 暂时返回默认值
        statistics.setTotalOrders(0);
        statistics.setCompletedOrders(0);
        statistics.setPendingOrders(0);
        statistics.setTotalAmount(BigDecimal.ZERO);
        
        return statistics;
    }
} 