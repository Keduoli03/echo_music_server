package com.lanke.echomusic.vo.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "评论视图对象")
public class CommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatarUrl;

    @Schema(description = "评论目标类型")
    private String targetType;

    @Schema(description = "目标对象名称（歌单名/歌曲名/专辑名）")
    private String targetName;

    @Schema(description = "评论目标ID")
    private Long targetId;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "回复的用户ID")
    private Long replyToUserId;

    @Schema(description = "回复的用户名")
    private String replyToUsername;

    @Schema(description = "回复的用户昵称")
    private String replyToNickname;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "回复数")
    private Integer replyCount;

    @Schema(description = "评论状态")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "回复列表（仅在查询顶级评论时包含）")
    private List<CommentVO> replies;

    @Schema(description = "是否为当前用户的评论")
    private Boolean isOwner;

    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;


}