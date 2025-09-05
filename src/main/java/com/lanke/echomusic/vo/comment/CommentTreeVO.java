package com.lanke.echomusic.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "树形结构评论视图对象")
public class CommentTreeVO {

    @Schema(description = "顶级评论列表")
    private List<CommentVO> comments;

    @Schema(description = "总评论数")
    private Long totalCount;

    @Schema(description = "顶级评论数")
    private Long topLevelCount;
}