package com.lanke.echomusic.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "歌单搜索请求参数")
public class PlaylistSearchDTO {
    @Schema(description = "当前页码", defaultValue = "1")
    private long current = 1;
    
    @Schema(description = "每页大小", defaultValue = "10")
    private long size = 10;
    
    @Schema(description = "歌单名称（模糊查询）")
    private String name;
    
    @Schema(description = "创建者用户名（模糊查询）")
    private String creatorName;
    
    @Schema(description = "是否公开 (1-公开, 0-私密)")
    private Byte isPublic;
    
    @Schema(description = "音乐类型 (1-华语流行, 2-欧美流行, 3-日韩流行, 4-古典音乐, 5-民谣, 6-摇滚, 7-电子音乐, 8-说唱, 9-爵士, 10-其他)")
    private Integer musicType;
    
    @Schema(description = "歌单分类 (1-个人歌单, 2-精选歌单, 3-推荐歌单, 4-官方歌单, 5-主题歌单)")
    private Integer playlistCategory;
    
    @Schema(description = "排序字段", allowableValues = {"createTime", "updateTime", "playCount", "collectCount"})
    private String orderBy = "createTime";
    
    @Schema(description = "排序方式", allowableValues = {"asc", "desc"})
    private String orderDirection = "desc";
}