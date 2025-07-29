package com.lanke.echomusic.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "修改歌单信息请求参数")
public class PlaylistUpdateDTO {
    @NotNull(message = "歌单ID不能为空")
    @Schema(description = "歌单ID", required = true)
    private Long id;

    @Schema(description = "歌单名称")
    private String name;

    @Schema(description = "歌单描述")
    private String description;

    @Schema(description = "封面URL")
    private String coverUrl;

    @Schema(description = "是否公开 (1-公开, 0-私密)", type = "integer", defaultValue = "1")
    private Integer isPublic;
}