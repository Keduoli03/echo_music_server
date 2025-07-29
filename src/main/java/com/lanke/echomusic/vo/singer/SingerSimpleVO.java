package com.lanke.echomusic.vo.singer;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "歌手简单信息视图对象")
public class SingerSimpleVO {
    @Schema(description = "歌手ID")
    private Long id;

    @Schema(description = "歌手名称")
    private String name;

    @Schema(description = "歌手头像URL")
    private String avatar;
}
