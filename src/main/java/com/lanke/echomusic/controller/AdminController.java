package com.lanke.echomusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.user.LoginDTO;
import com.lanke.echomusic.dto.user.UserSearchDTO;
import com.lanke.echomusic.service.IAdminService;
import com.lanke.echomusic.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证接口
 */
@Tag(name = "管理员认证接口", description = "提供管理员登录、登出等认证功能")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @Operation(summary = "用户登录",description = "通过用户名/邮箱和密码获取 JWT 令牌")
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            String token = adminService.login(
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
            adminService.logout(token);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户列表（支持分页和条件查询）
     *
     * @param searchDTO 查询参数
     * @return 用户分页数据
     *
     * @apiNote 支持的查询参数：
     * - pageNum: 页码（默认1）
     * - pageSize: 每页数量（默认10）
     * - usernameLike: 用户名模糊查询
     * - status: 用户状态（0-禁用，1-启用）
     * - orderBy: 排序字段，格式为 "字段名,排序方向"
     *   支持的字段：username, createdAt, status
     *   排序方向：asc（升序）或 desc（降序），默认降序
     *   示例："username,asc" 或 "createdAt"
     */
    @Operation(summary = "获取用户分页列表", description = "支持分页、条件筛选和多字段排序的用户查询接口")
    @GetMapping("/getAllUsers")
    public Result<IPage<UserVO>> getAllUsers(@ParameterObject UserSearchDTO searchDTO) {
        return Result.success(adminService.getAllUsers(searchDTO));
    }




}
