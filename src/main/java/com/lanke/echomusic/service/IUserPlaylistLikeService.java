package com.lanke.echomusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lanke.echomusic.entity.UserPlaylistLike;
import com.lanke.echomusic.vo.playlist.PlaylistListVO;

import java.util.List;

/**
 * <p>
 * 用户收藏歌单关联表 服务类
 * </p>
 *
 * @author lanke
 * @since 2025-01-21
 */
public interface IUserPlaylistLikeService extends IService<UserPlaylistLike> {

    /**
     * 用户收藏/取消收藏歌单
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @return true-收藏，false-取消收藏
     */
    boolean toggleLike(Long userId, Long playlistId);

    /**
     * 检查用户是否收藏某个歌单
     * @param userId 用户ID
     * @param playlistId 歌单ID
     * @return true-已收藏，false-未收藏
     */
    boolean isLiked(Long userId, Long playlistId);

    /**
     * 分页查询用户收藏的歌单列表
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 歌单列表
     */
    IPage<PlaylistListVO> getUserLikedPlaylists(Long userId, Long current, Long size);

    /**
     * 获取用户收藏的歌单ID列表
     * @param userId 用户ID
     * @return 歌单ID列表
     */
    List<Long> getUserLikedPlaylistIds(Long userId);
}