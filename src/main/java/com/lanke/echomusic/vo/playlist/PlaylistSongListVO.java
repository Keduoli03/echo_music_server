package com.lanke.echomusic.vo.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "歌单歌曲列表项")
public class PlaylistSongListVO {
    @Schema(description = "关联表ID")
    private Long id;
    
    @Schema(description = "歌曲ID")
    private Long songId;
    
    @Schema(description = "歌曲名称")
    private String songName;
    
    @Schema(description = "歌手名称")
    private String singerName;
    
    @Schema(description = "专辑名称")
    private String albumName;
    
    @Schema(description = "时长（秒）")
    private Integer duration;
    
    @Schema(description = "风格/流派")
    private String genre;
    
    @Schema(description = "音乐类型ID")
    private Integer musicType;
    
    @Schema(description = "音乐类型名称")
    private String musicTypeName;
    
    @Schema(description = "语言")
    private String language;
    
    @Schema(description = "播放次数")
    private Long playCount;
    
    @Schema(description = "收藏次数")
    private Long likeCount;
    
    @Schema(description = "发行日期")
    private LocalDate releaseDate;
    
    @Schema(description = "封面URL")
    private String coverUrl;
    
    @Schema(description = "播放URL")
    private String playUrl;
    
    @Schema(description = "歌曲在歌单中的排序")
    private Integer sort;
    
    @Schema(description = "添加到歌单的时间")
    private LocalDateTime addTime;
    
    @Schema(description = "歌曲状态（0-下架，1-上架）")
    private Integer status;
}