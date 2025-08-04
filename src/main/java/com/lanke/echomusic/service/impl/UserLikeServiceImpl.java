package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.entity.UserLike;
import com.lanke.echomusic.mapper.UserLikeMapper;
import com.lanke.echomusic.service.ISongService;
import com.lanke.echomusic.service.IUserLikeService;
import com.lanke.echomusic.vo.song.SongListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户喜欢歌曲关联表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-01-20
 */
@Service
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements IUserLikeService {

    @Autowired
    private ISongService songService;

    @Override
    @Transactional
    public boolean toggleLike(Long userId, Long songId) {
        // 检查是否已经喜欢
        LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLike::getUserId, userId)
                   .eq(UserLike::getSongId, songId);
        
        UserLike existingLike = getOne(queryWrapper);
        
        if (existingLike != null) {
            // 已喜欢，取消喜欢
            removeById(existingLike.getId());
            // 更新歌曲喜欢数量
            updateSongLikeCount(songId, -1);
            return false;
        } else {
            // 未喜欢，添加喜欢
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setSongId(songId);
            userLike.setCreatedAt(LocalDateTime.now());
            save(userLike);
            // 更新歌曲喜欢数量
            updateSongLikeCount(songId, 1);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, Long songId) {
        LambdaQueryWrapper<UserLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLike::getUserId, userId)
                   .eq(UserLike::getSongId, songId);
        return count(queryWrapper) > 0;
    }

    @Override
    public IPage<SongListVO> getUserLikedSongs(Long userId, Long current, Long size) {
        Page<SongListVO> page = new Page<>(current, size);
        return baseMapper.selectUserLikedSongs(page, userId);
    }

    @Override
    public List<Long> getUserLikedSongIds(Long userId) {
        return baseMapper.selectLikedSongIds(userId);
    }

    /**
     * 更新歌曲喜欢数量
     */
    private void updateSongLikeCount(Long songId, int delta) {
        Song song = songService.getById(songId);
        if (song != null) {
            long newCount = Math.max(0, (song.getLikeCount() != null ? song.getLikeCount() : 0) + delta);
            song.setLikeCount(newCount);
            songService.updateById(song);
        }
    }
}