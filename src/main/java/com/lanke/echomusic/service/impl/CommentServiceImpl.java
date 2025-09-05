package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.dto.comment.CommentDTO;
import com.lanke.echomusic.dto.comment.CommentSearchDTO;
import com.lanke.echomusic.entity.Comment;
import com.lanke.echomusic.entity.CommentLike;
import com.lanke.echomusic.enums.CommentStatus;
import com.lanke.echomusic.enums.CommentTargetType;
import com.lanke.echomusic.mapper.CommentMapper;
import com.lanke.echomusic.mapper.CommentLikeMapper;
import com.lanke.echomusic.service.ICommentService;
import com.lanke.echomusic.vo.comment.CommentPageVO;
import com.lanke.echomusic.vo.comment.CommentTreeVO;
import com.lanke.echomusic.vo.comment.CommentVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-01-22
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(Long userId, CommentDTO dto) {
        // 验证目标类型
        CommentTargetType targetType = CommentTargetType.fromCode(dto.getTargetType());
        if (targetType == null) {
            throw new IllegalArgumentException("不支持的评论目标类型");
        }

        // 创建评论实体
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setTargetType(dto.getTargetType());
        comment.setTargetId(dto.getTargetId());
        comment.setContent(dto.getContent());
        comment.setStatus(CommentStatus.NORMAL.getCode());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // 保存评论
        this.save(comment);

        // 返回评论详情
        return getCommentById(comment.getId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO replyComment(Long userId, CommentDTO dto) {
        // 验证父评论是否存在
        if (dto.getParentId() == null) {
            throw new IllegalArgumentException("回复评论时父评论ID不能为空");
        }

        Comment parentComment = this.getById(dto.getParentId());
        if (parentComment == null || !CommentStatus.NORMAL.getCode().equals(parentComment.getStatus())) {
            throw new IllegalArgumentException("父评论不存在或已被删除");
        }

        // 创建回复实体
        Comment reply = new Comment();
        reply.setUserId(userId);
        reply.setTargetType(parentComment.getTargetType());
        reply.setTargetId(parentComment.getTargetId());
        reply.setParentId(dto.getParentId());
        reply.setReplyToUserId(dto.getReplyToUserId());
        reply.setContent(dto.getContent());
        reply.setStatus(CommentStatus.NORMAL.getCode());
        reply.setLikeCount(0);
        reply.setReplyCount(0);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());

        // 保存回复
        this.save(reply);

        // 更新父评论的回复数
        baseMapper.updateReplyCount(dto.getParentId(), 1);

        // 返回回复详情
        return getCommentById(reply.getId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
    
        // 验证权限（只能删除自己的评论）
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权限删除此评论");
        }
    
        // 1. 删除该评论的所有点赞记录
        commentLikeMapper.delete(new LambdaQueryWrapper<CommentLike>()
                .eq(CommentLike::getCommentId, commentId));
    
        // 2. 如果是顶级评论，需要删除所有回复
        if (comment.getParentId() == null) {
            // 查找所有回复
            List<Comment> replies = this.list(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getParentId, commentId));
            
            // 删除每个回复的点赞记录
            for (Comment reply : replies) {
                commentLikeMapper.delete(new LambdaQueryWrapper<CommentLike>()
                        .eq(CommentLike::getCommentId, reply.getId()));
            }
            
            // 删除所有回复
            this.remove(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getParentId, commentId));
        }
    
        // 3. 如果是回复，更新父评论的回复数
        if (comment.getParentId() != null) {
            baseMapper.updateReplyCount(comment.getParentId(), -1);
        }
    
        // 4. 删除评论本身
        this.removeById(commentId);
    }

    @Override
    public CommentPageVO getCommentsPage(CommentSearchDTO dto) {
        Page<CommentVO> page = new Page<>(dto.getCurrent(), dto.getSize());
        // 修复方法调用，使用正确的方法名
        IPage<CommentVO> resultPage = baseMapper.selectCommentPage(page, dto);

        CommentPageVO pageVO = new CommentPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setRecords(resultPage.getRecords());

        return pageVO;
    }

    @Override
    public CommentTreeVO getCommentsTree(CommentSearchDTO dto) {
        // 查询顶级评论，使用正确的方法参数
        List<CommentVO> topLevelComments = baseMapper.selectTopLevelComments(
            dto.getTargetType(), dto.getTargetId()
        );

        // 为每个顶级评论加载回复（限制数量）
        for (CommentVO comment : topLevelComments) {
            List<CommentVO> replies = baseMapper.selectRepliesByParentId(comment.getId());
            // 限制回复数量为3条
            if (replies.size() > 3) {
                replies = replies.subList(0, 3);
            }
            comment.setReplies(replies);
        }

        CommentTreeVO treeVO = new CommentTreeVO();
        treeVO.setComments(topLevelComments);
        treeVO.setTotalCount((long) topLevelComments.size());
        treeVO.setTopLevelCount((long) topLevelComments.size());

        return treeVO;
    }

    @Override
    public CommentVO getCommentById(Long commentId, Long userId) {
        // 使用简单的查询方式，因为CommentMapper中没有selectCommentsWithUserInfoByIds方法
        CommentSearchDTO dto = new CommentSearchDTO();
        dto.setCurrent(1);
        dto.setSize(1);
        dto.setCurrentUserId(userId);
        
        // 直接通过ID查询
        Comment comment = this.getById(commentId);
        if (comment == null) {
            return null;
        }
        
        // 构建查询条件
        Page<CommentVO> page = new Page<>(1, 1);
        dto.setUserId(comment.getUserId());
        dto.setTargetType(comment.getTargetType());
        dto.setTargetId(comment.getTargetId());
        
        IPage<CommentVO> resultPage = baseMapper.selectCommentPage(page, dto);
        List<CommentVO> comments = resultPage.getRecords();
        
        // 从结果中找到对应ID的评论
        return comments.stream()
            .filter(c -> c.getId().equals(commentId))
            .findFirst()
            .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long userId, Long commentId) {
        // 检查评论是否存在
        Comment comment = this.getById(commentId);
        if (comment == null || !CommentStatus.NORMAL.getCode().equals(comment.getStatus())) {
            throw new IllegalArgumentException("评论不存在或已被删除");
        }

        // 检查用户是否已点赞
        boolean isLiked = commentLikeMapper.checkUserLike(userId, commentId) > 0;
        
        if (isLiked) {
            // 取消点赞
            commentLikeMapper.deleteUserLike(userId, commentId);
            // 减少点赞数
            baseMapper.updateLikeCount(commentId, -1);
            return false;
        } else {
            // 添加点赞
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(userId);
            commentLike.setCommentId(commentId);
            commentLike.setCreatedAt(LocalDateTime.now());
            commentLikeMapper.insert(commentLike);
            // 增加点赞数
            baseMapper.updateLikeCount(commentId, 1);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, Long commentId) {
        if (userId == null || commentId == null) {
            return false;
        }
        return commentLikeMapper.checkUserLike(userId, commentId) > 0;
    }

    @Override
    public CommentPageVO getUserComments(Long userId, Long current, Long size) {
        CommentSearchDTO dto = new CommentSearchDTO();
        dto.setCurrent(current);
        dto.setSize(size);
        dto.setUserId(userId);
        dto.setCurrentUserId(userId);
        
        return getCommentsPage(dto);
    }

    @Override
    public CommentPageVO getReplies(Long parentId, Long current, Long size, Long userId) {
        // 使用简单的方式获取回复
        List<CommentVO> allReplies = baseMapper.selectRepliesByParentId(parentId);
        
        // 手动分页
        int start = (int) ((current - 1) * size);
        int end = (int) Math.min(start + size, allReplies.size());
        
        List<CommentVO> replies = new ArrayList<>();
        if (start < allReplies.size()) {
            replies = allReplies.subList(start, end);
        }

        // 获取总数
        Long total = this.count(new LambdaQueryWrapper<Comment>()
            .eq(Comment::getParentId, parentId)
            .eq(Comment::getStatus, CommentStatus.NORMAL.getCode())
        );

        CommentPageVO pageVO = new CommentPageVO();
        pageVO.setCurrent(current);
        pageVO.setSize(size);
        pageVO.setTotal(total);
        pageVO.setRecords(replies);

        return pageVO;
    }
}