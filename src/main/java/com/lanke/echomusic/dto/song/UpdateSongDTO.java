package com.lanke.echomusic.dto.song;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "歌曲更新DTO")
public class UpdateSongDTO {
    @Schema(description = "歌曲ID", required = true)
    @NotNull(message = "歌曲ID不能为空")
    private Long id;

    @Schema(description = "歌曲名称")
    @Size(max = 100, message = "歌曲名称长度不能超过100个字符")
    private String songName;

    @Schema(description = "原名")
    @Size(max = 100, message = "原名长度不能超过100个字符")
    private String originalName;

    @Schema(description = "专辑名称")
    @Size(max = 100, message = "专辑名称长度不能超过100个字符")
    private String albumName;

    @Schema(description = "发行日期", example = "2023-05-10")
    private LocalDate releaseDate;

    @Schema(description = "歌曲风格/流派")
    @Size(max = 50, message = "风格长度不能超过50个字符")
    private String genre;

    @Schema(description = "歌曲语言")
    @Size(max = 20, message = "语言长度不能超过20个字符")
    private String language;

    @Schema(description = "歌曲时长（秒）")
    private Integer duration;

    @Schema(description = "作词")
    @Size(max = 100, message = "作词长度不能超过100个字符")
    private String lyricist;

    @Schema(description = "作曲")
    @Size(max = 100, message = "作曲长度不能超过100个字符")
    private String composer;

    @Schema(description = "编曲")
    @Size(max = 100, message = "编曲长度不能超过100个字符")
    private String arranger;

    @Schema(description = "歌手名称（多个歌手用逗号分隔）")
    private String singerName;

    @Schema(description = "歌曲描述")
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Schema(description = "歌曲状态 0-正常 1-禁用")
    private Byte status;
}