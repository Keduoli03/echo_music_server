package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.comment.CommentSearchDTO;
import com.lanke.echomusic.entity.Comment;
import com.lanke.echomusic.vo.comment.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 评论表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-01-22
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表（包含用户信息和目标对象名称）
     */
    @Select("<script>" +
            "SELECT " +
            "   c.id, c.user_id, c.target_type, c.target_id, c.parent_id, " +
            "   c.reply_to_user_id, c.content, c.like_count, c.reply_count, " +
            "   c.status, c.created_at, c.updated_at, " +
            "   u.username, u.nickname, u.avatar_url, " +
            "   ru.username AS reply_to_username, ru.nickname AS reply_to_nickname, " +
            "   CASE " +
            "       WHEN c.target_type = 'playlist' THEN p.name " +
            "       WHEN c.target_type = 'song' THEN s.name " +
            "       WHEN c.target_type = 'album' THEN a.name " +
            "       ELSE NULL " +
            "   END AS target_name " +
            "FROM l_comment c " +
            "LEFT JOIN l_user u ON c.user_id = u.id " +
            "LEFT JOIN l_user ru ON c.reply_to_user_id = ru.id " +
            "LEFT JOIN l_playlist p ON c.target_type = 'playlist' AND c.target_id = p.id " +
            "LEFT JOIN l_song s ON c.target_type = 'song' AND c.target_id = s.id " +
            "LEFT JOIN l_album a ON c.target_type = 'album' AND c.target_id = a.id " +
            "<where>" +
            "<if test='dto.targetType != null and dto.targetType != \"\"'>" +
            "AND c.target_type = #{dto.targetType} " +
            "</if>" +
            "<if test='dto.targetId != null'>" +
            "AND c.target_id = #{dto.targetId} " +
            "</if>" +
            "<if test='dto.parentId != null'>" +
            "AND c.parent_id = #{dto.parentId} " +
            "</if>" +
            "<if test='dto.userId != null'>" +
            "AND c.user_id = #{dto.userId} " +
            "</if>" +
            "<if test='dto.status != null'>" +
            "AND c.status = #{dto.status} " +
            "</if>" +
            "</where>" +
            "ORDER BY c.created_at DESC" +
            "</script>")
    IPage<CommentVO> selectCommentPage(Page<CommentVO> page, @Param("dto") CommentSearchDTO dto);

    /**
     * 查询顶级评论列表（不分页，包含目标对象名称）
     */
    @Select("<script>" +
            "SELECT " +
            "   c.id, c.user_id, c.target_type, c.target_id, c.parent_id, " +
            "   c.reply_to_user_id, c.content, c.like_count, c.reply_count, " +
            "   c.status, c.created_at, c.updated_at, " +
            "   u.username, u.nickname, u.avatar_url, " +
            "   CASE " +
            "       WHEN c.target_type = 'playlist' THEN p.name " +
            "       WHEN c.target_type = 'song' THEN s.name " +
            "       WHEN c.target_type = 'album' THEN a.name " +
            "       ELSE NULL " +
            "   END AS target_name " +
            "FROM l_comment c " +
            "LEFT JOIN l_user u ON c.user_id = u.id " +
            "LEFT JOIN l_playlist p ON c.target_type = 'playlist' AND c.target_id = p.id " +
            "LEFT JOIN l_song s ON c.target_type = 'song' AND c.target_id = s.id " +
            "LEFT JOIN l_album a ON c.target_type = 'album' AND c.target_id = a.id " +
            "WHERE c.target_type = #{targetType} " +
            "AND c.target_id = #{targetId} " +
            "AND c.parent_id IS NULL " +
            "AND c.status = 1 " +
            "ORDER BY c.created_at DESC" +
            "</script>")
    List<CommentVO> selectTopLevelComments(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    /**
     * 查询回复列表（包含目标对象名称）
     */
    @Select("<script>" +
            "SELECT " +
            "   c.id, c.user_id, c.target_type, c.target_id, c.parent_id, " +
            "   c.reply_to_user_id, c.content, c.like_count, c.reply_count, " +
            "   c.status, c.created_at, c.updated_at, " +
            "   u.username, u.nickname, u.avatar_url, " +
            "   ru.username AS reply_to_username, ru.nickname AS reply_to_nickname, " +
            "   CASE " +
            "       WHEN c.target_type = 'playlist' THEN p.name " +
            "       WHEN c.target_type = 'song' THEN s.name " +
            "       WHEN c.target_type = 'album' THEN a.name " +
            "       ELSE NULL " +
            "   END AS target_name " +
            "FROM l_comment c " +
            "LEFT JOIN l_user u ON c.user_id = u.id " +
            "LEFT JOIN l_user ru ON c.reply_to_user_id = ru.id " +
            "LEFT JOIN l_playlist p ON c.target_type = 'playlist' AND c.target_id = p.id " +
            "LEFT JOIN l_song s ON c.target_type = 'song' AND c.target_id = s.id " +
            "LEFT JOIN l_album a ON c.target_type = 'album' AND c.target_id = a.id " +
            "WHERE c.parent_id = #{parentId} " +
            "AND c.status = 1 " +
            "ORDER BY c.created_at ASC" +
            "</script>")
    List<CommentVO> selectRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 更新回复数量
     */
    @Update("UPDATE l_comment SET reply_count = reply_count + #{increment} WHERE id = #{commentId}")
    int updateReplyCount(@Param("commentId") Long commentId, @Param("increment") int increment);

    /**
     * 更新评论点赞数
     */
    @Update("UPDATE l_comment SET like_count = like_count + #{increment}, updated_at = NOW() WHERE id = #{commentId}")
    int updateLikeCount(@Param("commentId") Long commentId, @Param("increment") Integer increment);
}