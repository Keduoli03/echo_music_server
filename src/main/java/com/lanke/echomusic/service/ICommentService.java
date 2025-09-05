package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.dto.comment.CommentDTO;
import com.lanke.echomusic.dto.comment.CommentSearchDTO;
import com.lanke.echomusic.entity.Comment;
import com.lanke.echomusic.vo.comment.CommentPageVO;
import com.lanke.echomusic.vo.comment.CommentTreeVO;
import com.lanke.echomusic.vo.comment.CommentVO;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-01-22
 */
public interface ICommentService extends IService<Comment> {

    /**
     * 发表评论
     * @param userId 用户ID
     * @param dto 评论信息
     * @return 评论详情
     */
    CommentVO createComment(Long userId, CommentDTO dto);

    /**
     * 回复评论
     * @param userId 用户ID
     * @param dto 回复信息
     * @return 回复详情
     */
    CommentVO replyComment(Long userId, CommentDTO dto);

    /**
     * 删除评论（软删除）
     * @param userId 用户ID
     * @param commentId 评论ID
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * 分页查询评论列表
     * @param dto 查询参数
     * @return 分页结果
     */
    CommentPageVO getCommentsPage(CommentSearchDTO dto);

    /**
     * 获取树形结构评论列表
     * @param dto 查询参数
     * @return 树形结构评论
     */
    CommentTreeVO getCommentsTree(CommentSearchDTO dto);

    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @param userId 当前用户ID（可为空）
     * @return 评论详情
     */
    CommentVO getCommentById(Long commentId, Long userId);

    /**
     * 点赞/取消点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return true-点赞，false-取消点赞
     */
    boolean toggleLike(Long userId, Long commentId);

    /**
     * 检查用户是否点赞了评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return true-已点赞，false-未点赞
     */
    boolean isLiked(Long userId, Long commentId);

    /**
     * 获取用户的评论列表
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    CommentPageVO getUserComments(Long userId, Long current, Long size);

    /**
     * 获取回复列表
     * @param parentId 父评论ID
     * @param current 当前页
     * @param size 每页大小
     * @param userId 当前用户ID（可为空）
     * @return 回复列表
     */
    CommentPageVO getReplies(Long parentId, Long current, Long size, Long userId);
}