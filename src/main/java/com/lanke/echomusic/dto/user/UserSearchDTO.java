package com.lanke.echomusic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户查询参数DTO")
public class UserSearchDTO {

    @Schema(
            description = "当前页码（默认1）",
            example = "1",
            defaultValue = "1",
            minimum = "1"
    )
    private long current = 1;

    @Schema(
            description = "每页大小（默认10）",
            example = "10",
            defaultValue = "10",
            minimum = "1",
            maximum = "100"
    )
    private long size = 10;

    @Schema(
            description = "用户名模糊查询",
            example = "admin",
            minLength = 2,
            maxLength = 50
    )
    private String userName;

    @Schema(
            description = "昵称模糊查询",
            example = "管理员",
            minLength = 2,
            maxLength = 50
    )
    private String nickName;

    @Schema(
            description = "手机号模糊查询",
            example = "13800138",
            pattern = "^[0-9]*$",
            minLength = 6,
            maxLength = 20
    )
    private String userPhone;

    @Schema(
            description = "邮箱模糊查询",
            example = "@example.com",
            pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    )
    private String userEmail;

    @Schema(
            description = "用户状态：0-禁用, 1-启用",
            example = "1",
            allowableValues = {"0", "1"}
    )
    private Integer userStatus;

    @Schema(
            description = "排序字段（格式：字段名,排序方向）。支持字段：username, nickname, createdAt, status",
            example = "username,desc;createdAt,asc"
    )
    private String orderBy;
}
