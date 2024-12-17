package com.maple.quickqnairebackend.util;

import com.maple.quickqnairebackend.config.JwtConfiguration;
import com.maple.quickqnairebackend.dto.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Created by zong chang on 2024/11/30 21:39
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

//    注册的声明（建议但不强制使用）：
//    iss: jwt签发者
//    sub: jwt所面向的用户
//    aud: 接收jwt的一方
//    exp: jwt的过期时间，这个过期时间必须要大于签发时间
//    nbf: 定义在什么时间之前，该jwt都是不可用的.
//    iat: jwt的签发时间
//    jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。


    //private static final String SECRET_KEY = "your_secret_key";
    private static final long EXPIRATION_TIME = 86400; // 1 day in milliseconds

    private static final String USER_NAME = "username";
    private static final String USER_ID = "userId";
    private static final String ROLE = "role";

    private final JwtConfiguration jwtConfiguration;

    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    /**
     * 私钥 / 生成签名的时候使用的秘钥secret，一般可以从本地配置文件中读取，切记这个秘钥不能外露，只在服务端使用，在任何场景都不应该流露出去。
     * 一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
     * 应该大于等于 256位(长度32及以上的字符串)，并且是随机的字符串
     */
    private final static String SECRET = "mySuperSecretKeyWithAtLeast256BitsLengthForSecurityPurposes";
    /**
     * 秘钥实例
     */
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * jwt签发者
     */
    private final static String JWT_ISS = "Maple";

    // 生成 JWT Token
    public String generateToken(Authentication authentication) {
        Date exprireDate = Date.from(Instant.now().plusSeconds(EXPIRATION_TIME));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // 将 authorities 转换为 List<String>
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claim(USER_NAME,userDetails.getUsername())
                .claim(USER_ID, userDetails.getUserId())  // 将用户 ID 放入 payload
                .claim(ROLE, roles)  // 将角色信息加入 token
                .expiration(exprireDate)
                .issuedAt(new Date())
                .issuer(JWT_ISS)
                .signWith(KEY,ALGORITHM)
                .compact();
    }


    /**
     * 判断 token 是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public boolean isTokenValid(String token) {
        try {
            return !parseTokenPayload(token).getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    /**
     * 解析token
     * @param token token
     * @return Jws<Claims>
     */
    private Claims parseTokenPayload(String token) {
        return Jwts.parser()
                .verifyWith(KEY)  // 验证签名密钥
                .build()
                .parseSignedClaims(token)
                .getPayload();// 解析 Token;
    }

    // 获取用户角色
    public List<String> extractRole(String token) {
        Claims claims = parseTokenPayload(token);
        return claims.get(ROLE, List.class);  // 获取角色
    }

    // 获取用户 ID
    public Long extractUserId(String token) {
        Claims claims = parseTokenPayload(token);
        return claims.get(USER_ID, Long.class);  // 获取用户 ID
    }

    /**
     * 去除 Token 前缀
     *
     * @param request 原始 Token
     * @return 去除前缀后的 Token
     */
    public String removeTokenPrefix(HttpServletRequest request) {
        // 1. http 请求处理
        String token = request.getHeader(jwtConfiguration.getRequestHeaderKey());
        if (token != null && token.startsWith(jwtConfiguration.getTokenPrefix())) {
            return token.substring(jwtConfiguration.getTokenPrefix().length());
        }
        return null;
    }
}

