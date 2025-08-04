package com.lanke.echomusic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "UserVO", description = "用户视图对象（不包含敏感信息）")
public class UserVO {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String userName;  // 改为驼峰命名

    @Schema(description = "昵称")
    private String nickName;  // 改为驼峰命名

    @Schema(description = "手机号")
    private String phone;  // 改为更标准的驼峰命名

    @Schema(description = "邮箱")
    private String email;  // 改为更标准的驼峰命名

    @Schema(description = "简介")
    private String introduction;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "角色：ADMIN(管理员)，USER(普通用户)")
    private String role;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "最后活跃时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveAt;
}