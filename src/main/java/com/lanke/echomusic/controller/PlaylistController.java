package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.playlist.PlaylistDeleteDTO;
import com.lanke.echomusic.dto.playlist.PlaylistInfoDTO;
import com.lanke.echomusic.dto.playlist.PlaylistSearchDTO;
import com.lanke.echomusic.dto.playlist.PlaylistUpdateDTO;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.service.IPlaylistService;
import com.lanke.echomusic.utils.RequestProcessor;
import com.lanke.echomusic.vo.playlist.PlaylistPageVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lanke.echomusic.service.IUserPlaylistLikeService;

/**
 * <p>
 * 歌单表 前端控制器
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@RestController
@Tag(name = "歌单管理", description = "歌单信息维护")
@RequestMapping("/api/playlist")
public class PlaylistController {

    @Resource
    private IPlaylistService playlistService;
    @Resource
    private RequestProcessor requestProcessor; // 注入工具类

    @Operation(summary = "新建歌单")
    @SecurityRequirement(name = "Authorization") // 添加认证要求
    @PostMapping("/create")
    public Result<Long> createPlaylist(@RequestBody PlaylistInfoDTO dto) {
        try {
            // 从工具类获取用户ID（自动处理未登录/无效令牌等情况）
            Long userId = requestProcessor.getUserId();
            // 封装歌单对象（用户ID由后端注入）
            Playlist playlist = new Playlist();
            BeanUtils.copyProperties(dto, playlist);
            playlist.setUserId(userId);
            Long playlistId = playlistService.createPlaylist(playlist);
            return Result.success("歌单创建成功", playlistId);
        } catch (Exception e) { // 示例：未登录异常
            return Result.error(401, "请先登录");
        }
    }


    @Operation(summary = "修改歌单信息")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/update")
    public Result<Void> updatePlaylist(@RequestBody PlaylistUpdateDTO dto) {
        try {
            // 获取当前用户ID
            Long userId = requestProcessor.getUserId();

            // 执行修改操作
            playlistService.updatePlaylist(userId, dto);

            return Result.success("歌单信息修改成功");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "修改失败，请稍后再试");
        }
    }


    @Operation(summary = "删除歌单")
    @SecurityRequirement(name = "Authorization")
    @PostMapping("/delete")
    public Result<Void> deletePlaylist(@RequestBody PlaylistDeleteDTO dto) {
        try {
            // 获取当前用户ID
            Long userId = requestProcessor.getUserId();

            // 执行删除操作
            playlistService.deletePlaylist(userId, dto.getId());

            return Result.success("歌单删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(403, e.getMessage());
        }
    }

    @Operation(summary = "更新歌单封面", description = "为指定歌单上传封面图片（支持JPG/PNG等格式）")
    @PostMapping(value = "/upload/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadPlaylistCover(
            @RequestParam("playlistId") Long playlistId,   // 歌单ID
            @RequestParam("coverFile") MultipartFile file // 封面文件
    ) {
        try {
            // 基础校验
            if (playlistId == null || playlistId <= 0) {
                return Result.error(400, "歌单ID不能为空");
            }
            if (file.isEmpty()) {
                return Result.error(400, "请选择要上传的封面文件");
            }
            if (!isValidImageType(file)) {
                return Result.error(400, "仅支持JPG、PNG、GIF格式");
            }

            // 获取当前用户ID
            Long userId = requestProcessor.getUserId();

            // 调用服务层上传封面
            String coverUrl = playlistService.updatePlaylistCover(userId, playlistId, file);
            return Result.success("封面上传成功", coverUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }  catch (Exception e) {
            return Result.error(500, "封面上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取歌单列表", description = "支持分页、条件筛选和多字段排序的歌单查询接口")
    @GetMapping("/getPlaylistList")
    public Result<PlaylistPageVO> getPlaylistsPage(@ParameterObject PlaylistSearchDTO dto) {
        PlaylistPageVO result = playlistService.searchPlaylists(dto);
        return Result.success("获取成功", result);
    }

    @Operation(summary = "获取音乐类型列表")
    @GetMapping("/musicTypes")
    public Result<List<Map<String, Object>>> getMusicTypes() {
        List<Map<String, Object>> musicTypes = playlistService.getAllMusicTypes();
        return Result.success("获取成功", musicTypes);
    }

    @Operation(summary = "获取歌单分类列表")
    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> getPlaylistCategories() {
        List<Map<String, Object>> categories = playlistService.getAllPlaylistCategories();
        return Result.success("获取成功", categories);
    }

    @Operation(summary = "获取歌单详情", description = "根据歌单ID获取歌单的基本信息，不包括歌曲列表")
    @GetMapping("/detail/{id}")
    public Result<Playlist> getPlaylistDetail(@PathVariable Long id) {
        try {
            Playlist playlist = playlistService.getById(id);
            if (playlist == null) {
                return Result.error(404, "歌单不存在");
            }
            return Result.success("获取成功", playlist);
        } catch (Exception e) {
            return Result.error(500, "获取失败：" + e.getMessage());
        }
    }

    /**
     * 校验文件类型是否为支持的图片格式
     */
    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif")
        );
    }

    @Autowired
    private IUserPlaylistLikeService userPlaylistLikeService;
    
    /**
     * 用户收藏/取消收藏歌单
     */
    @PostMapping("/like/{playlistId}")
    @Operation(summary = "收藏/取消收藏歌单", description = "用户收藏或取消收藏指定歌单")
    @SecurityRequirement(name = "Authorization")
    public Result<Boolean> toggleLike(
            @Parameter(description = "歌单ID", required = true)
            @PathVariable Long playlistId) {
        try {
            Long userId = requestProcessor.getUserId();
            boolean isLiked = userPlaylistLikeService.toggleLike(userId, playlistId);
            return Result.success(isLiked ? "收藏成功" : "取消收藏成功", isLiked);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户是否收藏某个歌单
     */
    @GetMapping("/like/check/{playlistId}")
    @Operation(summary = "检查是否收藏歌单", description = "检查当前用户是否收藏指定歌单")
    @SecurityRequirement(name = "Authorization")
    public Result<Boolean> checkLike(
            @Parameter(description = "歌单ID", required = true)
            @PathVariable Long playlistId) {
        try {
            Long userId = requestProcessor.getUserId();
            boolean isLiked = userPlaylistLikeService.isLiked(userId, playlistId);
            return Result.success(isLiked);
        } catch (Exception e) {
            return Result.error(500, "查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户收藏的歌单列表
     */
    @GetMapping("/like/list")
    @Operation(summary = "获取用户收藏的歌单列表", description = "分页获取当前用户收藏的歌单列表")
    @SecurityRequirement(name = "Authorization")
    public Result<?> getUserLikedPlaylists(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Long size) {
        try {
            Long userId = requestProcessor.getUserId();
            return Result.success(userPlaylistLikeService.getUserLikedPlaylists(userId, current, size));
        } catch (Exception e) {
            return Result.error(500, "获取失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户收藏的歌单ID列表
     */
    @GetMapping("/like/ids")
    @Operation(summary = "获取用户收藏的歌单ID列表", description = "获取当前用户收藏的所有歌单ID")
    @SecurityRequirement(name = "Authorization")
    public Result<?> getUserLikedPlaylistIds() {
        try {
            Long userId = requestProcessor.getUserId();
            return Result.success(userPlaylistLikeService.getUserLikedPlaylistIds(userId));
        } catch (Exception e) {
            return Result.error(500, "获取失败：" + e.getMessage());
        }
    }
}
