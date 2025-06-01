package com.lanke.echomusic.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    /**
     * 生成JWT Token
     */
    public String generateToken(Map<String, Object> claims) {
        // 创建算法实例
        Algorithm algorithm = Algorithm.HMAC256(secret);

        // 创建JWT构建器
        JWTCreator.Builder builder = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + expire * 1000));

        // 添加自定义声明
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            if (entry.getValue() instanceof String) {
                builder.withClaim(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                builder.withClaim(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                builder.withClaim(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                builder.withClaim(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof Date) {
                builder.withClaim(entry.getKey(), (Date) entry.getValue());
            }
        }

        // 签名并生成Token
        return builder.sign(algorithm);
    }

    /**
     * 解析JWT Token
     */
    public Map<String, Object> parseToken(String token) {
        try {
            // 创建算法实例
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // 创建验证器
            JWTVerifier verifier = JWT.require(algorithm).build();

            // 验证并解码Token
            DecodedJWT jwt = verifier.verify(token);

            // 提取声明
            Map<String, Claim> claims = jwt.getClaims();
            Map<String, Object> result = new HashMap<>();

            for (Map.Entry<String, Claim> entry : claims.entrySet()) {
                result.put(entry.getKey(), entry.getValue().as(Object.class));
            }

            return result;
        } catch (JWTVerificationException e) {
            // Token验证失败
            return null;
        }
    }

    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            // 创建算法实例
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // 创建验证器
            JWTVerifier verifier = JWT.require(algorithm).build();

            // 验证Token
            verifier.verify(token);
            return false;
        } catch (JWTVerificationException e) {
            // Token验证失败，可能是过期了
            return true;
        }
    }
}