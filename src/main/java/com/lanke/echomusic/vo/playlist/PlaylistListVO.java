package com.lanke.echomusic.vo.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "歌单列表项")
public class PlaylistListVO {
    @Schema(description = "歌单ID")
    private Long id;
    
    @Schema(description = "歌单名称")
    private String name;
    
    @Schema(description = "歌单描述")
    private String description;
    
    @Schema(description = "封面URL")
    private String coverUrl;
    
    @Schema(description = "创建者ID")
    private Long userId;
    
    @Schema(description = "创建者用户名")
    private String creatorName;
    
    @Schema(description = "歌曲数量")
    private Integer songCount;
    
    @Schema(description = "播放次数")
    private Long playCount;
    
    @Schema(description = "收藏次数")
    private Integer collectCount;
    
    @Schema(description = "是否公开 (1-公开, 0-私密)")
    private Byte isPublic;
    
    @Schema(description = "音乐类型")
    private Integer musicType;
    
    @Schema(description = "音乐类型名称")
    private String musicTypeName;
    
    @Schema(description = "歌单分类")
    private Integer playlistCategory;
    
    @Schema(description = "歌单分类名称")
    private String playlistCategoryName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}