package com.lanke.echomusic.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "评论分页结果")
public class CommentPageVO {

    @Schema(description = "当前页码")
    private Long current;

    @Schema(description = "每页大小")
    private Long size;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "评论列表")
    private List<CommentVO> records;
}