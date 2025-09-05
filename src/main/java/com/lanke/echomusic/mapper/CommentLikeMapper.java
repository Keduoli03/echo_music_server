package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lanke.echomusic.entity.CommentLike;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 评论点赞表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-01-22
 */
public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    /**
     * 检查用户是否已点赞评论
     */
    @Select("SELECT COUNT(1) FROM l_comment_like WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int checkUserLike(@Param("userId") Long userId, @Param("commentId") Long commentId);

    /**
     * 删除用户对评论的点赞
     */
    @Delete("DELETE FROM l_comment_like WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int deleteUserLike(@Param("userId") Long userId, @Param("commentId") Long commentId);
}