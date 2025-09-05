package com.lanke.echomusic.dto.banner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Banner查询参数DTO")
public class BannerSearchDTO {
    
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
    
    @Schema(description = "Banner标题（模糊查询）", example = "邓紫棋")
    private String title;
    
    @Schema(description = "Banner描述（模糊查询）", example = "邓紫棋首发专辑")
    private String description;
    
    @Schema(description = "跳转类型（song-歌曲，album-专辑，playlist-歌单）", example = "song")
    private String type;
    
    @Schema(description = "Banner状态（1-启用，0-禁用）", example = "1")
    private Integer isActive;
    
    @Schema(
            description = "排序字段（格式：字段名,排序方向）。支持字段：sort, createdAt, updatedAt",
            example = "sort,asc;createdAt,desc"
    )
    private String orderBy = "sort,asc";
}