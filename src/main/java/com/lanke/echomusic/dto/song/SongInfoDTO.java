package com.lanke.echomusic.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "歌曲创建DTO")
public class SongInfoDTO {
    @Schema(description = "歌曲名称", required = true)
    @NotBlank(message = "歌曲名称不能为空")
    @Size(max = 100, message = "歌曲名称长度不能超过100个字符")
    private String songName;

    @Schema(description = "原名")
    @Size(max = 100, message = "原名长度不能超过100个字符")
    private String originalName;

    @Schema(description = "专辑ID")
    private Long albumId;

    @Schema(description = "发行日期", example = "2023-05-10")
    private LocalDate releaseDate;

    @Schema(description = "歌曲风格/流派")
    @Size(max = 50, message = "风格长度不能超过50个字符")
    private String genre;

    @Schema(description = "歌曲语言")
    @Size(max = 20, message = "语言长度不能超过20个字符")
    private String language;

    @Schema(description = "作词")
    @Size(max = 100, message = "作词长度不能超过100个字符")
    private String lyricist;

    @Schema(description = "作曲")
    @Size(max = 100, message = "作曲长度不能超过100个字符")
    private String composer;

    @Schema(description = "编曲")
    @Size(max = 100, message = "编曲长度不能超过100个字符")
    private String arranger;

    @Schema(description = "歌词")
    private String lyrics;

    @Schema(description = "播放URL")
    private String playUrl;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "歌曲状态", example = "1")
    private Integer status;

    @Schema(description = "歌曲时长（秒）")
    private Integer duration;

    @Schema(description = "歌手名称列表（多个用逗号分隔）", example = "许嵩,何曼婷")
    @NotBlank(message = "歌手名称不能为空")
    private String singerName; // 改名：从 artistNames 改为 singerName

    @Schema(description = "专辑名称（自动创建时必填）", example = "素颜")
    private String albumName;
}