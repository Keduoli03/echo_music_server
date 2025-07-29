package com.lanke.echomusic.controller;

import com.lanke.echomusic.common.Result;
import com.lanke.echomusic.dto.playlist.PlaylistDeleteDTO;
import com.lanke.echomusic.dto.playlist.PlaylistInfoDTO;
import com.lanke.echomusic.dto.playlist.PlaylistUpdateDTO;
import com.lanke.echomusic.entity.Playlist;
import com.lanke.echomusic.service.IPlaylistService;
import com.lanke.echomusic.utils.RequestProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 歌单表 前端控制器
 * </p>
 *
 * @author lanke
 * @since 2025-06-04
 */
@RestController
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
}
