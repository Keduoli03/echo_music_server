package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lanke.echomusic.dto.playlist.PlaylistSearchDTO;
import com.lanke.echomusic.dto.playlist.PlaylistUpdateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.mapper.PlaylistMapper;
import com.lanke.echomusic.mapper.UserMapper;
import com.lanke.echomusic.service.IMusicTypeService;
import com.lanke.echomusic.service.IPlaylistCategoryService;
import com.lanke.echomusic.service.IPlaylistService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.service.MinioService;
import com.lanke.echomusic.vo.playlist.PlaylistListVO;
import com.lanke.echomusic.vo.playlist.PlaylistPageVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 歌单表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist> implements IPlaylistService {

    private final UserMapper userMapper;
    @Autowired
    private MinioService minioService;
    private static final Logger log = LoggerFactory.getLogger(SongServiceImpl.class);
    public PlaylistServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Long createPlaylist(Playlist playlist) {
        // 校验业务字段（用户ID由工具类保证存在）
        if (StringUtils.isBlank(playlist.getName())) {
            throw new IllegalArgumentException("歌单名称不能为空");
        }

        // 设置默认值
        playlist.setIsPublic(playlist.getIsPublic() != null ? playlist.getIsPublic() : 1);
        playlist.setSongCount(0);
        playlist.setPlayCount(0L);
        playlist.setCollectCount(0);
        playlist.setIsHomeDisplay((byte) 0);
        playlist.setHomeSort(0);

        boolean saveSuccess = save(playlist);
        if (!saveSuccess) {
            throw new RuntimeException("歌单创建失败");
        }
        return playlist.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlaylist(Long userId, PlaylistUpdateDTO dto) {
        // 查询歌单信息
        Playlist playlist = this.getById(dto.getId());
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }

        // 直接通过Mapper查询用户角色
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .select(User::getRole)
                        .eq(User::getId, userId)
        );

        // 验证用户权限
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        if (!isAdmin && !playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该歌单");
        }


        // 更新歌单信息（只更新DTO中不为null的字段）
        if (dto.getName() != null) {
            playlist.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            playlist.setDescription(dto.getDescription());
        }
        if (dto.getCoverUrl() != null) {
            playlist.setCoverUrl(dto.getCoverUrl());
        }
        if (dto.getIsPublic() != null) {
            playlist.setIsPublic(dto.getIsPublic().byteValue());
        }

        // 更新修改时间
        playlist.setUpdateTime(LocalDateTime.now());

        // 执行更新
        boolean success = this.updateById(playlist);
        if (!success) {
            throw new RuntimeException("歌单信息更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlaylist(Long userId, Long playlistId) {
        // 查询歌单信息
        Playlist playlist = this.getById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }

        // 直接通过Mapper查询用户角色
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .select(User::getRole)
                        .eq(User::getId, userId)
        );

        // 验证用户权限
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        if (!isAdmin && !playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该歌单");
        }

        // 执行删除
        boolean success = this.removeById(playlistId);
        if (!success) {
            throw new RuntimeException("歌单删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updatePlaylistCover(Long userId, Long playlistId, MultipartFile coverFile) {
        // 1. 查询歌单是否存在
        Playlist playlist = this.getById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }

        // 直接通过Mapper查询用户角色
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .select(User::getRole)
                        .eq(User::getId, userId)
        );
        // 2. 验证用户权限：普通用户只能修改自己的歌单，管理员可以修改所有歌单
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        if (!isAdmin && !playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改该歌单");
        }

        // 3. 删除旧封面（如果存在）
        deleteOldCover(playlist);

        // 4. 上传新封面到MinIO（存储到playlist-covers目录）
        String coverUrl = minioService.uploadFile(coverFile, "playlist-covers");

        // 5. 更新数据库中的封面URL
        playlist.setCoverUrl(coverUrl);
        playlist.setUpdateTime(LocalDateTime.now()); // 更新修改时间
        boolean success = this.updateById(playlist);
        if (!success) {
            throw new RuntimeException("歌单封面更新失败");
        }

        return coverUrl;
    }


    /**
     * 删除旧封面文件
     */
    private void deleteOldCover(Playlist playlist) {
        String oldCoverUrl = playlist.getCoverUrl();
        if (org.springframework.util.StringUtils.hasText(oldCoverUrl)) {
            try {
                minioService.deleteFile(oldCoverUrl);
                log.info("删除旧封面成功：{}", oldCoverUrl);
            } catch (Exception e) {
                log.warn("删除旧封面失败：{}", oldCoverUrl, e);
            }
        }
    }


    @Override
    public PlaylistPageVO searchPlaylists(PlaylistSearchDTO dto) {
        Page<PlaylistListVO> page = new Page<>(dto.getCurrent(), dto.getSize());
        IPage<PlaylistListVO> resultPage = baseMapper.searchPlaylists(page, dto);
        
        PlaylistPageVO pageVO = new PlaylistPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setRecords(resultPage.getRecords());
        
        return pageVO;
    }

    @Autowired
    private IMusicTypeService musicTypeService;

    @Autowired
    private IPlaylistCategoryService playlistCategoryService;

    
    @Override
    public List<Map<String, Object>> getAllMusicTypes() {
        return musicTypeService.getAllActiveMusicTypes();
    }

    @Override
    public List<Map<String, Object>> getAllPlaylistCategories() {
        return playlistCategoryService.getAllActiveCategories();
    }
}

