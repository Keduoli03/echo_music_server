package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.entity.UserLike;
import com.lanke.echomusic.vo.song.SongListVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户喜欢歌曲关联表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-01-20
 */
public interface UserLikeMapper extends BaseMapper<UserLike> {

    /**
     * 分页查询用户喜欢的歌曲列表
     */
    @Select("<script>" +
            "SELECT " +
            "   s.id, s.name AS songName, s.album_id, s.duration, s.genre, s.language, " +
            "   s.play_count, s.like_count, s.release_date, s.cover_url, s.play_url, s.status, " +
            "   a.name AS albumName, " +
            "   GROUP_CONCAT(DISTINCT sr.name ORDER BY ss.sort ASC SEPARATOR '/') AS singerName " +
            "FROM l_user_like ul " +
            "INNER JOIN l_song s ON ul.song_id = s.id " +
            "LEFT JOIN l_album a ON s.album_id = a.id " +
            "LEFT JOIN l_song_singer ss ON s.id = ss.song_id " +
            "LEFT JOIN l_singer sr ON ss.singer_id = sr.id " +
            "WHERE ul.user_id = #{userId} " +
            "AND s.status = 1 " +
            "GROUP BY s.id, s.name, s.album_id, s.duration, s.genre, s.language, " +
            "         s.play_count, s.like_count, s.release_date, s.cover_url, s.play_url, s.status, a.name " +
            "ORDER BY ul.created_at DESC" +
            "</script>")
    IPage<SongListVO> selectUserLikedSongs(Page<SongListVO> page, @Param("userId") Long userId);

    /**
     * 查询用户喜欢的歌曲ID列表
     */
    @Select("SELECT song_id FROM l_user_like WHERE user_id = #{userId}")
    List<Long> selectLikedSongIds(@Param("userId") Long userId);
}