package com.lanke.echomusic.service;

import com.lanke.echomusic.dto.playlist.PlaylistSearchDTO;
import com.lanke.echomusic.dto.playlist.PlaylistUpdateDTO;
import com.lanke.echomusic.entity.Playlist;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.vo.playlist.PlaylistPageVO;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    
    /**
     * 分页查询歌单列表
     */
    PlaylistPageVO searchPlaylists(PlaylistSearchDTO dto);
    
    /**
     * 获取所有音乐类型
     */
    List<Map<String, Object>> getAllMusicTypes();
    
    /**
     * 获取所有歌单分类
     */
    List<Map<String, Object>> getAllPlaylistCategories();
}
