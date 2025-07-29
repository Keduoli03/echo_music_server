package com.lanke.echomusic.service.impl;

import com.lanke.echomusic.entity.PlaylistSong;
import com.lanke.echomusic.mapper.PlaylistSongMapper;
import com.lanke.echomusic.service.IPlaylistSongService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌单-歌曲关联表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong> implements IPlaylistSongService {

}
