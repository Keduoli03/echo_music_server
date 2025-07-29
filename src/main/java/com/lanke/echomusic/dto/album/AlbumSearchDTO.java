package com.lanke.echomusic.dto.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "专辑查询参数DTO")
public class AlbumSearchDTO {
    
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
    
    @Schema(description = "专辑名称（模糊查询）", example = "素颜")
    private String albumName;
    
    @Schema(description = "歌手名称（模糊查询）", example = "许嵩")
    private String singerName;
    
    @Schema(description = "专辑状态（1-正常，0-禁用）", example = "1")
    private Integer status;
    
    @Schema(description = "专辑类型（1-录音室专辑，2-现场专辑，3-精选集，4-单曲等）", example = "1")
    private Integer type;
    
    // 歌曲数量查询参数
    @Schema(description = "最小歌曲数量（包含）", example = "0")
    private Integer minSongCount;
    
    @Schema(description = "最大歌曲数量（包含）", example = "10")
    private Integer maxSongCount;
}