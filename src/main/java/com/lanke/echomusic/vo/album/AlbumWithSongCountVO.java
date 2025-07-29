package com.lanke.echomusic.vo.album;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "专辑查询结果VO（包含歌曲数量）")
public class AlbumWithSongCountVO {
    @Schema(description = "专辑ID")
    private Long id;
    
    @Schema(description = "专辑名称")
    private String albumName;
    
    @Schema(description = "歌手ID")
    private Long singerId;
    
    @Schema(description = "歌手名称")
    private String singerName;
    
    @Schema(description = "发行日期")
    private LocalDate releaseDate;
    
    @Schema(description = "专辑类型")
    private Byte type;
    
    @Schema(description = "专辑状态")
    private Byte status;
    
    @Schema(description = "专辑描述")
    private String description;
    
    @Schema(description = "封面URL")
    private String coverUrl;
    
    @Schema(description = "歌曲数量")
    private Integer songCount;
}