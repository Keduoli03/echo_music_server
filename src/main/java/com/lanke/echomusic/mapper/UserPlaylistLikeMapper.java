package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.entity.UserPlaylistLike;
import com.lanke.echomusic.vo.playlist.PlaylistListVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户收藏歌单关联表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
public interface UserPlaylistLikeMapper extends BaseMapper<UserPlaylistLike> {

    /**
     * 分页查询用户收藏的歌单列表
     */
    @Select("<script>" +
            "SELECT " +
            "   p.id, p.name, p.description, p.cover_url as coverUrl, " +
            "   p.user_id as userId, u.username as creatorName, " +
            "   p.song_count as songCount, p.play_count as playCount, " +
            "   p.collect_count as collectCount, p.is_public as isPublic, " +
            "   p.music_type as musicType, " +
            "   CASE p.music_type " +
            "       WHEN 1 THEN '华语流行' " +
            "       WHEN 2 THEN '欧美流行' " +
            "       WHEN 3 THEN '日韩流行' " +
            "       WHEN 4 THEN '古典音乐' " +
            "       WHEN 5 THEN '民谣' " +
            "       WHEN 6 THEN '摇滚' " +
            "       WHEN 7 THEN '电子音乐' " +
            "       WHEN 8 THEN '说唱' " +
            "       WHEN 9 THEN '爵士' " +
            "       WHEN 10 THEN '其他' " +
            "       ELSE '未分类' " +
            "   END as musicTypeName, " +
            "   p.playlist_category as playlistCategory, " +
            "   CASE p.playlist_category " +
            "       WHEN 1 THEN '个人歌单' " +
            "       WHEN 2 THEN '精选歌单' " +
            "       WHEN 3 THEN '推荐歌单' " +
            "       WHEN 4 THEN '官方歌单' " +
            "       WHEN 5 THEN '主题歌单' " +
            "       ELSE '未分类' " +
            "   END as playlistCategoryName, " +
            "   p.create_time as createTime, p.update_time as updateTime " +
            "FROM l_user_playlist_like upl " +
            "INNER JOIN l_playlist p ON upl.playlist_id = p.id " +
            "LEFT JOIN l_user u ON p.user_id = u.id " +
            "WHERE upl.user_id = #{userId} " +
            "ORDER BY upl.created_at DESC" +
            "</script>")
    IPage<PlaylistListVO> selectUserLikedPlaylists(Page<PlaylistListVO> page, @Param("userId") Long userId);

    /**
     * 查询用户收藏的歌单ID列表
     */
    @Select("SELECT playlist_id FROM l_user_playlist_like WHERE user_id = #{userId}")
    List<Long> selectLikedPlaylistIds(@Param("userId") Long userId);
}