package com.lanke.echomusic.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
@Schema(description = "普通用户更新信息DTO")
public class UserUpdateDTO {
    @Schema(description = "用户名")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    private String userName;  // 改为 userName

    @Schema(description = "昵称")
    @Size(max = 30, message = "昵称长度不能超过30")
    private String nickName;  // 改为 nickName

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;     // 保持 email（不是 emailAddress）

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "简介")
    @Size(max = 200, message = "简介长度不能超过200")
    private String introduction;
}