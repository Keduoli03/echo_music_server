package com.lanke.echomusic.dto.playlist;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "删除歌单请求参数")
public class PlaylistDeleteDTO {
    @NotNull(message = "歌单ID不能为空")
    @Schema(description = "歌单ID", required = true)
    private Long id;
}