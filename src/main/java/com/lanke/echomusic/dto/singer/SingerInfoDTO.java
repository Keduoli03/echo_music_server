package com.lanke.echomusic.dto.singer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "歌手信息传输对象")
public class SingerInfoDTO {
    @Schema(description = "歌手ID")
    private Long id;

    @Schema(description = "歌手名称", required = true)
    @NotBlank(message = "歌手名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100字")
    private String name;

    @Schema(description = "别名（多个用逗号分隔）")
    @Size(max = 200, message = "别名长度不能超过200字")
    private String alias;

    @Schema(description = "头像URL")
    @Size(max = 255, message = "URL长度不能超过255字")
    private String avatar;

    @Schema(description = "歌手简介")
    private String description;

    @Schema(description = "国籍")
    @Size(max = 50, message = "国籍长度不能超过50字")
    private String nationality;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "性别（0-未知，1-男，2-女）")
    private Integer gender;

}