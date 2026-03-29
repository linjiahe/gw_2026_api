package com.gw.server.security;

import com.gw.server.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Skip preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);
        if (token == null) {
            throw new BusinessException(401, "未登录，请先连接钱包登录");
        }

        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // Store userId and address in request attributes for controllers to use
        Integer userId = jwtTokenProvider.getUserIdFromToken(token);
        String address = jwtTokenProvider.getAddressFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("walletAddress", address);

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
