package com.lanke.echomusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.song.SongSearchDTO;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.vo.singer.SingerSimpleVO;
import com.lanke.echomusic.vo.song.SongListVO;
import org.apache.ibatis.annotations.Param;

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
    IPage<SongListVO> searchSongs(Page<SongListVO> page, @Param("dto") SongSearchDTO dto);
}