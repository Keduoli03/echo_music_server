package com.lanke.echomusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.user.*;
import com.lanke.echomusic.service.IAdminService;
import com.lanke.echomusic.utils.RequestProcessor;
import com.lanke.echomusic.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员认证接口
 */
@Tag(name = "管理员认证接口", description = "提供管理员登录、登出等认证功能")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @Autowired
    private RequestProcessor requestProcessor;

    // ------------------------ 登录/登出 ------------------------
    @Operation(summary = "用户登录", description = "通过用户名/邮箱和密码获取 JWT 令牌")
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        String token = adminService.login(
                loginDTO.getUsernameOrEmail(),
                loginDTO.getPassword()
        );
        return Result.success("登录成功", token); // 移除 try-catch，异常由全局处理器处理
    }

    @Operation(summary = "用户登出", description = "通过 JWT 令牌退出登录（从 Redis 中移除令牌）")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam String token) {
        adminService.logout(token);
        return Result.success(); // 移除 try-catch，异常由全局处理器处理
    }

    // ------------------------ 更新用户信息 ------------------------
    @Operation(summary = "更新用户信息", description = "不包含密码")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/updateUserInfo")
    public Result<Void> updateProfile(@RequestBody @Valid UserUpdateDTO dto) {
        try {
            Long userId = requestProcessor.getUserId(); // 直接获取用户ID，错误自动抛出
            adminService.updateProfile(userId, dto);
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
            UserVO user = adminService.getProfile(userId);
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
            adminService.updatePassword(userId, dto);
            return Result.success("密码修改成功");
        } catch (IllegalArgumentException e) {
            return Result.error(401, e.getMessage());
        }
    }

    // ------------------------ 更新用户头像 ------------------------
    @Operation(summary = "更新管理员用户头像", description = "上传新头像并更新用户信息")
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
            adminService.updateUserAvatar(userId, avatarFile);
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

    // ------------------------ 修改用户密码 ------------------------
    /**
     * 获取用户列表（支持分页和条件查询）
     *
     * @param searchDTO 查询参数
     * @return 用户分页数据
     *
     * @apiNote 支持的查询参数：
     * - current: 当前页码（默认1）
     * - size: 每页数量（默认10）
     * - usernameLike: 用户名模糊查询
     * - status: 用户状态（0-禁用，1-启用）
     * - orderBy: 排序字段，格式为 "字段名,排序方向"
     *   支持的字段：username, createdAt, status
     *   排序方向：asc（升序）或 desc（降序），默认降序
     *   示例："username,asc" 或 "createdAt"
     */
    @Operation(summary = "获取用户列表", description = "支持分页、条件筛选和多字段排序的用户查询接口")
    @GetMapping("/getUserList")
    public Result<IPage<UserVO>> getUserList(@ParameterObject UserSearchDTO searchDTO) {
        return Result.success("请求成功",adminService.getUserList(searchDTO));
    }



    // ------------------------ 管理员修改用户信息 ------------------------
    @Operation(summary = "管理员修改指定用户信息", description = "支持修改用户基本信息（不包含密码）")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/updateUser/{userId}")
    public Result<Void> UpdateUserInfo(
            @PathVariable Long userId, // 目标用户ID
            @RequestBody @Valid AdminUpdateDTO dto
    ) {
        try {
            Long adminId = requestProcessor.getUserId();
            adminService.updateUserInfo(adminId, userId, dto);
            return Result.success("用户信息修改成功");
        } catch (IllegalArgumentException e) {
            return Result.error(403, e.getMessage()); // 权限错误返回 403
        } catch (Exception e) {
            return Result.error(500, "修改失败：" + e.getMessage());
        }
    }

    // ------------------------ 管理员修改用户密码 ------------------------
    @Operation(summary = "管理员强制修改用户密码", description = "无需用户当前密码，直接设置新密码")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/updateUserPassword/{userId}")
    public Result<Void> adminUpdateUserPassword(
            @PathVariable Long userId, // 目标用户ID
            @RequestBody @Valid PasswordUpdateDTO dto
    ) {
        try {
            // 1. 验证新密码和确认密码一致
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                return Result.error(400, "新密码与确认密码不一致");
            }

            // 2. 获取管理员ID并执行修改
            Long adminId = requestProcessor.getUserId();
            adminService.updateUserPassword(adminId, userId, dto.getNewPassword());
            return Result.success("用户密码修改成功");
        } catch (IllegalArgumentException e) {
            return Result.error(403, e.getMessage()); // 权限错误返回 403
        } catch (Exception e) {
            return Result.error(500, "修改失败：" + e.getMessage());
        }
    }


}
