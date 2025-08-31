package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.playlist.PlaylistSongSearchDTO;
import com.lanke.echomusic.entity.PlaylistSong;
import com.lanke.echomusic.vo.playlist.PlaylistSongListVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 歌单-歌曲关联表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
public interface PlaylistSongMapper extends BaseMapper<PlaylistSong> {

    /**
     * 分页查询歌单中的歌曲
     * @param page 分页参数
     * @param dto 查询条件
     * @return 歌曲列表
     */
    IPage<PlaylistSongListVO> searchPlaylistSongs(@Param("page") Page<PlaylistSongListVO> page, @Param("dto") PlaylistSongSearchDTO dto);

    /**
     * 获取歌单中歌曲的最大排序值
     * @param playlistId 歌单ID
     * @return 最大排序值
     */
    Integer selectMaxSortByPlaylistId(@Param("playlistId") Long playlistId);

}
