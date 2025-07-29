package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.album.AlbumSearchDTO;
import com.lanke.echomusic.entity.Album;
import com.lanke.echomusic.vo.album.AlbumWithSongCountVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 专辑表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
public interface AlbumMapper extends BaseMapper<Album> {

    /**
     * 分页查询专辑列表（包含歌曲数量和歌手名称）
     */
    @Select("SELECT a.id, a.name as album_name, a.singer_id, a.release_date, " +
            "       a.type, a.status, a.description, a.cover_url, " +
            "       s.name as singer_name, " +
            "       COALESCE(song_counts.song_count, 0) as song_count " +
            "FROM l_album a " +
            "LEFT JOIN l_singer s ON a.singer_id = s.id " +
            "LEFT JOIN (" +
            "    SELECT album_id, COUNT(*) as song_count " +
            "    FROM l_song " +
            "    WHERE album_id IS NOT NULL " +
            "    GROUP BY album_id" +
            ") song_counts ON a.id = song_counts.album_id " +
            "ORDER BY a.release_date DESC")
    IPage<AlbumWithSongCountVO> selectAlbumsWithSongCount(Page<AlbumWithSongCountVO> page, @Param("searchDTO") AlbumSearchDTO searchDTO);

    /**
     * 查询空专辑ID列表（用于批量删除）
     */
    @Select("SELECT a.id " +
            "FROM l_album a " +
            "LEFT JOIN (" +
            "    SELECT album_id, COUNT(*) as song_count " +
            "    FROM l_song " +
            "    WHERE album_id IS NOT NULL " +
            "    GROUP BY album_id" +
            ") song_counts ON a.id = song_counts.album_id " +
            "WHERE COALESCE(song_counts.song_count, 0) = 0")
    List<Long> selectEmptyAlbumIds();
}
