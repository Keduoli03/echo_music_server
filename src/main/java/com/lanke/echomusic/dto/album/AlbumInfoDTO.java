package com.lanke.echomusic.dto.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * <p>专辑信息DTO</p>
 * <p>对应数据库表：l_album</p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@Data
@Schema(description = "专辑信息传输对象")
public class AlbumInfoDTO {

    // ---------------------- 基础信息 ----------------------
    @Schema(description = "专辑ID（新增时无需填写，更新时必填）", example = "1")
    private Long id;

    @Schema(description = "专辑名称", required = true, example = "叶惠美")
    @NotBlank(message = "专辑名称不能为空")
    @Size(max = 100, message = "专辑名称长度不能超过100个字符")
    private String name;

    @Schema(description = "歌手ID", required = true, example = "2001")
    @NotNull(message = "歌手ID不能为空")
    private Long singerId;

    @Schema(description = "发行日期", required = true, example = "2003-07-31")
    private LocalDate releaseDate;

    @Schema(description = "类型（1-专辑，2-EP，3-单曲）", required = true, example = "1")
    private Byte type; // 对应数据库TINYINT类型

    // ---------------------- 可选信息 ----------------------
    @Schema(description = "专辑描述", example = "周杰伦第四张专辑")
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Schema(description = "封面URL", example = "http://example.com/album-cover.jpg")
    @Size(max = 255, message = "URL长度不能超过255个字符")
    private String coverUrl;

    @Schema(description = "状态（0-下架，1-上架）", example = "1")
    private Byte status; // 默认为1（上架）
}