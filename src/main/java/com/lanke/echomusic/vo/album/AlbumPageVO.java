package com.lanke.echomusic.vo.album;

import com.lanke.echomusic.dto.album.AlbumInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "专辑分页结果视图对象")
public class AlbumPageVO {
    @Schema(description = "当前页码")
    private long current;
    
    @Schema(description = "每页数量")
    private long size;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "记录列表")
    private List<AlbumInfoDTO> records;
}