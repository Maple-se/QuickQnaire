package com.maple.quickqnairebackend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by zong chang on 2024/11/30 21:50
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    // 这里移除 JwtTokenProvider 的依赖
    // 不再需要 JwtTokenProvider，因为我们将直接使用 JwtTokenUtil 静态方法
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenUtil.removeTokenPrefix(request);

        if (StringUtils.hasText(token)) {
            if(!jwtTokenUtil.isTokenExpired(token)){
                // 捕获token过期异常并返回响应
                response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 状态码
                // 设置响应内容
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print("登录令牌过期");
                return; // 直接返回，避免后续filter链的处理
            }
            Authentication auth = jwtTokenUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

}


