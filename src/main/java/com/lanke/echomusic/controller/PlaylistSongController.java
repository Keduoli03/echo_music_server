package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.playlist.AddSongToPlaylistDTO;
import com.lanke.echomusic.dto.playlist.PlaylistSongSearchDTO;
import com.lanke.echomusic.service.IPlaylistSongService;
import com.lanke.echomusic.utils.RequestProcessor;
import com.lanke.echomusic.vo.playlist.AddSongToPlaylistResult;
import com.lanke.echomusic.vo.playlist.PlaylistSongPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 歌单-歌曲关联表 前端控制器
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@RestController
@Tag(name = "歌单歌曲管理", description = "歌单歌曲添加、删除、查询等功能")
@RequestMapping("/api/playlist/song")
public class PlaylistSongController {

    @Resource
    private IPlaylistSongService playlistSongService;
    
    @Resource
    private RequestProcessor requestProcessor;

    @Operation(summary = "添加歌曲到歌单", description = "将一首或多首歌曲添加到指定歌单")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/add")
    public Result<AddSongToPlaylistResult> addSongsToPlaylist(@RequestBody @Valid AddSongToPlaylistDTO dto) {
        try {
            Long userId = requestProcessor.getUserId();
            AddSongToPlaylistResult result = playlistSongService.addSongsToPlaylist(userId, dto);
            
            // 根据不同情况返回不同的状态码和消息
            if (result.getAddedCount() == 0 && result.getDuplicateCount() > 0) {
                // 所有歌曲都重复，返回409 Conflict
                return Result.error(409, result.getMessage());
            } else {
                // 全部成功或部分成功，都返回200 OK
                return Result.success(result);
            }
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "添加歌曲到歌单失败：" + e.getMessage());
        }
    }

    @Operation(summary = "从歌单中移除歌曲", description = "从指定歌单中移除一首歌曲")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/remove")
    public Result<String> removeSongFromPlaylist(
            @Parameter(description = "歌单ID", required = true) @RequestParam Long playlistId,
            @Parameter(description = "歌曲ID", required = true) @RequestParam Long songId) {
        try {
            Long userId = requestProcessor.getUserId();
            playlistSongService.removeSongFromPlaylist(userId, playlistId, songId);
            return Result.success("歌曲已从歌单中移除");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @Operation(summary = "获取歌单中的歌曲列表", description = "分页获取指定歌单中的歌曲，支持搜索和排序")
    @GetMapping("/list")
    public Result<PlaylistSongPageVO> getPlaylistSongs(@ParameterObject PlaylistSongSearchDTO dto) {
        try {
            PlaylistSongPageVO result = playlistSongService.getPlaylistSongs(dto);
            return Result.success("获取成功", result);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @Operation(summary = "调整歌曲在歌单中的排序", description = "修改歌曲在歌单中的显示顺序")
    @SecurityRequirement(name = "Authorization")
    @PutMapping("/sort")
    public Result<String> updateSongSort(
            @Parameter(description = "歌单ID", required = true) @RequestParam Long playlistId,
            @Parameter(description = "歌曲ID", required = true) @RequestParam Long songId,
            @Parameter(description = "新的排序值", required = true) @RequestParam Integer newSort) {
        try {
            Long userId = requestProcessor.getUserId();
            playlistSongService.updateSongSort(userId, playlistId, songId, newSort);
            return Result.success("歌曲排序已更新");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
