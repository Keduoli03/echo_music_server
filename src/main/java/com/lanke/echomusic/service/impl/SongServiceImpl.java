package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.song.SongSearchDTO;
import com.lanke.echomusic.utils.AudioUtils;
import com.lanke.echomusic.dto.album.AlbumInfoDTO;
import com.lanke.echomusic.dto.singer.SingerInfoDTO;
import com.lanke.echomusic.dto.song.SongDetailDTO;
import com.lanke.echomusic.dto.song.SongInfoDTO;
import com.lanke.echomusic.dto.song.UpdateSongDTO;
import com.lanke.echomusic.entity.Album;
import com.lanke.echomusic.entity.MusicType;
import com.lanke.echomusic.entity.Singer;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.entity.SongSinger;
import com.lanke.echomusic.mapper.SongMapper;
import com.lanke.echomusic.mapper.SongSingerMapper;
import com.lanke.echomusic.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lanke.echomusic.vo.song.SongListVO;
import com.lanke.echomusic.vo.song.SongPageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements ISongService {

    @Autowired private ISingerService singerService;
    @Autowired private IAlbumService albumService;
    @Autowired private SongMapper songMapper;
    @Autowired private ISongSingerService songSingerService; // 使用Service实现批量插入
    @Autowired private MinioService minioService;
    @Autowired
    private SongSingerMapper songSingerMapper;
    @Autowired 
    private IMusicTypeService musicTypeService;

    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);
    @Override
    @Transactional
    public Long createSong(SongInfoDTO dto) {
        // ====================== 1. 解析并自动创建歌手 ======================
        List<Long> singerIds = processSingers(dto.getSingerName()); // 改为 getSingerName()
    
        // ====================== 2. 自动创建专辑（若无有效专辑ID） ======================
        Long albumId = processAlbum(dto, singerIds);
    
        // ====================== 3. 创建歌曲实体 ======================
        Song song = buildSongEntity(dto, albumId);
        songMapper.insert(song);
    
        // ====================== 4. 批量绑定歌手关联（默认主唱类型）======================
        bindSongSingers(song.getId(), singerIds);
    
        return song.getId();
    }

    /**
     * 解析歌手名称并自动创建歌手
     */
    /**
     * 解析歌手名称并自动创建歌手
     */
    private List<Long> processSingers(String artistNames) {
        // 支持多种分隔符：斜杠(/)、逗号(,)、分号(;)
        List<String> nameList = List.of(artistNames.split("[/,;]"));
        return nameList.stream()
                .map(String::trim)
                .filter(name -> !name.isEmpty()) // 过滤空字符串
                .distinct()
                .map(name -> {
                    // 查询歌手是否存在
                    Singer singer = singerService.getSingerByName(name);
                    if (singer != null) return singer.getId();
    
                    // 创建歌手时传递完整的SingerInfoDTO
                    SingerInfoDTO singerDTO = new SingerInfoDTO();
                    singerDTO.setName(name);
                    // 可选：从歌曲DTO中获取默认头像（如果有）
                    // singerDTO.setAvatar(dto.getCoverUrl());
                    // 或设置默认值
                    singerDTO.setGender(0); // 未知性别
                    singerDTO.setStatus(1); // 默认启用状态
    
                    return singerService.createSinger(singerDTO);
                })
                .collect(Collectors.toList());
    }

    /**
     * 自动创建专辑（若无专辑ID或名称，则使用歌曲名作为专辑名）
     */
    private Long processAlbum(SongInfoDTO dto, List<Long> singerIds) {
        if (dto.getAlbumId() != null && dto.getAlbumId() > 0) {
            // 校验已有专辑是否存在
            Album album = albumService.getById(dto.getAlbumId());
            if (album == null) {
                throw new IllegalArgumentException("专辑ID不存在");
            }
            return dto.getAlbumId();
        }
    
        // 只有明确传入专辑名称时才创建专辑
        if (!StringUtils.hasText(dto.getAlbumName())) {
            // 没有专辑信息，返回null，表示歌曲不属于任何专辑
            return null;
        }
    
        // 自动创建专辑（只有当明确提供专辑名时）
        AlbumInfoDTO albumDTO = new AlbumInfoDTO();
        albumDTO.setAlbumName(dto.getAlbumName());
        
        // 修正：确保设置歌手名称，即使singerIds为空也要处理
        if (!singerIds.isEmpty()) {
            Singer singer = singerService.getById(singerIds.get(0));
            if (singer != null) {
                albumDTO.setSingerName(singer.getName());
            }
        } else {
            // 如果没有歌手ID，但有歌手名称，直接使用
            if (StringUtils.hasText(dto.getSingerName())) {
                albumDTO.setSingerName(dto.getSingerName());
            } else {
                // 如果完全没有歌手信息，抛出异常或设置默认值
                throw new IllegalArgumentException("创建专辑时必须提供歌手信息");
            }
        }
        
        albumDTO.setCoverUrl(dto.getCoverUrl());
        albumDTO.setReleaseDate(dto.getReleaseDate());
        albumDTO.setType(1);
        albumDTO.setStatus(1);
    
        return albumService.createAlbum(albumDTO);
    }

    /**
     * 构建歌曲实体
     */
    private Song buildSongEntity(SongInfoDTO dto, Long albumId) {
        Song song = new Song();
        song.setName(dto.getSongName());
        song.setOriginalName(dto.getOriginalName());
        song.setAlbumId(albumId);
        song.setReleaseDate(dto.getReleaseDate());
        song.setGenre(dto.getGenre());
        song.setMusicType(dto.getMusicType());  // 新增
        song.setLanguage(dto.getLanguage());
        song.setLyricist(dto.getLyricist());
        song.setComposer(dto.getComposer());
        song.setArranger(dto.getArranger());
        song.setLyrics(dto.getLyrics());
        song.setPlayUrl(dto.getPlayUrl());
        song.setCoverUrl(dto.getCoverUrl());
        song.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        song.setDuration(dto.getDuration());
        song.setPlayCount(0L);
        song.setLikeCount(0L);
        song.setCreatedAt(LocalDateTime.now());
        song.setUpdatedAt(LocalDateTime.now());
        return song;
    }

    @Override
    public SongDetailDTO getSongDetail(Long songId) {
        Song song = getById(songId);
        if (song == null) {
            throw new IllegalArgumentException("歌曲不存在");
        }

        SongDetailDTO detail = new SongDetailDTO();
        BeanUtils.copyProperties(song, detail);
        
        // 手动设置歌曲名称（字段名不匹配）
        detail.setSongName(song.getName());

        // 处理音乐类型名称
        if (song.getMusicType() != null) {
            MusicType musicType = musicTypeService.getById(song.getMusicType());
            if (musicType != null) {
                detail.setMusicTypeName(musicType.getName());
            }
        }

        // 处理专辑名称
        if (song.getAlbumId() != null) {
            Album album = albumService.getById(song.getAlbumId());
            if (album != null) {
                detail.setAlbumName(album.getName());
            }
        }

        // 处理歌手名称（生成用/分割的字符串）
        List<SingerInfoDTO> singers = getSingersBySongId(songId);
        if (singers != null && !singers.isEmpty()) {
            String singerName = singers.stream()
                    .map(SingerInfoDTO::getName)
                    .collect(Collectors.joining("/"));
            detail.setSingerName(singerName);
        }

        return detail;
    }

    private List<SingerInfoDTO> getSingersBySongId(Long songId) {
        // 查询歌曲关联的所有歌手ID
        List<SongSinger> songSingers = songSingerService.list(
                new LambdaQueryWrapper<SongSinger>().eq(SongSinger::getSongId, songId)
        );

        // 提取歌手ID列表
        List<Long> singerIds = songSingers.stream()
                .map(SongSinger::getSingerId)
                .collect(Collectors.toList());

        // 通过singerService调用listSingersByIds方法
        return singerService.listSingersByIds(singerIds);
    }

    @Override
    @Transactional
    public void updateSongPlayUrl(Long songId, MultipartFile file) {
        Song song = songMapper.selectById(songId);
        if (song == null) {
            throw new RuntimeException("歌曲不存在");
        }

        // 1. 解析时长
        Integer duration = AudioUtils.parseAudioDuration(file);
        log.info("解析到音频时长: {}秒 [{}]", duration, file.getOriginalFilename());

        // 2. 上传文件到MinIO
        String newPlayUrl = minioService.uploadFile(file, "songs");

        // 3. 保存旧URL用于清理
        String oldPlayUrl = song.getPlayUrl();

        // 4. 更新歌曲信息
        song.setPlayUrl(newPlayUrl);
        song.setDuration(duration);
        song.setUpdatedAt(LocalDateTime.now());

        if (songMapper.updateById(song) != 1) {
            throw new RuntimeException("歌曲文件更新失败");
        }

        // 5. 清理旧文件
        if (oldPlayUrl != null && !oldPlayUrl.isEmpty()) {
            try {
                minioService.deleteFile(oldPlayUrl);
                log.info("已删除旧文件: {}", oldPlayUrl);
            } catch (Exception e) {
                log.error("删除旧文件失败: {} - {}", oldPlayUrl, e.getMessage());
            }
        }
    }



    @Override
    @Transactional // 开启事务
    public boolean deleteSong(Long songId) {
        // 1. 查询歌曲信息（获取文件URL）
        Song song = getById(songId);
        if (song == null) {
            throw new RuntimeException("歌曲不存在");
        }

        // 2. 删除关联的歌手关联记录
        deleteSongSingers(songId);

        // 3. 删除MinIO中的文件（播放地址和封面）
        deleteMinioFiles(song);

        // 4. 删除数据库中的歌曲记录
        boolean result = removeById(songId);
        if (!result) {
            throw new RuntimeException("删除歌曲记录失败");
        }

        return true;
    }

    /**
     * 删除歌曲-歌手关联记录
     */
    private void deleteSongSingers(Long songId) {
        int deleted = songSingerMapper.delete(
                new LambdaQueryWrapper<SongSinger>()
                        .eq(SongSinger::getSongId, songId)
        );
        if (deleted < 0) {
            log.warn("删除歌手关联记录失败，歌曲ID：{}", songId);
        }
    }

    /**
     * 删除MinIO中的音频文件和封面
     */
    private void deleteMinioFiles(Song song) {
        // 删除播放文件
        deleteFileIfExists(song.getPlayUrl());
        // 删除封面文件（如果有）
        deleteFileIfExists(song.getCoverUrl());
    }

    /**
     * 通用文件删除方法（避免空指针）
     */
    private void deleteFileIfExists(String fileUrl) {
        if (StringUtils.hasText(fileUrl)) {
            try {
                minioService.deleteFile(fileUrl);
                log.info("删除文件成功：{}", fileUrl);
            } catch (Exception e) {
                log.error("删除文件失败：{}", fileUrl, e);
            }
        }
    }

    @Override
    @Transactional // 确保事务性
    public String updateSongCover(Long songId, MultipartFile coverFile) {
        // 1. 查询歌曲是否存在
        Song song = getById(songId);
        if (song == null) {
            throw new RuntimeException("歌曲不存在");
        }

        // 2. 上传封面到MinIO（存储到covers目录）
        String coverUrl = minioService.uploadFile(
                coverFile,"covers");

        // 3. 更新数据库中的封面URL
        song.setCoverUrl(coverUrl);
        updateById(song);

        return coverUrl;
    }


    //歌曲查找
    @Override
    public SongPageVO searchSongs(SongSearchDTO dto) {
        Page<SongListVO> page = new Page<>(dto.getCurrent(), dto.getSize());
        IPage<SongListVO> resultPage = songMapper.searchSongs(page, dto);

        SongPageVO pageVO = new SongPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setRecords(resultPage.getRecords()); // 直接使用查询结果，无需额外处理歌手

        return pageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSongInfo(UpdateSongDTO dto) {
        // 1. 验证歌曲是否存在
        Song existingSong = getById(dto.getId());
        if (existingSong == null) {
            throw new IllegalArgumentException("歌曲不存在");
        }
    
        // 2. 处理专辑更新逻辑
        Long newAlbumId = processAlbumUpdate(existingSong, dto.getAlbumName());
        
        // 3. 复制非空属性到现有歌曲实体
        BeanUtils.copyProperties(dto, existingSong, getNullPropertyNames(dto));
        
        // 4. 设置类型、专辑等
        if (dto.getSongName() != null) {
            existingSong.setName(dto.getSongName());
        }
        if (newAlbumId != null) {
            existingSong.setAlbumId(newAlbumId);
        }
        if (dto.getMusicType() != null) {
            existingSong.setMusicType(dto.getMusicType());
        }
    
        // 5. 更新歌曲基本信息
        boolean updateResult = updateById(existingSong);
        if (!updateResult) {
            throw new RuntimeException("歌曲信息更新失败");
        }
    
        // 6. 处理歌手信息更新
        if (dto.getSingerName() != null && !dto.getSingerName().trim().isEmpty()) {
            deleteSongSingers(dto.getId());
            List<Long> singerIds = processSingers(dto.getSingerName());
            bindSongSingers(dto.getId(), singerIds);
        }
    
        return true;
    }

    /**
     * 处理专辑更新逻辑
     */
    private Long processAlbumUpdate(Song existingSong, String newAlbumName) {
        Long oldAlbumId = existingSong.getAlbumId();
        
        // 如果没有提供新的专辑名称，保持原有专辑
        if (!StringUtils.hasText(newAlbumName)) {
            return oldAlbumId;
        }
        
        // 查找新专辑是否存在
        Album newAlbum = albumService.getAlbumByName(newAlbumName);
        Long newAlbumId;
        
        if (newAlbum != null) {
            // 专辑已存在，直接使用
            newAlbumId = newAlbum.getId();
        } else {
            // 专辑不存在，创建新专辑
            // 获取歌曲的第一个歌手作为专辑歌手
            List<Long> singerIds = getSingerIdsBySongId(existingSong.getId());
            Long singerId = singerIds.isEmpty() ? null : singerIds.get(0);
            newAlbumId = albumService.createAlbumByName(newAlbumName, singerId);
        }
        
        // 处理旧专辑的删除逻辑
        if (oldAlbumId != null && !oldAlbumId.equals(newAlbumId)) {
            handleOldAlbumDeletion(oldAlbumId);
        }
        
        return newAlbumId;
    }

    /**
     * 处理旧专辑的删除逻辑
     */
    private void handleOldAlbumDeletion(Long oldAlbumId) {
        // 检查旧专辑是否还有其他歌曲使用
        int songCount = albumService.countSongsByAlbumId(oldAlbumId);
        
        // 如果只有当前这一首歌使用该专辑，则删除专辑
        if (songCount <= 1) {
            try {
                albumService.deleteAlbum(oldAlbumId);
                log.info("删除未使用的专辑，ID: {}", oldAlbumId);
            } catch (Exception e) {
                log.warn("删除专辑失败，ID: {}", oldAlbumId, e);
            }
        }
    }

    /**
     * 获取歌曲关联的歌手ID列表
     */
    private List<Long> getSingerIdsBySongId(Long songId) {
        List<SongSinger> songSingers = songSingerService.list(
            new LambdaQueryWrapper<SongSinger>()
                .eq(SongSinger::getSongId, songId)
                .orderByAsc(SongSinger::getSort)
        );
        return songSingers.stream()
                .map(SongSinger::getSingerId)
                .collect(Collectors.toList());
    }

    /**
     * 获取对象中为null的属性名称数组
     */
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

     /**
     * 批量绑定歌曲和歌手的关联关系
     */
    private void bindSongSingers(Long songId, List<Long> singerIds) {
        if (songId == null || CollectionUtils.isEmpty(singerIds)) {
            return;
        }
        
        // 创建歌曲-歌手关联记录
        List<SongSinger> songSingers = singerIds.stream()
                .map(singerId -> {
                    SongSinger songSinger = new SongSinger();
                    songSinger.setSongId(songId);
                    songSinger.setSingerId(singerId);
                    songSinger.setSort(0); // 默认排序
                    return songSinger;
                })
                .collect(Collectors.toList());
        
        // 批量插入关联记录
        songSingerService.saveBatch(songSingers);
        
        log.info("成功绑定歌曲ID: {} 与 {} 个歌手的关联关系", songId, singerIds.size());
    }

}

   


