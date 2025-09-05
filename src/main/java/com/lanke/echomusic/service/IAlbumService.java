package com.lanke.echomusic.service;
import com.lanke.echomusic.dto.album.AlbumInfoDTO;
import com.lanke.echomusic.dto.album.AlbumSearchDTO;
import com.lanke.echomusic.entity.Album;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.vo.album.AlbumPageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 专辑表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
public interface IAlbumService extends IService<Album> {

    Long createAlbum(AlbumInfoDTO dto);

    List<Album> getAlbumsBySingerId(Long singerId);
    
    List<Album> getAllAlbums();
    
    // 保留原有方法（兼容性）
    List<AlbumInfoDTO> getAllAlbumsWithSingerName();
    
    // 新增：分页查询专辑列表
    AlbumPageVO getAlbumsPage(AlbumSearchDTO searchDTO);

    boolean updateAlbum(AlbumInfoDTO dto);

    boolean deleteAlbum(Long id);

    String updateAlbumCover(Long albumId, MultipartFile file);
    
    Album getAlbumByName(String albumName);
    
    int countSongsByAlbumId(Long albumId);
    
    Long createAlbumByName(String albumName, Long singerId);
}
