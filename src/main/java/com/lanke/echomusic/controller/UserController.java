package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.user.LoginDTO;
import com.lanke.echomusic.dto.user.UserUpdateDTO;
import com.lanke.echomusic.service.IUserService;
import com.lanke.echomusic.utils.JwtUtil;
import com.lanke.echomusic.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 用户认证接口
 */
@Tag(name = "用户认证接口", description = "提供用户登录、登出等认证功能")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private JwtUtil JwtUtil;

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


    @Operation(summary = "更新用户信息", description = "更新当前用户的个人信息")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/updateUserInfo")
    public Result<Void> updateProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid UserUpdateDTO dto) {
        try {
            // 1. 验证Authorization头格式
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error(401, "无效的授权头");
            }

            // 2. 提取Token
            String token = authorizationHeader.substring(7);

            // 3. 验证Token有效性
            if (JwtUtil.isTokenExpired(token)) {
                return Result.error(401, "令牌已过期");
            }

            // 4. 解析Token获取用户信息
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (claims == null || claims.isEmpty()) {
                return Result.error(401, "无效的令牌");
            }

            // 5. 获取用户ID并转换为Long类型
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                return Result.error(401, "令牌中缺少用户ID");
            }

            Long userId;
            try {
                userId = Long.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                return Result.error(401, "用户ID格式错误");
            }

            // 6. 使用用户ID更新信息
            userService.updateProfile(userId, dto);
            return Result.success();

        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "更新失败：" + e.getMessage());
        }
    }


    @Operation(summary = "获取当前用户信息", description = "获取已登录用户的详细信息")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/getUserInfo")
    public Result<UserVO> getProfile(
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // 1. 验证Authorization头格式
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error(401, "无效的授权头");
            }

            // 2. 提取Token
            String token = authorizationHeader.substring(7);

            // 3. 验证Token有效性
            if (JwtUtil.isTokenExpired(token)) {
                return Result.error(401, "令牌已过期");
            }

            // 4. 解析Token获取用户ID
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (claims == null || !claims.containsKey("userId")) {
                return Result.error(401, "令牌中缺少用户ID");
            }

            Long userId = Long.valueOf(claims.get("userId").toString());

            // 5. 查询用户信息
            UserVO user = userService.getProfile(userId);
            return Result.success(user);

        } catch (NumberFormatException e) {
            return Result.error(401, "用户ID格式错误");
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取用户信息失败：" + e.getMessage());
        }
    }



    @Operation(summary = "更新用户头像", description = "上传新头像并更新用户信息")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> updateAvatar(
            @RequestHeader("Authorization") String authorizationHeader,
            @Parameter(description = "头像文件", required = true)
            @RequestParam("avatar") MultipartFile avatarFile) {

        try {
            // 1. 从请求头中提取Token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return Result.error(401, "无效的授权头");
            }
            String token = authorizationHeader.substring(7);

            // 2. 验证Token并获取用户ID
            Map<String, Object> claims = JwtUtil.parseToken(token);
            if (claims == null || !claims.containsKey("userId")) {
                return Result.error(401, "令牌中缺少用户ID");
            }

            Long userId = Long.valueOf(claims.get("userId").toString());
            // 3. 基本文件验证
            if (avatarFile.isEmpty()) {
                return Result.error(400, "请选择头像文件");
            }

            // 可选：添加文件类型和大小验证
            if (!isValidImageType(avatarFile)) {
                return Result.error(400, "只支持JPG, PNG格式的图片");
            }

            // 4. 更新用户头像
            userService.updateUserAvatar(userId, avatarFile);
            return Result.success("头像更新成功");

        } catch (Exception e) {
            return Result.error(500, "头像更新失败: " + e.getMessage());
        }
    }

    // 可选：验证图片类型
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"));
    }



}