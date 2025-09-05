package com.lanke.echomusic.dto.song;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;


/**
 * <p>歌曲详情DTO</p>
 * 包含歌曲基础信息、所属专辑及关联歌手的完整信息
 *
 * @author lanke
 * @since 2025-06-02
 */
@Data
@Schema(description = "歌曲详情数据传输对象")
public class SongDetailDTO {

    // ====================== 歌曲基础信息 ======================
    @Schema(description = "歌曲ID", example = "1", required = true)
    private Long id;

    @Schema(description = "歌曲名称", example = "素颜", required = true)
    private String songName;

    @Schema(description = "歌曲原名", example = "素颜")
    private String originalName;

    @Schema(description = "作词", example = "许嵩")
    private String lyricist;

    @Schema(description = "作曲", example = "许嵩")
    private String composer;

    @Schema(description = "编曲", example = "许嵩")
    private String arranger;

    @Schema(description = "发行日期", example = "2010-08-18", format = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Schema(description = "歌曲语言", example = "中文")
    private String language;

    @Schema(description = "风格/流派")
    private String genre;
    
    @Schema(description = "音乐类型ID")
    private Integer musicType;
    
    @Schema(description = "音乐类型名称")
    private String musicTypeName;

    @Schema(description = "歌词内容")
    private String lyrics;

    @Schema(description = "播放地址URL", example = "http://music.example.com/suyan.mp3")
    private String playUrl;

    @Schema(description = "封面图片URL", example = "http://cover.example.com/suyan.jpg")
    private String coverUrl;

    @Schema(description = "歌曲状态（1-上架，0-下架）", example = "1")
    private Integer status;

    @Schema(description = "时长（秒）", example = "240")
    private Integer duration;

    @Schema(description = "歌手名称（多个歌手用/分割）", example = "周杰伦/林俊杰")
    private String singerName;

    @Schema(description = "专辑名称", example = "叶惠美")
    private String albumName;

    // ====================== 扩展字段（可选） ======================
    @Schema(description = "创建时间", example = "2025-06-02T15:00:00", format = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate createdAt;

    @Schema(description = "更新时间", example = "2025-06-02T15:00:00", format = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate updatedAt;
}