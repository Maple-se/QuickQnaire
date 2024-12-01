package com.maple.quickqnairebackend.util;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
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
public class JwtTokenFilter extends OncePerRequestFilter {

    // 这里移除 JwtTokenProvider 的依赖
    // 不再需要 JwtTokenProvider，因为我们将直接使用 JwtTokenUtil 静态方法

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && JwtTokenUtil.validateToken(token)) {  // 直接调用 JwtTokenUtil 静态方法
            Long userId = JwtTokenUtil.extractUserId(token);
            String role = JwtTokenUtil.extractRole(token);

            // 设置认证信息，加入角色信息
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, AuthorityUtils.createAuthorityList(role));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}


