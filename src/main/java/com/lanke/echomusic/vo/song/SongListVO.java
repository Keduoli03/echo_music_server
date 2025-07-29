package com.lanke.echomusic.vo.song;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "歌曲列表项视图对象")
public class SongListVO {
    @Schema(description = "歌曲ID")
    private Long id;
    @Schema(description = "歌曲名称")
    private String songName;
    @Schema(description = "歌手")
    private String singerName;
    @Schema(description = "专辑名称")
    private String albumName;
    @Schema(description = "时长（秒）")
    private Integer duration;
    @Schema(description = "风格/流派")
    private String genre;
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
    @Schema(description = "状态（0-下架，1-上架）")
    private Integer status;
}