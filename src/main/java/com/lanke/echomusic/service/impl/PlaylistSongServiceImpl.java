package com.lanke.echomusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lanke.echomusic.dto.playlist.AddSongToPlaylistDTO;
import com.lanke.echomusic.dto.playlist.PlaylistSongSearchDTO;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.entity.PlaylistSong;
import com.lanke.echomusic.entity.Song;
import com.lanke.echomusic.entity.User;
import com.lanke.echomusic.mapper.PlaylistSongMapper;
import com.lanke.echomusic.mapper.UserMapper;
import com.lanke.echomusic.service.IPlaylistService;
import com.lanke.echomusic.service.IPlaylistSongService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lanke.echomusic.service.ISongService;
import com.lanke.echomusic.vo.playlist.AddSongToPlaylistResult;
import com.lanke.echomusic.vo.playlist.PlaylistSongListVO;
import com.lanke.echomusic.vo.playlist.PlaylistSongPageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 歌单-歌曲关联表 服务实现类
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@Service
public class PlaylistSongServiceImpl extends ServiceImpl<PlaylistSongMapper, PlaylistSong> implements IPlaylistSongService {

    @Autowired
    private IPlaylistService playlistService;
    
    @Autowired
    private ISongService songService;
    
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddSongToPlaylistResult addSongsToPlaylist(Long userId, AddSongToPlaylistDTO dto) {
        // 1. 验证歌单是否存在且用户有权限
        Playlist playlist = playlistService.getById(dto.getPlaylistId());
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }
        
        // 验证用户权限
        validatePlaylistPermission(userId, playlist);
        
        // 2. 验证歌曲是否存在
        List<Song> songs = songService.listByIds(dto.getSongIds());
        if (songs.size() != dto.getSongIds().size()) {
            throw new IllegalArgumentException("部分歌曲不存在");
        }
        
        // 3. 检查重复歌曲（默认检查）
        List<PlaylistSong> existingSongs = this.list(
            new LambdaQueryWrapper<PlaylistSong>()
                .eq(PlaylistSong::getPlaylistId, dto.getPlaylistId())
                .in(PlaylistSong::getSongId, dto.getSongIds())
        );
        
        Set<Long> existingSongIds = existingSongs.stream()
            .map(PlaylistSong::getSongId)
            .collect(Collectors.toSet());
        
        List<Long> validSongIds = dto.getSongIds().stream()
            .filter(songId -> !existingSongIds.contains(songId))
            .collect(Collectors.toList());
        
        List<Long> duplicateSongIds = dto.getSongIds().stream()
            .filter(existingSongIds::contains)
            .collect(Collectors.toList());
        
        // 4. 如果没有新歌曲需要添加，直接返回重复信息
        if (validSongIds.isEmpty()) {
            return new AddSongToPlaylistResult(0, duplicateSongIds.size(), duplicateSongIds);
        }
        
        // 5. 获取当前歌单中的最大排序值
        Integer maxSort = baseMapper.selectMaxSortByPlaylistId(dto.getPlaylistId());
        if (maxSort == null) {
            maxSort = 0;
        }
        
        // 6. 批量添加歌曲到歌单
        List<PlaylistSong> playlistSongs = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < validSongIds.size(); i++) {
            PlaylistSong playlistSong = new PlaylistSong();
            playlistSong.setPlaylistId(dto.getPlaylistId());
            playlistSong.setSongId(validSongIds.get(i));
            playlistSong.setSort(maxSort + i + 1);
            playlistSong.setAddTime(now);
            playlistSongs.add(playlistSong);
        }
        
        boolean success = this.saveBatch(playlistSongs);
        if (!success) {
            throw new RuntimeException("添加歌曲到歌单失败");
        }
        
        // 7. 更新歌单的歌曲数量
        playlist.setSongCount(playlist.getSongCount() + validSongIds.size());
        playlistService.updateById(playlist);
        
        return new AddSongToPlaylistResult(validSongIds.size(), duplicateSongIds.size(), duplicateSongIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSongFromPlaylist(Long userId, Long playlistId, Long songId) {
        // 1. 验证歌单是否存在且用户有权限
        Playlist playlist = playlistService.getById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }
        
        validatePlaylistPermission(userId, playlist);
        
        // 2. 查找并删除歌单歌曲关联
        PlaylistSong playlistSong = this.getOne(
            new LambdaQueryWrapper<PlaylistSong>()
                .eq(PlaylistSong::getPlaylistId, playlistId)
                .eq(PlaylistSong::getSongId, songId)
        );
        
        if (playlistSong == null) {
            throw new IllegalArgumentException("歌曲不在该歌单中");
        }
        
        boolean success = this.removeById(playlistSong.getId());
        if (!success) {
            throw new RuntimeException("从歌单中移除歌曲失败");
        }
        
        // 3. 更新歌单的歌曲数量
        playlist.setSongCount(Math.max(0, playlist.getSongCount() - 1));
        playlistService.updateById(playlist);
    }

    @Override
    public PlaylistSongPageVO getPlaylistSongs(PlaylistSongSearchDTO dto) {
        // 验证歌单是否存在
        Playlist playlist = playlistService.getById(dto.getPlaylistId());
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }
        
        Page<PlaylistSongListVO> page = new Page<>(dto.getCurrent(), dto.getSize());
        IPage<PlaylistSongListVO> resultPage = baseMapper.searchPlaylistSongs(page, dto);
        
        PlaylistSongPageVO pageVO = new PlaylistSongPageVO();
        pageVO.setCurrent(resultPage.getCurrent());
        pageVO.setSize(resultPage.getSize());
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setRecords(resultPage.getRecords());
        
        return pageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSongSort(Long userId, Long playlistId, Long songId, Integer newSort) {
        // 1. 验证歌单是否存在且用户有权限
        Playlist playlist = playlistService.getById(playlistId);
        if (playlist == null) {
            throw new IllegalArgumentException("歌单不存在");
        }
        
        validatePlaylistPermission(userId, playlist);
        
        // 2. 查找歌单歌曲关联
        PlaylistSong playlistSong = this.getOne(
            new LambdaQueryWrapper<PlaylistSong>()
                .eq(PlaylistSong::getPlaylistId, playlistId)
                .eq(PlaylistSong::getSongId, songId)
        );
        
        if (playlistSong == null) {
            throw new IllegalArgumentException("歌曲不在该歌单中");
        }
        
        // 3. 更新排序
        playlistSong.setSort(newSort);
        boolean success = this.updateById(playlistSong);
        if (!success) {
            throw new RuntimeException("更新歌曲排序失败");
        }
    }
    
    /**
     * 验证用户对歌单的操作权限
     */
    private void validatePlaylistPermission(Long userId, Playlist playlist) {
        // 查询用户角色
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .select(User::getRole)
                .eq(User::getId, userId)
        );
        
        // 验证权限：管理员或歌单创建者可以操作
        boolean isAdmin = user != null && "ADMIN".equals(user.getRole());
        if (!isAdmin && !playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该歌单");
        }
    }
}
