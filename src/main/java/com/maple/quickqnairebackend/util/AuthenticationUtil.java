package com.maple.quickqnairebackend.util;

import com.maple.quickqnairebackend.entity.User;
import com.maple.quickqnairebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zong chang on 2024/12/4 19:32
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Component
public class AuthenticationUtil {

    @Autowired
    private UserService userService;

    public Long authenticateAndGetUserId(String authorization) throws IllegalArgumentException {
        if (authorization == null || authorization.isEmpty()) {
            throw new IllegalArgumentException("Authorization token is missing.");
        }

        // 从 Authorization 头中提取 token
        String token = authorization.replace("Bearer ", "");
        Long userId = JwtTokenUtil.extractUserId(token);

        if (userId == null) {
            throw new IllegalArgumentException("Invalid Token");
        }

        // 可以在此处扩展验证用户是否有效的逻辑
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return userId;
    }
}
