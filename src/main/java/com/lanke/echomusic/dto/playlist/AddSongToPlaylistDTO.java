package com.lanke.echomusic.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "添加歌曲到歌单请求参数")
public class AddSongToPlaylistDTO {
    @NotNull(message = "歌单ID不能为空")
    @Schema(description = "歌单ID", required = true)
    private Long playlistId;

    @NotNull(message = "歌曲ID不能为空")
    @Schema(description = "歌曲ID列表", required = true)
    private List<Long> songIds;
}