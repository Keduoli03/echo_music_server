package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.playlist.PlaylistSearchDTO;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.vo.playlist.PlaylistListVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 歌单表 Mapper 接口
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
public interface PlaylistMapper extends BaseMapper<Playlist> {
    
    /**
     * 分页查询歌单列表
     */
    IPage<PlaylistListVO> searchPlaylists(Page<PlaylistListVO> page, @Param("dto") PlaylistSearchDTO dto);
}
