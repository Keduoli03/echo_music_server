package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.user.LoginDTO;
import com.lanke.echomusic.dto.user.PasswordUpdateDTO;
import com.lanke.echomusic.dto.user.UserUpdateDTO;
import com.lanke.echomusic.service.IUserService;
import com.lanke.echomusic.utils.RequestProcessor;
import com.lanke.echomusic.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户认证接口
 */
@Tag(name = "用户认证接口", description = "提供用户登录、登出等认证功能")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private RequestProcessor requestProcessor;

    // ------------------------ 登录/登出 ------------------------
    @Operation(summary = "用户登录", description = "通过用户名/邮箱和密码获取 JWT 令牌")
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        String token = userService.login(
                loginDTO.getUsernameOrEmail(),
                loginDTO.getPassword()
        );
        return Result.success("登录成功", token); // 移除 try-catch，异常由全局处理器处理
    }

    @Operation(summary = "用户登出", description = "通过 JWT 令牌退出登录（从 Redis 中移除令牌）")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam String token) {
        userService.logout(token);
        return Result.success(); // 移除 try-catch，异常由全局处理器处理
    }

    // ------------------------ 更新用户信息 ------------------------
    @Operation(summary = "更新用户信息", description = "不包含密码")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/updateUserInfo")
    public Result<Void> updateProfile(@RequestBody @Valid UserUpdateDTO dto) {
        try {
            Long userId = requestProcessor.getUserId(); // 直接获取用户ID，错误自动抛出
            userService.updateProfile(userId, dto);
            return Result.success("更新成功");
        } catch (IllegalArgumentException e) {
            return Result.error(401, e.getMessage()); // 捕获工具类抛出的异常
        }
    }

    // ------------------------ 获取用户信息 ------------------------
    @Operation(summary = "获取用户信息", description = "不包含密码")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/getUserInfo")
    public Result<UserVO> getProfile() {
        try {
            Long userId = requestProcessor.getUserId();
            UserVO user = userService.getProfile(userId);
            return Result.success("获取成功", user);
        } catch (IllegalArgumentException e) {
            return Result.error(401, e.getMessage());
        }
    }

    // ------------------------ 修改用户密码 ------------------------
    @Operation(summary = "修改用户密码", description = "修改用户密码")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/updatePassword")
    public Result<Void> updatePassword(@RequestBody @Valid PasswordUpdateDTO dto) {
        try {
            Long userId = requestProcessor.getUserId();
            userService.updatePassword(userId, dto);
            return Result.success("密码修改成功");
        } catch (IllegalArgumentException e) {
            return Result.error(401, e.getMessage());
        }
    }

    // ------------------------ 更新用户头像 ------------------------
    @Operation(summary = "更新用户头像", description = "上传新头像并更新用户信息")
    @SecurityRequirement(name = "Authorization")
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> updateAvatar(@RequestParam("avatar") MultipartFile avatarFile) {
        try {
            // 1. 获取用户ID
            Long userId = requestProcessor.getUserId();

            // 2. 文件验证
            if (avatarFile.isEmpty()) {
                return Result.error(400, "请选择头像文件");
            }
            if (!isValidImageType(avatarFile)) {
                return Result.error(400, "只支持JPG, PNG格式的图片");
            }

            // 3. 调用服务层更新头像
            userService.updateUserAvatar(userId, avatarFile);
            return Result.success("头像更新成功");
        } catch (IllegalArgumentException e) {
            // 4. 统一处理JWT相关异常
            return Result.error(401, e.getMessage());
        }
    }
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"));
    }

}