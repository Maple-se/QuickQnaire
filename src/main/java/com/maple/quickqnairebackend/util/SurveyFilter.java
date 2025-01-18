package com.maple.quickqnairebackend.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zong chang on 2024/12/24 12:26
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Component
public class SurveyFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 获取请求的 URI
        String requestUri = request.getRequestURI();

        // 检查路径是否匹配指定的模式
        if (isUrlMatch(requestUri)) {
            // 提取 encodedSurveyId
            String encodedSurveyId = extractSurveyIdFromUri(requestUri);
            // 获取当前的 Authentication 对象
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            //仅针对访问路径中有问卷id的URI
            if (encodedSurveyId != null ) {
                //System.out.println(encodedSurveyId);
                if(authentication != null){
                    // 将 encodedSurveyId 存储到 Authentication 的 details 中
                    UsernamePasswordAuthenticationToken authWithSurveyId =
                            new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());
                    authWithSurveyId.setDetails(encodedSurveyId);  // 存储 Survey 到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authWithSurveyId);
                }else {
                    //该匿名用户只在问卷过滤器中有效
                    UsernamePasswordAuthenticationToken anonymousAuthWithSurveyId = new UsernamePasswordAuthenticationToken("Anonymous","password123");
                    anonymousAuthWithSurveyId.setDetails(encodedSurveyId);  // 存储 Survey 到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(anonymousAuthWithSurveyId);
                }
            }
        }

        // 执行下一个过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 检查请求 URI 是否匹配需要拦截的路径模式
     */
    private boolean isUrlMatch(String uri) {
        // 你可以根据具体路径来匹配
        return uri.matches(".*/preview/.*") ||
                uri.matches(".*/delete-survey/.*") ||
                uri.matches(".*/update-survey/.*") ||
                uri.matches(".*/submit-for-approval/.*") ||
                uri.matches(".*/approval-survey/.*") ||
                uri.matches(".*/reject-survey/.*") ||
                uri.matches(".*/close-survey/.*") ||
                uri.matches(".*/delete-question/.*") ||
                uri.matches(".*/delete-option/.*") ||
                uri.matches(".*/detail/.*")||
                uri.matches(".*/submit-survey/.*") ;
    }

    /**
     * 从 URI 中提取 encodedSurveyId
     */
    private String extractSurveyIdFromUri(String uri) {
        // 假设路径模式是 "/preview/{encodedSurveyId}" 或类似形式
        String[] parts = uri.split("/");
        // 返回路径中的 encodedSurveyId
        return parts.length > 3 ? parts[3] : null;
    }
}
