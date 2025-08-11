package com.aiphone.security;

import com.aiphone.common.exception.UnauthorizedException;
import com.aiphone.entity.User;
import com.aiphone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 */
@Component
public class SecurityUtils {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        SecurityUtils.userService = userService;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String openid = ((UserDetails) principal).getUsername();
            // 通过openid获取用户ID
            User user = userService.getUserByOpenid(openid);
            if (user != null) {
                return user.getId();
            } else {
                throw new UnauthorizedException("用户不存在");
            }
        }
        
        throw new UnauthorizedException("无法获取用户信息");
    }

    /**
     * 获取当前登录用户openid
     */
    public static String getCurrentUserOpenid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("用户未登录");
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        
        throw new UnauthorizedException("无法获取用户信息");
    }

    /**
     * 检查用户是否已登录
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 检查用户是否有指定权限
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(authority));
    }
} 