package com.lanke.echomusic.service;
import com.lanke.echomusic.dto.song.SongDetailDTO;
import com.lanke.echomusic.dto.song.SongInfoDTO;
import com.lanke.echomusic.dto.song.SongSearchDTO;
import com.lanke.echomusic.dto.song.UpdateSongDTO;
import com.lanke.echomusic.entity.Song;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.vo.song.SongPageVO;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 歌曲表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
public interface ISongService extends IService<Song> {

    Long createSong(@Valid SongInfoDTO songInfoDTO);

    SongDetailDTO getSongDetail(Long id);

    void updateSongPlayUrl(Long songId, MultipartFile file);

    boolean deleteSong(Long songId);

    String updateSongCover(Long songId, MultipartFile file);

    SongPageVO searchSongs(SongSearchDTO dto);
    
    // 新增更新歌曲信息方法
    boolean updateSongInfo(UpdateSongDTO dto);
}
