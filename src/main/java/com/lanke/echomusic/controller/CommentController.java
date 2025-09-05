package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.comment.CommentDTO;
import com.lanke.echomusic.dto.comment.CommentSearchDTO;
import com.lanke.echomusic.service.ICommentService;
import com.lanke.echomusic.vo.comment.CommentPageVO;
import com.lanke.echomusic.vo.comment.CommentTreeVO;
import com.lanke.echomusic.vo.comment.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 评论表 前端控制器
 * </p>
 *
 * @author lanke
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/comment")
@Tag(name = "评论管理接口", description = "提供评论发表、回复、查询等功能")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Operation(summary = "发表评论", description = "用户发表新评论")
    @PostMapping("/create")
    public Result<CommentVO> createComment(
            @Valid @RequestBody CommentDTO dto,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        CommentVO result = commentService.createComment(userId, dto);
        return Result.success("评论发表成功", result);
    }

    @Operation(summary = "回复评论", description = "用户回复已有评论")
    @PostMapping("/reply")
    public Result<CommentVO> replyComment(
            @Valid @RequestBody CommentDTO dto,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        CommentVO result = commentService.replyComment(userId, dto);
        return Result.success("回复成功", result);
    }

    @Operation(summary = "删除评论", description = "删除用户自己的评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        commentService.deleteComment(userId, commentId);
        return Result.success("评论删除成功");
    }

    @Operation(summary = "获取评论列表", description = "分页查询评论列表")
    @GetMapping("/getCommentList")
    public Result<CommentPageVO> getCommentsList(@ParameterObject CommentSearchDTO dto) {
        CommentPageVO result = commentService.getCommentsPage(dto);
        return Result.success("获取成功", result);
    }

    @Operation(summary = "获取树形评论列表", description = "获取带回复的树形结构评论列表")
    @GetMapping("/tree")
    public Result<CommentTreeVO> getCommentsTree(@ParameterObject CommentSearchDTO dto) {
        CommentTreeVO result = commentService.getCommentsTree(dto);
        return Result.success("获取成功", result);
    }

    @Operation(summary = "获取评论详情", description = "根据ID获取评论详情")
    @GetMapping("/{commentId}")
    public Result<CommentVO> getCommentById(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        CommentVO result = commentService.getCommentById(commentId, userId);
        if (result == null) {
            return Result.error("评论不存在");
        }
        return Result.success("获取成功", result);
    }

    @Operation(summary = "点赞/取消点赞评论", description = "切换评论点赞状态")
    @PostMapping("/{commentId}/like")
    public Result<Boolean> toggleLike(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        boolean isLiked = commentService.toggleLike(userId, commentId);
        String message = isLiked ? "点赞成功" : "取消点赞成功";
        return Result.success(message, isLiked);
    }

    @Operation(summary = "获取用户评论列表", description = "获取当前用户的评论列表")
    @GetMapping("/my")
    public Result<CommentPageVO> getUserComments(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        CommentPageVO result = commentService.getUserComments(userId, current, size);
        return Result.success("获取成功", result);
    }

    @Operation(summary = "获取回复列表", description = "获取指定评论的回复列表")
    @GetMapping("/{parentId}/replies")
    public Result<CommentPageVO> getReplies(
            @Parameter(description = "父评论ID") @PathVariable Long parentId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            HttpServletRequest request
    ) {
        Long userId = getUserIdFromRequest(request);
        CommentPageVO result = commentService.getReplies(parentId, current, size, userId);
        return Result.success("获取成功", result);
    }

    /**
     * 从请求中获取用户ID
     * 这里需要根据项目的认证机制来实现
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        // 这里需要根据项目的JWT或Session机制来获取用户ID
        // 暂时返回1L作为示例，实际项目中需要实现具体的用户认证逻辑
        return 1L;
    }
}