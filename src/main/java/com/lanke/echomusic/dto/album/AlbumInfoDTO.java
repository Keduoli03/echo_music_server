package com.lanke.echomusic.dto.album;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    // albumName
    @Schema(description = "专辑名称", required = true, example = "叶惠美")
    @NotBlank(message = "专辑名称不能为空")
    @Size(max = 100, message = "专辑名称长度不能超过100个字符")
    private String albumName;

    // singerName
    @Schema(description = "歌手名称", example = "周杰伦")
    private String singerName;

    @Schema(description = "发行日期", required = true, example = "2003-07-31")
    private LocalDate releaseDate;
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Schema(description = "类型（1-专辑，2-EP，3-单曲）", required = true, example = "1")
    private Integer type;  // 改为 Integer

    @Schema(description = "状态（0-下架，1-上架）", example = "1")
    private Integer status;  // 改为 Integer
    
    // 添加缺少的字段
    @Schema(description = "专辑描述", example = "周杰伦的第四张录音室专辑")
    private String description;
    
    @Schema(description = "专辑封面URL", example = "http://localhost:9000/echo-music/album-covers/cover.jpg")
    private String coverUrl;

    // 在AlbumInfoDTO类中添加以下字段

    @Schema(description = "专辑下歌曲数量", example = "12")
    private Integer songCount;
}