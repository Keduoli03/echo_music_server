package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.entity.UserPlaylistLike;
import com.lanke.echomusic.mapper.UserPlaylistLikeMapper;
import com.lanke.echomusic.service.IPlaylistService;
import com.lanke.echomusic.service.IUserPlaylistLikeService;
import com.lanke.echomusic.vo.playlist.PlaylistListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户收藏歌单关联表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
@Service
public class UserPlaylistLikeServiceImpl extends ServiceImpl<UserPlaylistLikeMapper, UserPlaylistLike> implements IUserPlaylistLikeService {

    @Autowired
    private IPlaylistService playlistService;

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long playlistId) {
        // 检查歌单是否存在
        Playlist playlist = playlistService.getById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }

        // 检查是否已经收藏
        LambdaQueryWrapper<UserPlaylistLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlaylistLike::getUserId, userId)
                   .eq(UserPlaylistLike::getPlaylistId, playlistId);
        
        UserPlaylistLike existingLike = getOne(queryWrapper);
        
        if (existingLike != null) {
            // 已收藏，取消收藏
            removeById(existingLike.getId());
            // 更新歌单收藏数量
            updatePlaylistCollectCount(playlistId, -1);
            return false;
        } else {
            // 未收藏，添加收藏
            UserPlaylistLike userPlaylistLike = new UserPlaylistLike();
            userPlaylistLike.setUserId(userId);
            userPlaylistLike.setPlaylistId(playlistId);
            userPlaylistLike.setCreatedAt(LocalDateTime.now());
            save(userPlaylistLike);
            // 更新歌单收藏数量
            updatePlaylistCollectCount(playlistId, 1);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, Long playlistId) {
        LambdaQueryWrapper<UserPlaylistLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlaylistLike::getUserId, userId)
                   .eq(UserPlaylistLike::getPlaylistId, playlistId);
        return count(queryWrapper) > 0;
    }

    @Override
    public IPage<PlaylistListVO> getUserLikedPlaylists(Long userId, Long current, Long size) {
        Page<PlaylistListVO> page = new Page<>(current, size);
        return baseMapper.selectUserLikedPlaylists(page, userId);
    }

    @Override
    public List<Long> getUserLikedPlaylistIds(Long userId) {
        return baseMapper.selectLikedPlaylistIds(userId);
    }

    /**
     * 更新歌单收藏数量
     */
    private void updatePlaylistCollectCount(Long playlistId, int delta) {
        Playlist playlist = playlistService.getById(playlistId);
        if (playlist != null) {
            int newCount = Math.max(0, (playlist.getCollectCount() != null ? playlist.getCollectCount() : 0) + delta);
            playlist.setCollectCount(newCount);
            playlistService.updateById(playlist);
        }
    }
}