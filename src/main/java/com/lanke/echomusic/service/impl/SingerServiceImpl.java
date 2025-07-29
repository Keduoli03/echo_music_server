package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lanke.echomusic.dto.singer.SingerInfoDTO;
import com.lanke.echomusic.dto.singer.SingerSearchDTO;
import com.lanke.echomusic.entity.Album;
import com.lanke.echomusic.entity.Singer;
import com.lanke.echomusic.entity.SongSinger;
import com.lanke.echomusic.mapper.AlbumMapper;
import com.lanke.echomusic.mapper.SingerMapper;
import com.lanke.echomusic.service.ISingerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.service.ISongSingerService;
import com.lanke.echomusic.service.MinioService;
import com.lanke.echomusic.vo.singer.SingerPageVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements ISingerService {

    private final AlbumMapper albumMapper;

    private final ISongSingerService songSingerService;
    private final MinioService minioService;
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);

    public SingerServiceImpl(AlbumMapper albumMapper, ISongSingerService songSingerService, MinioService minioService) {
        this.albumMapper = albumMapper;
        this.songSingerService = songSingerService;
        this.minioService = minioService;
    }

    @Override
    public Long createSinger(SingerInfoDTO dto) {
        Singer singer = new Singer();
        // 复制DTO所有字段（包括别名、头像等）
        BeanUtils.copyProperties(dto, singer);
        
        // 类型转换：Integer -> Byte
        if (dto.getGender() != null) {
            singer.setGender(dto.getGender().byteValue());
        }
        if (dto.getStatus() != null) {
            singer.setStatus(dto.getStatus().byteValue());
        } else {
            singer.setStatus((byte) 1); // 默认启用状态
        }
    
        // 自动填充时间字段（可通过MyBatis-Plus实现）
        singer.setCreatedAt(LocalDateTime.now());
        singer.setUpdatedAt(LocalDateTime.now());
    
        baseMapper.insert(singer);
        return singer.getId();
    }

    @Override
    public Singer getSingerByName(String name) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Singer>().eq(Singer::getName, name));
    }

    @Override
    public List<SingerInfoDTO> listSingersByIds(List<Long> singerIds) {
        List<Singer> singers = listByIds(singerIds);
        return singers.stream()
                .map(singer -> {
                    SingerInfoDTO dto = new SingerInfoDTO();
                    BeanUtils.copyProperties(singer, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public boolean updateSinger(SingerInfoDTO dto) {
        if (dto.getId() == null || dto.getId() <= 0) {
            throw new IllegalArgumentException("歌手ID不能为空");
        }

        Singer singer = getById(dto.getId());
        if (singer == null) {
            throw new IllegalArgumentException("歌手不存在");
        }

        BeanUtils.copyProperties(dto, singer, getNullPropertyNames(dto));
        singer.setUpdatedAt(LocalDateTime.now());

        return updateById(singer);
    }

    @Override
    @Transactional
    public boolean deleteSinger(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("歌手ID不能为空");
        }

        Singer singer = getById(id);
        if (singer == null) {
            throw new IllegalArgumentException("歌手不存在");
        }

        // 使用checkRelatedData方法检查所有关联数据
        if (checkRelatedData(id)) {
            throw new RuntimeException("歌手有关联数据（专辑或歌曲），无法删除");
        }

        // 删除头像文件
        deleteOldAvatar(singer);

        // 无关联数据，执行物理删除
        return removeById(id);
    }

    /**
     * 检查歌手是否有关联数据
     */
    private boolean checkRelatedData(Long singerId) {
        // 检查是否有关联专辑
        long albumCount = albumMapper.selectCount(new LambdaQueryWrapper<Album>()
                .eq(Album::getSingerId, singerId));

        // 检查是否有关联歌曲
        long songCount = songSingerService.count(new LambdaQueryWrapper<SongSinger>()
                .eq(SongSinger::getSingerId, singerId));

        return albumCount > 0 || songCount > 0;
    }
    /**
     * 获取对象中为空的属性名数组
     */
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            if (src.getPropertyValue(pd.getName()) == null) {
                emptyNames.add(pd.getName());
            }
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    @Override
    @Transactional // 确保事务性
    public String updateSingerAvatar(Long singerId, MultipartFile avatarFile) {
        // 1. 查询歌手是否存在
        Singer singer = getById(singerId);
        if (singer == null) {
            throw new RuntimeException("歌手不存在");
        }

        // 2. 删除旧头像（如果存在）
        deleteOldAvatar(singer);

        // 3. 上传新头像到MinIO（存储到avatars目录）
        String avatarUrl = minioService.uploadFile(avatarFile,"avatars");

        // 4. 更新数据库中的头像URL
        singer.setAvatar(avatarUrl); // 假设实体类Singer的头像字段为avatar
        updateById(singer);

        return avatarUrl;
    }

    /**
     * 删除旧头像文件
     */
    private void deleteOldAvatar(Singer singer) {
        String oldAvatarUrl = singer.getAvatar();
        if (StringUtils.hasText(oldAvatarUrl)) {
            try {
                minioService.deleteFile(oldAvatarUrl);
                log.info("删除旧头像成功：{}", oldAvatarUrl);
            } catch (Exception e) {
                log.warn("删除旧头像失败：{}", oldAvatarUrl, e);
            }
        }
    }


    @Override
    public SingerPageVO getSingersPage(SingerSearchDTO searchDTO) {
        // 使用 MyBatis Plus 的条件构造器进行查询
        LambdaQueryWrapper<Singer> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(searchDTO.getName())) {
            queryWrapper.like(Singer::getName, searchDTO.getName());
        }
        if (StringUtils.hasText(searchDTO.getAlias())) {
            queryWrapper.like(Singer::getAlias, searchDTO.getAlias());
        }
        if (StringUtils.hasText(searchDTO.getNationality())) {
            queryWrapper.like(Singer::getNationality, searchDTO.getNationality());
        }
        if (searchDTO.getGender() != null) {
            queryWrapper.eq(Singer::getGender, searchDTO.getGender().byteValue());
        }
        if (searchDTO.getStatus() != null) {
            queryWrapper.eq(Singer::getStatus, searchDTO.getStatus().byteValue());
        }
        
        // 处理排序
        if (StringUtils.hasText(searchDTO.getOrderBy())) {
            String[] orderFields = searchDTO.getOrderBy().split(";");
            for (String orderField : orderFields) {
                String[] parts = orderField.split(",");
                if (parts.length == 2) {
                    String field = parts[0].trim();
                    String direction = parts[1].trim();
                    
                    switch (field) {
                        case "name":
                            if ("desc".equalsIgnoreCase(direction)) {
                                queryWrapper.orderByDesc(Singer::getName);
                            } else {
                                queryWrapper.orderByAsc(Singer::getName);
                            }
                            break;
                        case "createdAt":
                            if ("desc".equalsIgnoreCase(direction)) {
                                queryWrapper.orderByDesc(Singer::getCreatedAt);
                            } else {
                                queryWrapper.orderByAsc(Singer::getCreatedAt);
                            }
                            break;
                        case "birthDate":
                            if ("desc".equalsIgnoreCase(direction)) {
                                queryWrapper.orderByDesc(Singer::getBirthDate);
                            } else {
                                queryWrapper.orderByAsc(Singer::getBirthDate);
                            }
                            break;
                    }
                }
            }
        } else {
            // 默认按创建时间倒序排列
            queryWrapper.orderByDesc(Singer::getCreatedAt);
        }
        
        // 分页查询
        Page<Singer> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
        IPage<Singer> resultPage = baseMapper.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<SingerInfoDTO> singerDTOList = resultPage.getRecords().stream().map(singer -> {
            SingerInfoDTO dto = new SingerInfoDTO();
            BeanUtils.copyProperties(singer, dto);
            // 类型转换：Byte -> Integer
            dto.setGender(singer.getGender() != null ? singer.getGender().intValue() : null);
            dto.setStatus(singer.getStatus() != null ? singer.getStatus().intValue() : null);
            return dto;
        }).collect(Collectors.toList());
        
        // 构建分页结果
        SingerPageVO pageVO = new SingerPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setRecords(singerDTOList);
        
        return pageVO;
    }

}