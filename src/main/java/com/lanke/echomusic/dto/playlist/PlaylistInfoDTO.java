package com.lanke.echomusic.dto.playlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建歌单请求参数")
public class PlaylistInfoDTO {
    @Schema(description = "歌单名称", required = true)
    @NotBlank(message = "歌单名称不能为空")
    private String name;

    @Schema(description = "歌单描述")
    private String description;


    @Schema(description = "是否公开 (1-公开, 0-私密)", type = "integer",defaultValue = "0")
    private Byte isPublic;

    // getter/setter
}