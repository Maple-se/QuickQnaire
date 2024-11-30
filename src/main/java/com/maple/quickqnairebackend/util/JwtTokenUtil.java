package com.maple.quickqnairebackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * Created by zong chang on 2024/11/30 21:39
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Component
public class JwtTokenUtil {

    private static final String SECRET_KEY = "your_secret_key";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // 生成 JWT Token
    public static String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities())  // 将角色信息加入 token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 验证 Token 是否有效
    public static boolean validateToken(String token) {
        try {
            // 解析并验证 Token
            Jwts.builder()
                    .setSigningKey(SECRET_KEY)  // 设置签名密钥
                    .build()
                    .parseClaimsJws(token);    // 解析 Token
            return true;  // 如果没有异常，则 Token 有效
        } catch (Exception e) {
            // 如果发生异常，表示 Token 无效（例如过期或签名错误）
            return false;
        }
    }

    // 获取 Claims 信息
    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)  // 设置签名密钥
                .build()
                .parseClaimsJws(token)     // 解析 Token
                .getBody();  // 返回 Claims（有效载荷）
    }
}

