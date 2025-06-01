package com.lanke.echomusic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "用户信息响应DTO")
public class UserInfoDTO {
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "昵称", example = "测试用户")
    private String nickname;

    @Schema(description = "邮箱", example = "test@example.com")
    private String email;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "简介", example = "个人简介")
    private String introduction;

    @Schema(description = "用户状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2023-01-01T12:00:00Z")
    private Date createdAt;

    @Schema(description = "最后登录时间", example = "2023-01-02T10:30:00Z")
    private Date lastLoginAt;

}