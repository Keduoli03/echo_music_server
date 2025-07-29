package com.lanke.echomusic.dto.singer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "歌手查询参数DTO")
public class SingerSearchDTO {
    
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
    
    @Schema(description = "歌手名称（模糊查询）", example = "许嵩")
    private String name;
    
    @Schema(description = "别名（模糊查询）", example = "vae")
    private String alias;
    
    @Schema(description = "国籍（模糊查询）", example = "中国")
    private String nationality;
    
    @Schema(description = "性别（0-未知，1-男，2-女）", example = "1")
    private Integer gender;
    
    @Schema(description = "状态（1-启用，0-禁用）", example = "1")
    private Integer status;
    
    @Schema(
            description = "排序字段（格式：字段名,排序方向）。支持字段：name, createdAt, birthDate",
            example = "name,asc;createdAt,desc"
    )
    private String orderBy;
}