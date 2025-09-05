package com.lanke.echomusic.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "发表评论DTO")
public class CommentDTO {

    @Schema(description = "评论目标类型", example = "song", allowableValues = {"song", "playlist", "album"})
    @NotBlank(message = "评论目标类型不能为空")
    private String targetType;

    @Schema(description = "评论目标ID", example = "1")
    @NotNull(message = "评论目标ID不能为空")
    private Long targetId;

    @Schema(description = "父评论ID（回复评论时必填）", example = "1")
    private Long parentId;

    @Schema(description = "回复的用户ID（回复评论时必填）", example = "1")
    private Long replyToUserId;

    @Schema(description = "评论内容", example = "这首歌真好听！")
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 500, message = "评论内容长度必须在1-500字符之间")
    private String content;
}