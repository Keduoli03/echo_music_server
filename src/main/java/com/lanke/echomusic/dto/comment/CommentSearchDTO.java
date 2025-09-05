package com.lanke.echomusic.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评论查询参数DTO")
public class CommentSearchDTO {

    @Schema(description = "当前页码（默认1）", example = "1", defaultValue = "1", minimum = "1")
    private long current = 1;

    @Schema(description = "每页大小（默认10）", example = "10", defaultValue = "10", minimum = "1", maximum = "100")
    private long size = 10;

    @Schema(description = "评论目标类型", example = "song", allowableValues = {"song", "playlist", "album"})
    private String targetType;

    @Schema(description = "评论目标ID", example = "1")
    private Long targetId;

    @Schema(description = "父评论ID（查询回复时使用）", example = "1")
    private Long parentId;

    @Schema(description = "用户ID（查询某用户的评论）", example = "1")
    private Long userId;

    @Schema(description = "当前用户ID（用于判断点赞状态等）", example = "1")
    private Long currentUserId;

    @Schema(description = "评论状态", example = "1", allowableValues = {"0", "1", "2"})
    private Integer status;

    @Schema(description = "排序字段", example = "created_at,desc")
    private String orderBy;
}