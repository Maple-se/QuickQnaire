package com.maple.quickqnairebackend.util;

import com.maple.quickqnairebackend.dto.CustomUserDetails;
import com.maple.quickqnairebackend.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zong chang on 2024/12/17 19:02
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
public class SecurityUtil {
    private SecurityUtil() {

    }

    /**
     * 获取Security中上下文中的对象
     *
     * @return CustomUserDetails
     */
    public static CustomUserDetails getUser() {
        // 从全局 SecurityContextHolder 中获取 Authentication 找到 CustomUserDetails 进行返回
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                return (CustomUserDetails) authentication.getPrincipal();
            }
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 登录信息
     */
    public static Long getUserId() {
        CustomUserDetails user = getUser();
        if (user != null) {
            return user.getUserId();
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        CustomUserDetails user = getUser();
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 登录信息
     */
    public static Long getUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        return null;
    }

    /**
     * 获取当前登录用户角色信息
     *
     * @return 角色信息
     */
    public static List<String> getUserRoles() {
        Collection<? extends GrantedAuthority> authorities = getAuthentication().getAuthorities();
        if (authorities != null) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 获取 Security 上下文中 Authentication 对象
     *
     * @return Authentication 对象
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * @return 是否是管理员
     */
    public static boolean isAdmin() {
        List<String> userRoles = getUserRoles();
        if (userRoles == null) {
            return false;
        }
        return userRoles.contains(String.valueOf(User.Role.ADMIN));
    }
}
