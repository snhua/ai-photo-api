package com.aiphone.util;

import com.aiphone.entity.User;
import com.aiphone.security.SecurityUtils;
import com.aiphone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户上下文工具类
 */
@Component
public class UserContext {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        UserContext.userService = userService;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    /**
     * 获取当前登录用户openid
     */
    public static String getCurrentUserOpenid() {
        return SecurityUtils.getCurrentUserOpenid();
    }

    /**
     * 获取当前登录用户完整信息
     */
    public static User getCurrentUser() {
        String openid = getCurrentUserOpenid();
        return userService.getUserByOpenid(openid);
    }

    /**
     * 检查用户是否已登录
     */
    public static boolean isAuthenticated() {
        return SecurityUtils.isAuthenticated();
    }
} 