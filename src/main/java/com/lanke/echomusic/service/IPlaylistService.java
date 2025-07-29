package com.lanke.echomusic.service;

import com.lanke.echomusic.dto.playlist.PlaylistUpdateDTO;
import com.lanke.echomusic.entity.Playlist;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 歌单表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
public interface IPlaylistService extends IService<Playlist> {

    Long createPlaylist(Playlist playlist);

    void updatePlaylist(Long userId, PlaylistUpdateDTO dto);

    void deletePlaylist(Long userId, @NotNull(message = "歌单ID不能为空") Long id);

    String updatePlaylistCover(Long userId, Long playlistId, MultipartFile file);
}
