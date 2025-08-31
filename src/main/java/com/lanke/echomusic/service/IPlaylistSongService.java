package com.lanke.echomusic.service;

import com.lanke.echomusic.dto.playlist.AddSongToPlaylistDTO;
import com.lanke.echomusic.dto.playlist.PlaylistSongSearchDTO;
import com.lanke.echomusic.entity.PlaylistSong;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.vo.playlist.AddSongToPlaylistResult;
import com.lanke.echomusic.vo.playlist.PlaylistSongPageVO;

/**
 * <p>
 * 歌单-歌曲关联表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
public interface IPlaylistSongService extends IService<PlaylistSong> {
    
    /**
     * 添加歌曲到歌单
     * @param userId 用户ID
     * @param dto 添加歌曲请求参数
     * @return 添加结果，包含成功数量和重复信息
     */
    AddSongToPlaylistResult addSongsToPlaylist(Long userId, AddSongToPlaylistDTO dto);
    
    /**
     * 从歌单中移除歌曲
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @param songId 歌曲ID
     */
    void removeSongFromPlaylist(Long userId, Long playlistId, Long songId);
    
    PlaylistSongPageVO getPlaylistSongs(PlaylistSongSearchDTO dto);
    
    void updateSongSort(Long userId, Long playlistId, Long songId, Integer newSort);
}
