package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.user.LoginDTO;
import com.lanke.echomusic.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证接口
 */
@Tag(name = "用户认证接口", description = "提供用户登录、登出等认证功能")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Operation(summary = "用户登录",description = "通过用户名/邮箱和密码获取 JWT 令牌")
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            String token = userService.login(
                    loginDTO.getUsernameOrEmail(),
                    loginDTO.getPassword()
            );
            return Result.success(token);
        } catch (BadCredentialsException e) { // 捕获认证失败异常
            return Result.error(401, e.getMessage()); // 返回 401 状态码
        } catch (IllegalStateException e) { // 捕获用户禁用异常
            return Result.error(403, e.getMessage()); // 返回 403 状态码
        } catch (Exception e) { // 通用异常
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    @Operation( summary = "用户登出",description = "通过 JWT 令牌退出登录（从 Redis 中移除令牌）")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam String token) {
        try {
            userService.logout(token);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


}