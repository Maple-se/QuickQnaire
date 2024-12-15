package com.maple.quickqnairebackend.config;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Created by zong chang on 2024/12/15 19:07
 *
 * @author : Maple-se
 * @version : 1.0
 * @description :
 */
@Data
@Component
public class JwtConfiguration {
    /**
     * 生成 token 的前缀
     */
    private String tokenPrefix = "Bearer ";
    /**
     * 请求头存储 token 的 key
     */
    private String requestHeaderKey = HttpHeaders.AUTHORIZATION;
    /**
     * JWT 密匙
     */
    private String secretKey = "mySuperSecretKeyWithAtLeast256BitsLengthForSecurityPurposes";;
    /**
     * 默认 Token 过期时间（小时为单位）
     */
    private Duration tokenExpirationTime = Duration.ofHours(8);
}
