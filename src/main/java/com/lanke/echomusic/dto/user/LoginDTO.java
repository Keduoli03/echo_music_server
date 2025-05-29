package com.lanke.echomusic.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 用户登录数据传输对象
 */
@Data
public class LoginDTO {

    /**
     * 用户名或邮箱
     */
    @NotBlank(message = "用户名或邮箱不能为空")
    @Pattern(regexp = "^(?:[a-zA-Z0-9]+\\.?)*[a-zA-Z0-9]+@(?:[a-zA-Z0-9]+\\.)+[a-zA-Z]{2,}$|^[a-zA-Z0-9_]+$",
            message = "请输入有效的用户名或邮箱")
    private String usernameOrEmail;

    /**
     * 密码
     * 最小长度为6位
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;
}
