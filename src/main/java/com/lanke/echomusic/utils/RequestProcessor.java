package com.lanke.echomusic.utils;

import com.lanke.echomusic.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RequestProcessor {

    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public RequestProcessor(JwtUtil jwtUtil, HttpServletRequest request) {
        this.jwtUtil = jwtUtil;
        this.request = request;
    }

    /**
     * 提取用户ID（简化版）：直接返回ID，错误时抛出异常
     * @return 用户ID
     * @throws IllegalArgumentException 当令牌无效或用户ID缺失时抛出
     */
    public Long getUserId() {
        Result<Long> userIdResult = extractUserId();
        if (!userIdResult.isSuccess()) { // 假设 Result 有 isSuccess() 方法
            throw new IllegalArgumentException(userIdResult.getMsg());
        }
        return userIdResult.getData();
    }

    /**
     * 原始提取方法（返回 Result 对象）
     */
    public Result<Long> extractUserId() {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Result.error(401, "无效的授权头");
        }

        String token = authorizationHeader.substring(7);
        if (jwtUtil.isTokenExpired(token)) {
            return Result.error(401, "令牌已过期");
        }

        Map<String, Object> claims = jwtUtil.parseToken(token);
        if (claims == null || claims.isEmpty()) {
            return Result.error(401, "无效的令牌");
        }

        Object userIdObj = claims.get("userId");
        if (userIdObj == null) {
            return Result.error(401, "令牌中缺少用户ID");
        }

        try {
            return Result.success(Long.parseLong(userIdObj.toString()));
        } catch (NumberFormatException e) {
            return Result.error(401, "用户ID格式错误");
        }
    }
}