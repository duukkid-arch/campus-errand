package com.campus.errand.security;

import com.campus.errand.common.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        String authorization = request.getHeader("Authorization");

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {
            throw new BizException("未登录或Token缺失");
        }

        String token = authorization.substring(7);

        try {
            Long userId = jwtUtil.parseUserId(token);

            // 把当前登录用户ID放到本次请求中
            request.setAttribute("userId", userId);

            return true;
        } catch (Exception e) {
            throw new BizException("Token无效或已过期");
        }
    }
}
