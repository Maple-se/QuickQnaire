package com.maple.quickqnairebackend.util;

import com.maple.quickqnairebackend.entity.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;



/**
 * Created by zong chang on 2024/11/30 21:39
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Component
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
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

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
    public static String generateToken(User user) {
        // 令牌id
        String uuid = UUID.randomUUID().toString();
        Date exprireDate = Date.from(Instant.now().plusSeconds(EXPIRATION_TIME));
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claim("userId", user.getId())  // 将用户 ID 放入 payload
                .claim("role", user.getRole())  // 将角色信息加入 token
                .id(uuid)
                .expiration(exprireDate)
                .issuedAt(new Date())
                .subject(user.getUsername())
                .issuer(JWT_ISS)
                .signWith(KEY,ALGORITHM)
                .compact();
    }

    // 验证 Token 是否有效
    public static boolean validateToken(String token) {
        try {
            // 解析并验证 Token
            Jwts.parser()
                    .verifyWith(KEY)  // 验证签名密钥
                    .build()
                    .parseSignedClaims(token);// 解析 Token;
            return true;  // 如果没有异常，则 Token 有效
        } catch (Exception e) {
            // 如果发生异常，表示 Token 无效（例如过期或签名错误）
            return false;
        }
    }

    /**
     * 解析token
     * @param token token
     * @return Jws<Claims>
     */
    public static Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)  // 验证签名密钥
                .build()
                .parseSignedClaims(token);// 解析 Token;
    }

    // 获取用户角色
    public static String extractRole(String token) {
        Claims claims = parsePayload(token);
        return claims.get("role", String.class);  // 获取角色
    }

    // 获取用户 ID
    public static Long extractUserId(String token) {
        Claims claims = parsePayload(token);
        return claims.get("userId", Long.class);  // 获取用户 ID
    }

    public static JwsHeader parseHeader(String token) {
        return getClaims(token).getHeader();
    }

    // 返回 Claims（有效载荷）
    public static Claims parsePayload(String token) {
        return getClaims(token).getPayload();
    }
}

