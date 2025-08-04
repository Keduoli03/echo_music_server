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

    @Schema(description = "是否公开 (1-公开, 0-私密)", type = "integer", defaultValue = "0")
    private Byte isPublic;

    @Schema(description = "音乐类型 (1-华语流行, 2-欧美流行, 3-日韩流行, 4-古典音乐, 5-民谣, 6-摇滚, 7-电子音乐, 8-说唱, 9-爵士, 10-其他)")
    private Integer musicType;

    @Schema(description = "歌单分类 (1-个人歌单, 2-精选歌单, 3-推荐歌单, 4-官方歌单, 5-主题歌单)", defaultValue = "1")
    private Integer playlistCategory;
}