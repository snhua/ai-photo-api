package com.aiphone.security;

import com.aiphone.entity.User;
import com.aiphone.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 自定义用户详情服务
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.getUserByOpenid(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with openid: " + username);
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getOpenid())
                    .password("") // 微信登录不需要密码
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(user.getStatus() == 1)
                    .build();
        } catch (Exception e) {
            log.error("Error loading user by username: {}", username, e);
            throw new UsernameNotFoundException("User not found with openid: " + username);
        }
    }
} 