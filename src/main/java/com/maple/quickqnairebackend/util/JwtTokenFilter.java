package com.maple.quickqnairebackend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
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

        if (token != null && jwtTokenUtil.isTokenValid(token)) {  // 直接调用 JwtTokenUtil 静态方法
            Long userId = jwtTokenUtil.extractUserId(token);
            String role = jwtTokenUtil.extractRole(token);

            // 设置认证信息，加入角色信息
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, AuthorityUtils.createAuthorityList(role));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}


