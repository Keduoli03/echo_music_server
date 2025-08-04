package com.lanke.echomusic.vo.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "歌单分页结果")
public class PlaylistPageVO {
    @Schema(description = "当前页码")
    private long current;
    
    @Schema(description = "每页大小")
    private long size;
    
    @Schema(description = "总记录数")
    private long total;
    
    @Schema(description = "歌单列表")
    private List<PlaylistListVO> records;
}