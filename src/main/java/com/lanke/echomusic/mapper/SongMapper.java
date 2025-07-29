package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.dto.song.SongSearchDTO;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.vo.singer.SingerSimpleVO;
import com.lanke.echomusic.vo.song.SongListVO;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 歌曲表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
public interface SongMapper extends BaseMapper<Song> {
    /**
     * 分页查询歌曲列表（拼接歌手名称为字符串）
     */
    @Select("<script>" +
            "SELECT " +
            "   s.id, s.name AS songName, s.album_id, s.duration, s.genre, s.language, " +
            "   s.play_count, s.like_count, s.release_date, s.cover_url, s.play_url, s.status, " +
            "   a.name AS albumName, " +
            "   GROUP_CONCAT(DISTINCT sr.name ORDER BY ss.sort ASC SEPARATOR '/') AS singerName " + // 关键：列别名设为singer
            "FROM l_song s " +
            "LEFT JOIN l_album a ON s.album_id = a.id " +
            "LEFT JOIN l_song_singer ss ON s.id = ss.song_id " +
            "LEFT JOIN l_singer sr ON ss.singer_id = sr.id " +
            "<where>" +
            "   <if test='dto.songName != null and dto.songName != \"\"'>" +
            "       AND s.name LIKE CONCAT('%', #{dto.songName}, '%')" +
            "   </if>" +
            "   <if test='dto.singerName != null and dto.singerName != \"\"'>" +
            "       AND EXISTS (" +
            "           SELECT 1 FROM l_song_singer ss2 " +
            "           JOIN l_singer sr2 ON ss2.singer_id = sr2.id " +
            "           WHERE ss2.song_id = s.id " +
            "           AND sr2.name LIKE CONCAT('%', #{dto.singerName}, '%')" +
            "       )" +
            "   </if>" +
            "   <if test='dto.albumName != null and dto.albumName != \"\"'>" +
            "       AND a.name LIKE CONCAT('%', #{dto.albumName}, '%')" +
            "   </if>" +
            "</where>" +
            "GROUP BY s.id " + // 按歌曲ID分组
            "<if test='dto.orderBy != null and dto.orderBy != \"\"'>" +
            "   ORDER BY ${dto.orderBy}" +
            "</if>" +
            "<if test='dto.orderBy == null or dto.orderBy == \"\"'>" +
            "   ORDER BY s.play_count DESC, s.like_count DESC" +
            "</if>" +
            "</script>")
    IPage<SongListVO> searchSongs(IPage<SongListVO> page, @Param("dto") SongSearchDTO dto);
}