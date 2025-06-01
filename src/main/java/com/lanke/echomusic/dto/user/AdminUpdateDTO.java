package com.lanke.echomusic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理员更新用户信息DTO")
public class AdminUpdateDTO extends UserUpdateDTO {
    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long id;


    @Schema(description = "用户状态：0-禁用，1-启用", allowableValues = {"0", "1"}, example = "1")
    private Integer status;
}