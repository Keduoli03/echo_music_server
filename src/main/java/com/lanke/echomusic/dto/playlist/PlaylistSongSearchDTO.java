package com.lanke.echomusic.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "歌单歌曲搜索请求参数")
public class PlaylistSongSearchDTO {
    @NotNull(message = "歌单ID不能为空")
    @Schema(description = "歌单ID", required = true)
    private Long playlistId;

    @Schema(description = "当前页码", defaultValue = "1")
    private long current = 1;
    
    @Schema(description = "每页大小", defaultValue = "10")
    private long size = 10;
    
    @Schema(description = "歌曲名称（模糊查询）")
    private String songName;
    
    @Schema(description = "歌手名称（模糊查询）")
    private String singerName;
    
    @Schema(description = "专辑名称（模糊查询）")
    private String albumName;
    
    @Schema(description = "排序字段", allowableValues = {"addTime", "sort", "songName"})
    private String orderBy = "addTime";
    
    @Schema(description = "排序方式", allowableValues = {"asc", "desc"})
    private String orderDirection = "desc";
}