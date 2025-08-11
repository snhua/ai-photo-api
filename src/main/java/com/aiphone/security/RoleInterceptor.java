package com.aiphone.security;

import com.aiphone.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色权限拦截器
 */
@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        HasRole hasRole = handlerMethod.getMethodAnnotation(HasRole.class);
        
        if (hasRole != null) {
            String requiredRole = hasRole.value();
            if (!SecurityUtils.hasAuthority("ROLE_" + requiredRole)) {
                throw new UnauthorizedException("权限不足");
            }
        }

        return true;
    }
}