package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.dto.banner.BannerSearchDTO;
import com.lanke.echomusic.entity.Banner;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.entity.Album;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.mapper.BannerMapper;
import com.lanke.echomusic.service.IBannerService;
import com.lanke.echomusic.service.ISongService;
import com.lanke.echomusic.vo.banner.BannerPageVO;
import com.lanke.echomusic.service.IAlbumService;
import com.lanke.echomusic.service.IPlaylistService;
import com.lanke.echomusic.service.MinioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Banner轮播图服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

    @Autowired
    private ISongService songService;

    @Autowired
    private IAlbumService albumService;

    @Autowired
    private IPlaylistService playlistService;
    
    @Autowired
    private MinioService minioService;
    
    private static final Logger log = LoggerFactory.getLogger(BannerServiceImpl.class);

    @Override
    public List<Banner> getActiveBanners() {
        return list(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getIsActive, 1)
                .orderByAsc(Banner::getSortOrder)
                .orderByDesc(Banner::getCreatedAt));
    }

    @Override
    @Transactional
    public Banner createBanner(Banner banner) {
        // 验证跳转类型
        if (!banner.isValidType()) {
            throw new IllegalArgumentException("无效的跳转类型: " + banner.getType());
        }
    
        // 跳转目标验证改为可选：只有当linkId不为空时才验证
        if (banner.getLinkId() != null && !validateLinkTarget(banner.getType(), banner.getLinkId())) {
            throw new IllegalArgumentException("跳转目标不存在");
        }
    
        // 设置默认值
        if (banner.getLinkId() == null) {
            banner.setLinkId(0L); // 设置默认值为0，表示暂无跳转目标
        }
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        if (banner.getIsActive() == null) {
            banner.setIsActive(1);
        }
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());
    
        save(banner);
        return banner;
    }

    @Override
    @Transactional
    public Banner updateBanner(Banner banner) {
        Banner existingBanner = getById(banner.getId());
        if (existingBanner == null) {
            throw new IllegalArgumentException("Banner不存在");
        }

        // 验证跳转类型
        if (!banner.isValidType()) {
            throw new IllegalArgumentException("无效的跳转类型: " + banner.getType());
        }

        // 跳转目标验证改为可选：只有当linkId不为空时才验证
        if (banner.getLinkId() != null && !validateLinkTarget(banner.getType(), banner.getLinkId())) {
            throw new IllegalArgumentException("跳转目标不存在");
        }

        banner.setUpdatedAt(LocalDateTime.now());
        updateById(banner);
        return banner;
    }

    @Override
    @Transactional
    public void toggleBannerStatus(Long id, Integer isActive) {
        Banner banner = getById(id);
        if (banner == null) {
            throw new IllegalArgumentException("Banner不存在");
        }

        banner.setIsActive(isActive);
        banner.setUpdatedAt(LocalDateTime.now());
        updateById(banner);
    }

    @Override
    public boolean validateLinkTarget(String type, Long linkId) {
        if (type == null || linkId == null) {
            return false;
        }

        switch (type) {
            case Banner.TYPE_SONG:
                Song song = songService.getById(linkId);
                return song != null && song.getStatus() == 1; // 确保歌曲存在且启用
            case Banner.TYPE_ALBUM:
                Album album = albumService.getById(linkId);
                return album != null && album.getStatus() == 1; // 确保专辑存在且启用
            case Banner.TYPE_PLAYLIST:
                Playlist playlist = playlistService.getById(linkId);
                return playlist != null && playlist.getIsPublic() == 1; // 确保歌单存在且公开
            default:
                return false;
        }
    }

    @Override
    public String uploadBannerImage(MultipartFile file) {
        try {
            // 1. 验证文件类型
            if (!isValidImageType(file)) {
                throw new IllegalArgumentException("仅支持JPG、PNG、GIF格式");
            }
            
            // 2. 使用MinIO服务上传文件到banner-images目录
            String imageUrl = minioService.uploadFile(file, "banner-images");
            
            log.info("Banner图片上传成功：{}", imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("Banner图片上传失败", e);
            throw new RuntimeException("Banner图片上传失败: " + e.getMessage());
        }
    }
    
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif")
        );
    }
    

    @Override
    public BannerPageVO getBannersPage(BannerSearchDTO searchDTO) {
        // 创建分页对象
        Page<Banner> page = new Page<>(searchDTO.getCurrent(), searchDTO.getSize());
        
        // 构建查询条件
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        
        // 标题模糊查询
        if (StringUtils.hasText(searchDTO.getTitle())) {
            queryWrapper.like("title", searchDTO.getTitle());
        }
        
        // 描述模糊查询
        if (StringUtils.hasText(searchDTO.getDescription())) {
            queryWrapper.like("description", searchDTO.getDescription());
        }
        
        // 跳转类型查询
        if (StringUtils.hasText(searchDTO.getType())) {
            queryWrapper.eq("type", searchDTO.getType());
        }
        
        // 状态查询
        if (searchDTO.getIsActive() != null) {
            queryWrapper.eq("is_active", searchDTO.getIsActive());
        }
        
        // 处理排序
        if (StringUtils.hasText(searchDTO.getOrderBy())) {
            String[] orderParts = searchDTO.getOrderBy().split(",");
            for (String orderPart : orderParts) {
                String[] parts = orderPart.trim().split(",");
                if (parts.length >= 1) {
                    String field = parts[0].trim();
                    String direction = parts.length > 1 ? parts[1].trim() : "asc";
                    
                    // 转换字段名为数据库字段
                    String dbField = convertToDbField(field);
                    if (dbField != null) {
                        if ("desc".equalsIgnoreCase(direction)) {
                            queryWrapper.orderByDesc(dbField);
                        } else {
                            queryWrapper.orderByAsc(dbField);
                        }
                    }
                }
            }
        } else {
            // 默认排序：按排序字段升序，创建时间降序
            queryWrapper.orderByAsc("sort_order").orderByDesc("created_at");
        }
        
        // 执行分页查询
        IPage<Banner> resultPage = page(page, queryWrapper);
        
        // 构建返回结果
        BannerPageVO pageVO = new BannerPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setRecords(resultPage.getRecords());
        
        return pageVO;
    }
    
    /**
     * 转换排序字段名为数据库字段名
     */
    private String convertToDbField(String field) {
        switch (field) {
            case "sort":
                return "sort_order";
            case "createdAt":
                return "created_at";
            case "updatedAt":
                return "updated_at";
            default:
                return null;
        }
    }
}