package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.entity.UserLike;
import com.lanke.echomusic.vo.song.SongListVO;

import java.util.List;

/**
 * <p>
 * 用户喜欢歌曲关联表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-01-20
 */
public interface IUserLikeService extends IService<UserLike> {

    /**
     * 用户喜欢/取消喜欢歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return true-喜欢，false-取消喜欢
     */
    boolean toggleLike(Long userId, Long songId);

    /**
     * 检查用户是否喜欢某首歌曲
     * @param userId 用户ID
     * @param songId 歌曲ID
     * @return true-已喜欢，false-未喜欢
     */
    boolean isLiked(Long userId, Long songId);

    /**
     * 分页查询用户喜欢的歌曲列表
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 歌曲列表
     */
    IPage<SongListVO> getUserLikedSongs(Long userId, Long current, Long size);

    /**
     * 获取用户喜欢的歌曲ID列表
     * @param userId 用户ID
     * @return 歌曲ID列表
     */
    List<Long> getUserLikedSongIds(Long userId);
}