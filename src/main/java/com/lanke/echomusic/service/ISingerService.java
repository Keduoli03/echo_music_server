package com.lanke.echomusic.service;

import com.lanke.echomusic.dto.singer.SingerInfoDTO;
import com.lanke.echomusic.dto.singer.SingerSearchDTO;
import com.lanke.echomusic.entity.Singer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.vo.singer.SingerPageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 歌手表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-06-02
 */
public interface ISingerService extends IService<Singer> {

    
    Singer getSingerByName(String name);

    List<SingerInfoDTO> listSingersByIds(List<Long> singerIds);

    Long createSinger(SingerInfoDTO dto);

    boolean updateSinger(SingerInfoDTO dto);

    boolean deleteSinger(Long id);

    String updateSingerAvatar(Long singerId, MultipartFile file);
    
    /**
     * 分页查询歌手列表
     * @param searchDTO 查询参数
     * @return 分页结果
     */
    SingerPageVO getSingersPage(SingerSearchDTO searchDTO);

    /**
     * 获取所有国籍选项
     * @return 国籍列表
     */
    List<String> getAllNationalities();

    /**
     * 获取歌手类型选项
     * @return 歌手类型映射
     */
    Map<Integer, String> getSingerTypes();
    
    /**
     * 根据ID获取歌手详细信息
     * @param id 歌手ID
     * @return 歌手信息DTO
     */
    SingerInfoDTO getSingerById(Long id);
}
