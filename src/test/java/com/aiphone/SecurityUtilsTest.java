package com.aiphone;

import com.aiphone.entity.User;
import com.aiphone.security.SecurityUtils;
import com.aiphone.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SecurityUtils测试类
 */
@SpringBootTest
@ActiveProfiles("test")
public class SecurityUtilsTest {

    @MockBean
    private UserService userService;

    @Test
    public void testGetCurrentUserId() {
        // 模拟用户
        User mockUser = new User();
        mockUser.setId(123L);
        mockUser.setOpenid("test_openid_123");
        mockUser.setNickname("测试用户");

        // 模拟UserDetails
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test_openid_123");

        // 模拟Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 模拟SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 模拟UserService
        when(userService.getUserByOpenid("test_openid_123")).thenReturn(mockUser);

        // 测试获取用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        assertEquals(123L, userId);

        // 验证UserService被调用
        verify(userService, times(1)).getUserByOpenid("test_openid_123");
    }

    @Test
    public void testGetCurrentUserIdWithNullAuthentication() {
        // 模拟空的SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // 测试未认证的情况
        assertThrows(Exception.class, () -> {
            SecurityUtils.getCurrentUserId();
        });
    }

    @Test
    public void testGetCurrentUserIdWithUserNotFound() {
        // 模拟UserDetails
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("non_existent_openid");

        // 模拟Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 模拟SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 模拟UserService返回null
        when(userService.getUserByOpenid("non_existent_openid")).thenReturn(null);

        // 测试用户不存在的情况
        assertThrows(Exception.class, () -> {
            SecurityUtils.getCurrentUserId();
        });
    }

    @Test
    public void testIsAuthenticated() {
        // 测试已认证的情况
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        assertTrue(SecurityUtils.isAuthenticated());

        // 测试未认证的情况
        when(authentication.isAuthenticated()).thenReturn(false);
        assertFalse(SecurityUtils.isAuthenticated());

        // 测试空认证的情况
        when(securityContext.getAuthentication()).thenReturn(null);
        assertFalse(SecurityUtils.isAuthenticated());
    }

    @Test
    public void testGetCurrentUserOpenid() {
        // 模拟UserDetails
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test_openid_123");

        // 模拟Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 模拟SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 测试获取openid
        String openid = SecurityUtils.getCurrentUserOpenid();
        assertEquals("test_openid_123", openid);
    }
} 