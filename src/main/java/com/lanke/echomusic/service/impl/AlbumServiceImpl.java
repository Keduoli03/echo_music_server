package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.dto.album.AlbumInfoDTO;
import com.lanke.echomusic.dto.album.AlbumSearchDTO;
import com.lanke.echomusic.entity.Album;
import com.lanke.echomusic.entity.Singer;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.mapper.AlbumMapper;
import com.lanke.echomusic.mapper.SongMapper;
import com.lanke.echomusic.service.IAlbumService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.service.ISingerService;
import com.lanke.echomusic.service.MinioService;
import com.lanke.echomusic.vo.album.AlbumPageVO;
import com.lanke.echomusic.vo.album.AlbumWithSongCountVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>专辑表 服务实现类</p>
 *
 * @author lanke
 * @since 2025-06-02
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements IAlbumService {
    private final SongMapper songMapper;
    private final ISingerService singerService;
    @Autowired
    private MinioService minioService;
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);

    public AlbumServiceImpl(SongMapper songMapper, ISingerService singerService) {
        this.songMapper = songMapper;
        this.singerService = singerService;
    }

    @Override
    public Long createAlbum(AlbumInfoDTO dto) {
        Album album = new Album();
        // 基础信息映射
        album.setName(dto.getAlbumName());
        album.setReleaseDate(dto.getReleaseDate());
        
        // 直接使用 Integer 类型，无需转换
        album.setType(dto.getType() != null ? dto.getType() : 1);
        album.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        
        // 其他字段
        album.setDescription(dto.getDescription());
        album.setCoverUrl(dto.getCoverUrl());
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());
        
        // 处理歌手信息 - 根据歌手名称查找并设置歌手ID
        if (StringUtils.hasText(dto.getSingerName())) {
            Singer singer = singerService.getSingerByName(dto.getSingerName());
            if (singer != null) {
                album.setSingerId(singer.getId());
            } else {
                // 如果歌手不存在，可以选择创建新歌手或抛出异常
                throw new IllegalArgumentException("歌手不存在：" + dto.getSingerName());
            }
        }
        
        save(album);
        return album.getId();
    }

    @Override
    public List<Album> getAlbumsBySingerId(Long singerId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Album>()
                .eq(Album::getSingerId, singerId)
                .orderByDesc(Album::getReleaseDate)); // 按发行日期倒序排列
    }

    @Override
    public List<Album> getAllAlbums() {
        return baseMapper.selectList(new LambdaQueryWrapper<Album>()
                .orderByDesc(Album::getReleaseDate));
    }

    @Override
    public List<AlbumInfoDTO> getAllAlbumsWithSingerName() {
        // 查询所有专辑
        List<Album> albums = baseMapper.selectList(new LambdaQueryWrapper<Album>()
                .orderByDesc(Album::getReleaseDate));
        
        // 转换为DTO并填充歌手名称
        return albums.stream().map(album -> {
            AlbumInfoDTO dto = new AlbumInfoDTO();
            
            // 手动设置字段，不使用BeanUtils.copyProperties
            dto.setId(album.getId());
            dto.setAlbumName(album.getName()); // 将Album的name映射到DTO的albumName
            dto.setReleaseDate(album.getReleaseDate());
            
            // 类型转换：Byte -> Integer
            dto.setType(album.getType() != null ? album.getType().intValue() : null);
            dto.setStatus(album.getStatus() != null ? album.getStatus().intValue() : null);
            
            dto.setDescription(album.getDescription());
            dto.setCoverUrl(album.getCoverUrl());
            
            // 查询并设置歌手名称
            if (album.getSingerId() != null) {
                Singer singer = singerService.getById(album.getSingerId());
                if (singer != null) {
                    dto.setSingerName(singer.getName());
                }
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean updateAlbum(AlbumInfoDTO dto) {
        if (dto.getId() == null || dto.getId() <= 0) {
            throw new IllegalArgumentException("专辑ID不能为空");
        }
    
        // 检查专辑是否存在
        Album album = getById(dto.getId());
        if (album == null) {
            throw new IllegalArgumentException("专辑不存在");
        }
    
        // 处理歌手信息更新
        if (StringUtils.hasText(dto.getSingerName())) {
            // 根据歌手名称查找歌手
            Singer singer = singerService.getSingerByName(dto.getSingerName());
            if (singer == null) {
                throw new IllegalArgumentException("歌手不存在：" + dto.getSingerName());
            }
            // 更新专辑的歌手ID
            album.setSingerId(singer.getId());
        }
    
        // 更新其他字段
        if (StringUtils.hasText(dto.getAlbumName())) {
            album.setName(dto.getAlbumName());
        }
        if (dto.getReleaseDate() != null) {
            album.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getType() != null) {
            album.setType(dto.getType()); // 直接使用Integer类型
        }
        if (StringUtils.hasText(dto.getDescription())) {
            album.setDescription(dto.getDescription());
        }
        if (StringUtils.hasText(dto.getCoverUrl())) {
            album.setCoverUrl(dto.getCoverUrl());
        }
        if (dto.getStatus() != null) {
            album.setStatus(dto.getStatus()); // 直接使用Integer类型
        }
        
        album.setUpdatedAt(LocalDateTime.now());
        return updateById(album);
    }

    @Override
    @Transactional
    public boolean deleteAlbum(Long id) {
        Album album = getById(id);
        if (album == null) {
            throw new IllegalArgumentException("专辑不存在");
        }

        // 1. 解除与歌曲的关联（将歌曲的album_id设为NULL）
        songMapper.update(null, new LambdaUpdateWrapper<Song>()
                .set(Song::getAlbumId, null)
                .eq(Song::getAlbumId, id));

        // 2. 物理删除专辑
        return removeById(id);
    }

    /**
     * 获取对象中为空的属性名数组
     */
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            try {
                Object srcValue = src.getPropertyValue(pd.getName());
                if (srcValue == null) emptyNames.add(pd.getName());
            } catch (Exception e) {
                // 忽略异常
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    @Override
    @Transactional // 确保事务性
    public String updateAlbumCover(Long albumId, MultipartFile coverFile) {
        // 1. 查询专辑是否存在
        Album album = getById(albumId);
        if (album == null) {
            throw new RuntimeException("专辑不存在");
        }

        // 2. 删除旧封面（如果存在）
        deleteOldCover(album);

        // 3. 上传新封面到MinIO（存储到album-covers目录）
        String coverUrl = minioService.uploadFile(coverFile,"album-covers");

        // 4. 更新数据库中的封面URL
        album.setCoverUrl(coverUrl); // 假设实体类Album的封面字段为coverUrl
        updateById(album);

        return coverUrl;
    }

    /**
     * 删除旧封面文件
     */
    private void deleteOldCover(Album album) {
        String oldCoverUrl = album.getCoverUrl();
        if (StringUtils.hasText(oldCoverUrl)) {
            try {
                minioService.deleteFile(oldCoverUrl);
                log.info("删除旧封面成功：{}", oldCoverUrl);
            } catch (Exception e) {
                log.warn("删除旧封面失败：{}", oldCoverUrl, e);
            }
        }
    }

    @Override
    public Album getAlbumByName(String albumName) {
        if (!StringUtils.hasText(albumName)) { 
            return null;
        }
        return baseMapper.selectOne(new LambdaQueryWrapper<Album>()
                .eq(Album::getName, albumName)
                .last("LIMIT 1"));
    }

    @Override
    public int countSongsByAlbumId(Long albumId) {
        if (albumId == null) {
            return 0;
        }
        return Math.toIntExact(songMapper.selectCount(new LambdaQueryWrapper<Song>()
                .eq(Song::getAlbumId, albumId)));
    }

    @Override
    public Long createAlbumByName(String albumName, Long singerId) {
        if (!StringUtils.hasText(albumName)) {
            throw new IllegalArgumentException("专辑名称不能为空");
        }
        
        AlbumInfoDTO albumDTO = new AlbumInfoDTO();
        albumDTO.setAlbumName(albumName);
        albumDTO.setType(1); // DTO 使用 Integer
        albumDTO.setReleaseDate(java.time.LocalDate.now());
        
        // 修正：通过singerId获取歌手名称
        if (singerId != null) {
            Singer singer = singerService.getById(singerId);
            if (singer != null) {
                albumDTO.setSingerName(singer.getName());
            }
        }
        
        return createAlbum(albumDTO);
    }

    @Override
    public AlbumPageVO getAlbumsPage(AlbumSearchDTO searchDTO) {
        // 使用 MyBatis Plus 的条件构造器进行查询
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(searchDTO.getAlbumName())) {
            queryWrapper.like(Album::getName, searchDTO.getAlbumName());
        }
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq(Album::getStatus, searchDTO.getStatus().byteValue());
        }
        if (searchDTO.getType() != null) {
            queryWrapper.eq(Album::getType, searchDTO.getType().byteValue());
        }
        
        // 按发行日期倒序排列
        queryWrapper.orderByDesc(Album::getReleaseDate);
        
        // 分页查询
        Page<Album> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
        IPage<Album> resultPage = baseMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO并填充歌手名称和歌曲数量
        List<AlbumInfoDTO> albumDTOList = resultPage.getRecords().stream().map(album -> {
            AlbumInfoDTO dto = new AlbumInfoDTO();
            
            dto.setId(album.getId());
            dto.setAlbumName(album.getName());
            dto.setReleaseDate(album.getReleaseDate());
            
            // 类型转换：Byte -> Integer
            dto.setType(album.getType() != null ? album.getType().intValue() : null);
            dto.setStatus(album.getStatus() != null ? album.getStatus().intValue() : null);
            
            dto.setDescription(album.getDescription());
            dto.setCoverUrl(album.getCoverUrl());
            
            // 查询并设置歌手名称
            if (album.getSingerId() != null) {
                Singer singer = singerService.getById(album.getSingerId());
                if (singer != null) {
                    dto.setSingerName(singer.getName());
                }
            }
            
            // 查询歌曲数量
            dto.setSongCount(countSongsByAlbumId(album.getId()));
            
            return dto;
        }).collect(Collectors.toList());
        
        // 应用歌曲数量过滤条件
        if (searchDTO.getMinSongCount() != null) {
            albumDTOList = albumDTOList.stream()
                    .filter(dto -> dto.getSongCount() >= searchDTO.getMinSongCount())
                    .collect(Collectors.toList());
        }
        if (searchDTO.getMaxSongCount() != null) {
            albumDTOList = albumDTOList.stream()
                    .filter(dto -> dto.getSongCount() <= searchDTO.getMaxSongCount())
                    .collect(Collectors.toList());
        }
        
        // 构建分页结果
        AlbumPageVO pageVO = new AlbumPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal((long) albumDTOList.size()); // 过滤后的总数
        pageVO.setRecords(albumDTOList);
        
        return pageVO;
    }
}