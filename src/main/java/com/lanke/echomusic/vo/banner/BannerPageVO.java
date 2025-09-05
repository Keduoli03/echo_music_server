package com.lanke.echomusic.vo.banner;

import com.lanke.echomusic.entity.Banner;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Banner分页结果视图对象")
public class BannerPageVO {
    @Schema(description = "当前页码")
    private long current;

    @Schema(description = "每页数量")
    private long size;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "Banner记录列表")
    private List<Banner> records;
}